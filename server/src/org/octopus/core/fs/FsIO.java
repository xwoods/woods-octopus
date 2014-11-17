package org.octopus.core.fs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.stream.NullInputStream;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.web.Webs.Err;
import org.octopus.OctopusErr;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.DocumentType;
import org.octopus.core.bean.MetaInfo;

/**
 * 
 * 文件的相关操作
 * 
 * @author pw
 * 
 */
@IocBean
public class FsIO {

    private Log log = Logs.get();

    /**
     * 写文件, 空间限制
     */
    private long WRITE_LIMIT = 1024 * 1024 * 1024;

    /**
     * 读文件, 大小限制
     */
    private long READ_LIMIT = 1024 * 1024 * 100;

    /**
     * 做IO操作, 缓存大小
     */
    private int IO_BUFFER_SIZE = 8192;

    @Inject("refer:dao")
    protected Dao dao;

    @Inject("refer:fsExtra")
    protected FsExtra fsExtra;

    private Cnd fsCnd(Document doc) {
        return Cnd.where("module", "=", doc.getModule()).and("define", "=", doc.getDefine());
    }

    public boolean existDocument(String module, String define, String parentId, String fnm) {
        Document chkdoc = new Document();
        chkdoc.setModule(module);
        chkdoc.setDefine(define);
        chkdoc.setParentId(parentId);
        chkdoc.setName(fnm);
        return existDocument(chkdoc);
    }

    /**
     * @param doc
     *            文档
     * @return 在当前目录下是否有重名的
     */
    public boolean existDocument(Document doc) {
        int sn = dao.count(Document.class,
                           fsCnd(doc).and("parentId", "=", doc.getParentId()).and("name",
                                                                                  "=",
                                                                                  doc.getName()));
        return sn > 0;
    }

    /**
     * @param doc
     * @return 在发现同名文件的时候, 调用该方法, 返回一个有效的文件名称
     * 
     */
    public void setNewName(Document doc) {
        String cnm = doc.getName();
        int i = 1;
        boolean same = true;
        while (same) {
            String nnm = cnm + "(" + i++ + ")";
            doc.setName(nnm);
            same = existDocument(doc);
        }
    }

    public Document fetch(String id) {
        return dao.fetch(Document.class, id);
    }

    public Document fetch(String module, String define, String fnm, String type) {
        Document parent = new Document();
        parent.setModule(module);
        parent.setDefine(define);
        parent.setId(define); // define就是parentId
        return fetch(parent, fnm, type);
    }

    public Document fetch(Document parent, String fnm, String type) {
        Cnd qcnd = fsCnd(parent).and("parentId", "=", parent.getId()).and("name", "=", fnm);
        if (!Strings.isBlank(type)) {
            qcnd.and("type", "=", type);
        }
        Document doc = dao.fetch(Document.class, qcnd);
        return doc;
    }

    public Document make(String module,
                         String define,
                         String fnm,
                         String type,
                         boolean isPrivate,
                         String ctUser) {
        Document parent = new Document();
        parent.setModule(module);
        parent.setDefine(define);
        parent.setId(define); // define就是parentId
        return make(parent, fnm, type, isPrivate, ctUser);
    }

