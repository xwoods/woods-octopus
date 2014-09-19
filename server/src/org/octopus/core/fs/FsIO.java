package org.octopus.core.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
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
import org.nutz.lang.stream.NullInputStream;
import org.nutz.log.Log;
import org.nutz.log.Logs;
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

    @Inject("refer:dao")
    protected Dao dao;

    @Inject("refer:fsExtraMaker")
    protected FsExtraMaker extraMaker;

    private Cnd fsCnd(Document doc) {
        return Cnd.where("module", "=", doc.getModule()).and("define", "=", doc.getDefine());
    }

    /**
     * @param doc
     *            文档
     * @return 在当前目录下是否有重名的
     */
    public boolean hasSameName(Document doc) {
        int sn = dao.count(Document.class,
                           fsCnd(doc).and("name", "=", doc.getName()).and("parentId",
                                                                          "=",
                                                                          doc.getParentId()));
        return sn > 0;
    }

    /**
     * @param doc
     * @return 在发现同名文件的时候, 调用该方法, 返回一个有效的文件名称
     * 
     */
    public String notSameName(Document doc) {
        String cnm = doc.getName();
        int i = 0;
        boolean found = false;
        while (!found) {
            String nnm = cnm + "_" + i++;
            doc.setName(nnm);
            found = hasSameName(doc);
        }
        return doc.getName();
    }

    /**
     * 生成一个新文件
     * 
     * @param doc
     * @return 文件保存在磁盘上的真实路径
     */
    public void make(Document doc) {
        // 检查文件是否重名
        if (hasSameName(doc)) {
            notSameName(doc);
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
        // 逻辑上的文件夹, 不需要生成文件或目录
        if (doc.isDir()) {
            return;
        }
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
            if (doc.isHasTrans()) {
                Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_TRANS));
            }
            if (doc.isHasPreview()) {
                Files.createDirIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_PREVIEW));
                extraMaker.makePreview(doc);
            }
            if (doc.isHasInfo()) {
                Files.createFileIfNoExists(FsPath.fileExtra(doc, FsPath.EXTRA_INFO));
            }
        }
        catch (IOException e) {
            log.error(e);
            throw Lang.wrapThrow(e);
        }
    }

    public boolean writeText(Document doc, InputStream ins) {
        try {
            Reader reader = new InputStreamReader(ins, "UTF-8");
            String text = Streams.readAndClose(reader);
            if (null == text)
                text = "";
            // 读取旧数据
            String old = readText(doc);
            // 如果不同，写入数据
            if (null == old || !old.equals(text)) {
                File f = Files.createFileIfNoExists(FsPath.file(doc));
                // 磁盘剩余空间少于1GB,不允许写文件
                if (f.getFreeSpace() < WRITE_LIMIT) {
                    throw new RuntimeException("Write Text, Free Space is too small!");
                }
                Files.write(f, text);
                // 更新文件信息
                doc.setSize(f.length());
                doc.setModifyTime(new Date());
                dao.update(doc, "size|modifyTime|modifyUser");
                return true;
            }
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        return false;
    }

    public boolean writeBinary(Document doc, InputStream ins) {

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
