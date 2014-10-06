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

    public Document fetch(String module, String define, String fnm) {
        Document parent = new Document();
        parent.setModule(module);
        parent.setDefine(define);
        parent.setId(define); // define就是parentId
        return fetch(parent, fnm);
    }

    public Document fetch(Document parent, String fnm) {
        Document doc = dao.fetch(Document.class, fsCnd(parent).and("parentId", "=", parent.getId())
                                                              .and("name", "=", fnm));
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
                if (doc.isHasInfo()) {
                    Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_INFO));
                }
                if (doc.isHasTrans()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_TRANS));
                }
                if (doc.isHasPreview()) {
                    Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_PREVIEW));
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
                doc.setSize(f.length());
                doc.setModifyTime(new Date());
                doc.setModifyUser(mdUser);
                dao.update(doc, "size|modifyTime|modifyUser");
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
                doc.setSize(f.length());
                doc.setModifyTime(new Date());
                doc.setModifyUser(mdUser);
                dao.update(doc, "size|modifyTime|modifyUser");
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

}