    /**
     * 生成一个新文件对象
     * 
     * @param parent
     *            父节点
     * @param fnm
     *            文件名称
     * @param type
     *            文件类型
     * @param isPrivate
     *            是否私有
     * @param ctUser
     *            建立者
     * @return 文件对象
     */
    public Document make(Document parent, String fnm, String type, boolean isPrivate, String ctUser) {
        String fnmM = null;
        String fnmS = null;
        if (Strings.isBlank(type)) { // 查找文件类型
            fnmM = Files.getMajorName(fnm);
            fnmS = Files.getSuffixName(fnm).toLowerCase();
        } else {
            fnmM = fnm;
            fnmS = type;
        }
        Document doc = new Document();
        doc.setCreateTime(new Date());
        doc.setCreateUser(ctUser);
        doc.setParentId(parent.getId());
        doc.setModule(parent.getModule());
        doc.setDefine(parent.getDefine());
        doc.setName(fnmM);
        doc.setType(fnmS);
        // 是否私有看模块规定
        doc.setPrivate(isPrivate);
        // 默认可读
        doc.setCanRead(true);
        doc.setCanWrite(false);
        doc.setCanRemove(false);
        // 检查文件是否重名
        if (existDocument(doc)) {
            setNewName(doc);
        }
        // 根据文件后缀, 补全文件相关属性
        DocumentType docTp = FsSetting.typeMap.get(doc.getType());
        doc.setHasPreview(docTp.isHasPreview());
        doc.setHasInfo(docTp.isHasInfo());
        doc.setHasTrans(docTp.isHasTrans());
        doc.setMime(docTp.getMime());
        doc.setReadAs(docTp.getReadAs());
        doc.setCate(FsSetting.type2Cate.get(doc.getType()));
        doc.setMeta(Json.toJson(FsSetting.type2metaMap.get(doc.getType()), JsonFormat.compact()));
        // 插入数据库
        dao.insert(doc);
        // 逻辑上的文件夹, 不需要生成文件或目录
        if (!doc.isDir()) {
            // 生成对应文件跟目录
            try {

                // 复合型文件, 生成目录
                if (doc.isComplex()) {
                    Files.createDirIfNoExists(FsPath.file(doc));
                }
                // BIN或TXT, 生成文件
                else {
                    Files.createFileIfNoExists(FsPath.file(doc));
                }

                // 其他相关文件与目录
                // info
                if (doc.isHasInfo()) {
                    Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_INFO));
                }
                // preview
                if (doc.isHasPreview()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW));
                }
                // trans
                if (doc.isHasTrans()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_TRANS));
                    Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_TRANSINFO));
                }
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        }
        return doc;
    }

    /**
     * 以文本的方式写入文件
     * 
     * @param doc
     *            文档对象
     * @param ins
     *            新的文件流
     * @param mdUser
     *            修改者
     * @return 是否写入成功
     */
    public boolean writeText(Document doc, InputStream ins, String mdUser) {
        try {
            Reader reader = new InputStreamReader(ins, "UTF-8");
            String text = Streams.readAndClose(reader);
            if (null == text)
                text = "";
            File f = Files.createFileIfNoExists(FsPath.file(doc));
            if (f.getFreeSpace() < WRITE_LIMIT) {
                throw OctopusErr.NEED_MORE_DISK_SPACE();
            }
            // 读取旧数据
            String old = readText(doc);
            // 如果不同，写入数据
            if (null == old || !old.equals(text)) {
                if (log.isDebugEnabled()) {
                    log.debug("New Text, Rewrite " + doc);
                }
                Files.write(f, text);
                // 更新文件信息
                afterWrite(f, mdUser, doc);
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Same Text, Ignore Rewrite " + doc);
                }
            }
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        return false;
    }

    /**
     * 以二进制方式写入文件
     * 
     * @param doc
     *            文档对象
     * @param ins
     *            新的文件流
     * @param mdUser
     *            修改者
     * @return 是否写入成功
     */
    public boolean writeBinary(Document doc, InputStream ins, String mdUser) {
        File tmpf = null;
        try {
            // 这个是旧文件
            File f = Files.createFileIfNoExists(FsPath.file(doc));
            // 空间不足
            if (f.getFreeSpace() < WRITE_LIMIT) {
                throw OctopusErr.NEED_MORE_DISK_SPACE();
            }
            // 创建临时文件
            tmpf = Files.createFileIfNoExists(f.getAbsolutePath()
                                              + "."
                                              + System.nanoTime()
                                              + R.random(0, 100000)
                                              + ".tmp");
            // 将内容写入临时文件
            BufferedOutputStream ops = new BufferedOutputStream(Streams.fileOut(tmpf),
                                                                IO_BUFFER_SIZE);
            Streams.writeAndClose(ops, ins);
            long sz = tmpf.length();
            if (sz != f.length()
                || !Streams.equals(Streams.buff(Streams.fileIn(f)),
                                   Streams.buff(Streams.fileIn(tmpf)))) {
                if (log.isDebugEnabled()) {
                    log.debug("New Binary, Rewrite " + doc);
                }
                // 替换文件
                Files.deleteFile(f);
                Files.move(tmpf, f);
                // 更新文件信息
                afterWrite(f, mdUser, doc);
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Same Binary, Ignore Rewrite " + doc);
                }
            }
        }
        catch (IOException e) {
            throw Err.wrap(e);
        }
        finally {
            if (null != tmpf)
                Files.deleteFile(tmpf);
        }
        return false;
    }

    private void afterWrite(File f, String mdUser, Document doc) {
        doc.setSha1(Lang.sha1(f));
        doc.setSize(f.length());
        doc.setModifyTime(new Date());
        doc.setModifyUser(mdUser);
        dao.update(doc, "size|modifyTime|modifyUser|sha1");
        fsExtra.makeMeta(doc);
        if (doc.isHasPreview()) {
            fsExtra.makePreview(doc);
        }
        // FIXME 暂时仅仅支持视频的转换
        // 如果是视频的话
        if ("video".equals(doc.getCate()) && doc.isHasTrans()) {
            TransInfo tinfo = new TransInfo();
            tinfo.setCutX(1);
            tinfo.setCutY(1);
            tinfo.setHasThumb(true);
            tinfo.setHasPreview(true);
            tinfo.setHasTrans(true);
            fsExtra.makeTrans(doc, tinfo);
        }
    }

    /**
     * 按照文本文件进行读取
     * 
     * @param doc
     * @return
     */
    public String readText(Document doc) {
        File f = new File(FsPath.file(doc));
        if (!f.exists()) {
            return "";
        }
        if (f.length() == 0) {
            return "";
        }
        // 检查文件大小,如果超过100mb,则马上警告
        if (f.length() > READ_LIMIT)
            if (log.isWarnEnabled())
                log.warnf("Read Text, Size=%s byte!! Very Big?!! DocId=%s", f.length(), doc.getId());
        return Files.read(f);
    }

    /**
     * 按照json格式读取, 返回对象
     * 
     * @param doc
     * @param clz
     * @return
     */
    public <T> T readJson(Document doc, Class<T> clz) {
        String dtxt = readText(doc);
        return Json.fromJson(clz, dtxt);
    }

    /**
     * 按照二进制文件进行读取
     * 
     * @param doc
     * @return
     */
    public InputStream readBinary(Document doc) {
        File f = new File(FsPath.file(doc));
        if (!f.exists())
            return new NullInputStream();
        try {
            return new FileInputStream(f);
        }
        catch (FileNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 按照复杂文件读取, 返回子文件列表
     * 
     * @param doc
     * @return
     */
    public List<String> readComplex(Document doc) {
        File f = new File(FsPath.file(doc));
        if (!f.exists())
            return null;
        if (f.isDirectory()) {
            return Arrays.asList(f.list());
        }
        throw new RuntimeException(String.format("Read Complex, But DocId[%s] Is Not Dir!",
                                                 doc.getId()));
    }

    /**
     * 获取root目录下的子文件
     * 
     * @param root
     * @param type
     * @param deep
     * @return
     */
    public List<Document> children(Document root, String type, boolean deep) {
        List<Document> chList = null;
        if (root.isDir()) {
            Cnd cnd = fsCnd(root).and("parentId", "=", root.getId());
            if (!Strings.isBlank(type)) {
                cnd.and("type", "=", type);
            }
            chList = dao.query(Document.class, cnd);
            // 查询更多的子文件
            if (deep) {
                for (Document ch : chList) {
                    if (ch.isDir()) {
                        chList.addAll(children(ch, type, deep));
                    }
                }
            }
        }
        return chList;
    }

    /**
     * 依次遍历访问子文件
     * 
     * @param root
     * @param type
     * @param eachVisit
     */
    public void visitChildren(Document root, String type, boolean deep, Each<Document> eachVisit) {
        // TODO
        throw Lang.noImplement();
    }

    /**
     * 删除文档
     * 
     * @param docId
     */
    public void delete(String docId) {
        Document doc = fetch(docId);
        delete(doc);
    }

    /**
     * 删除文档
     * 
     * @param docId
     */
    public void delete(Document doc) {
        if (doc.isDir()) {
            // 删除子节点
            List<Document> dList = children(doc, null, true);
            for (Document dc : dList) {
                deleteDocument(dc);
            }
        }
        // 删自己
        deleteDocument(doc);
    }

    /**
     * 删除文档
     * 
     * @param docId
     */
    private void deleteDocument(Document doc) {
        // 直接删除
        _delete(FsPath.file(doc));
        if (doc.isHasInfo()) {
            _delete(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_INFO));
        }
        if (doc.isHasPreview()) {
            _delete(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW));
        }
        if (doc.isHasTrans()) {
            _delete(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_TRANS));
            _delete(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_TRANSINFO));
        }
        dao.delete(doc);
    }

    private boolean _delete(String path) {
        File df = new File(path);
        if (df.exists()) {
            try {
                return df.isDirectory() ? Files.deleteDir(df) : Files.deleteFile(df);
            }
            catch (Exception e) {
                log.error(e);
            }
        }
        return false;
    }

    /**
     * 复制一个当前的对象
     * 
     * @param oldDoc
     * 
     * @return
     */
    public Document copy(Document oldDoc, String ctUser, boolean withOutTrans) {
        // 新建doc
        Document doc = new Document();
        doc.setCreateTime(new Date());
        doc.setCreateUser(ctUser);
        doc.setModifyTime(new Date());
        doc.setModifyUser(ctUser);
        doc.setSha1(oldDoc.getSha1());
        doc.setSize(oldDoc.getSize());
        doc.setParentId(oldDoc.getParentId());
        doc.setModule(oldDoc.getModule());
        doc.setDefine(oldDoc.getDefine());
        doc.setName(oldDoc.getName());
        doc.setType(oldDoc.getType());
        // 是否私有看模块规定
        doc.setPrivate(oldDoc.isPrivate());
        // 默认可读
        doc.setCanRead(oldDoc.isCanRead());
        doc.setCanWrite(oldDoc.isCanWrite());
        doc.setCanRemove(oldDoc.isCanRemove());
        // 检查文件是否重名
        if (existDocument(doc)) {
            setNewName(doc);
        }
        // 根据文件后缀, 补全文件相关属性
        doc.setHasPreview(oldDoc.isHasPreview());
        doc.setHasInfo(oldDoc.isHasInfo());
        doc.setHasTrans(oldDoc.isHasTrans());
        doc.setMime(oldDoc.getMime());
        doc.setReadAs(oldDoc.getReadAs());
        doc.setCate(oldDoc.getCate());
        doc.setTransFail(oldDoc.isTransFail());
        doc.setTransDone(oldDoc.isTransDone());
        doc.setTransRate(oldDoc.getTransRate());
        if (withOutTrans && doc.isHasTrans()) {
            doc.setTransFail(false);
            doc.setTransDone(false);
            doc.setTransRate(0);
            MetaInfo mi = oldDoc.metaInfo();
            mi.set("transCutX", 0);
            mi.set("transCutY", 0);
            mi.set("transCutWidth", 0);
            mi.set("transCutHeight", 0);
            oldDoc.setMeta(mi.toString());
        }
        doc.setMeta(oldDoc.getMeta());
        // 插入数据库
        dao.insert(doc);
        // 逻辑上的文件夹, 不需要生成文件或目录
        if (!doc.isDir()) {
            // 生成对应文件跟目录
            try {
                // 复合型文件, 生成目录
                if (doc.isComplex()) {
                    Files.createDirIfNoExists(FsPath.file(doc));
                    Files.copyDir(new File(FsPath.file(oldDoc)), new File(FsPath.file(doc)));
                }
                // BIN或TXT, 生成文件
                else {
                    Files.createFileIfNoExists(FsPath.file(doc));
                    Files.copyFile(new File(FsPath.file(oldDoc)), new File(FsPath.file(doc)));
                }

                // 其他相关文件与目录
                // info
                if (doc.isHasInfo()) {
                    Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_INFO));
                    Files.copyFile(new File(FsPath.fileExtra(oldDoc, FsPath.EXTRA_FILE_INFO)),
                                   new File(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_INFO)));
                }
                // preview
                if (doc.isHasPreview()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW));
                    Files.copyDir(new File(FsPath.fileExtra(oldDoc, FsPath.EXTRA_DIR_PREVIEW)),
                                  new File(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW)));
                }
                // trans
                if (doc.isHasTrans()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_TRANS));
                    Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_TRANSINFO));
                    if (!withOutTrans) {
                        Files.copyDir(new File(FsPath.fileExtra(oldDoc, FsPath.EXTRA_DIR_TRANS)),
                                      new File(FsPath.fileExtra(doc, FsPath.EXTRA_DIR_TRANS)));
                        Files.copyFile(new File(FsPath.fileExtra(oldDoc,
                                                                 FsPath.EXTRA_FILE_TRANSINFO)),
                                       new File(FsPath.fileExtra(doc, FsPath.EXTRA_FILE_TRANSINFO)));
                    }
                }
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
        }
        return doc;
    }
}
