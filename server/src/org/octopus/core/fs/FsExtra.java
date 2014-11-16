package org.octopus.core.fs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;

import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.MetaInfo;
import org.octopus.core.bean.VideoConvRequest;
import org.octopus.core.service.OctThumbnailService;
import org.octopus.core.service.OctVideoConvService;

/**
 * TODO 暂时放在这里, 将来把文件按照类型注册并实现对应的Extra功能
 * 
 * @author pw
 * 
 */
@IocBean
public class FsExtra {

    private Log log = Logs.get();

    @Inject
    private Dao dao;

    @Inject("refer:thumbnailService")
    private OctThumbnailService thumbnailService;

    @Inject("refer:videoConvService")
    private OctVideoConvService videoConvService;

    @Inject("java:$conf.getInt('thumbnail-width', 256)")
    protected int thumbWidth;

    @Inject("java:$conf.getInt('thumbnail-height', 256)")
    protected int thumbHeight;

    private String PREIVEW_VIDEO = "/fs/preview/video.jpg";

    /**
     * 生成meta信息
     * 
     * @param doc
     */
    public void makeMeta(Document doc) {
        File df = new File(FsPath.file(doc));
        MetaInfo mi = doc.metaInfo();
        if (doc.isBinary()) {
            if ("image".equals(doc.getCate())) {
                BufferedImage bimg = Images.read(df);
                mi.set("width", bimg.getWidth());
                mi.set("height", bimg.getHeight());
                mi.set("thumbWidth", thumbWidth);
                mi.set("thumbHeight", thumbHeight);
                mi.set("duration", 10); // 暂定10s
            } else if ("video".equals(doc.getCate())) {
                // 转换时填写对应的属性
                // TODO 是否要移到这个位置呢?
                mi.set("thumbWidth", thumbWidth);
                mi.set("thumbHeight", thumbHeight);
            }
        }
        if (doc.isTxt()) {
            if (mi.exist("line")) { // 行数
                LineNumberReader lnreader = null;
                try {
                    lnreader = new LineNumberReader(new FileReader(df));
                    lnreader.skip(df.length());
                    mi.set("line", lnreader.getLineNumber());
                }
                catch (Exception e) {
                    log.error(e);
                }
                finally {
                    Streams.safeClose(lnreader);
                }
            }
        }

        if (mi.isChanged()) {
            doc.setMeta(mi.toString());
            dao.update(doc, "meta");
        }
    }

    /**
     * 生成对应的preview文件
     * 
     * @param doc
     */
    public boolean makePreview(Document doc) {
        if (doc.isHasPreview()) {
            String srcPath = FsPath.file(doc);
            String tarPath = FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW) + "/preview.jpg";
            // 图片
            if ("image".equals(doc.getCate())) {
                // if("gif".equals(doc.getType())){
                // try {
                // Files.copyFile(new File(srcPath), new File(tarPath));
                // } catch (IOException e) {
                // e.printStackTrace();
                // }
                // }
                thumbnailService.createThumbnail(srcPath, tarPath, thumbWidth, thumbHeight);
            }
            // 视频
            else if ("video".equals(doc.getCate())) {
                Files.write(new File(tarPath), this.getClass().getResourceAsStream(PREIVEW_VIDEO));
            }
            // TODO 其他未实现呢
            else {
                throw Lang.noImplement();
            }
            return true;
        }
        return false;
    }

    /**
     * 复制preview, 一般用于一个文件进行手动转换的操作(视频或图片进行切割)
     * 
     * @param src
     * @param tar
     */
    public boolean copyPreview(Document src, Document tar) {
        if (src.isHasPreview() && tar.isHasPreview()) {

            File srcPreviewImage = new File(FsPath.fileExtra(src, FsPath.EXTRA_DIR_PREVIEW)
                                            + "/preview.jpg");
            File tarPreviewImage = new File(FsPath.fileExtra(tar, FsPath.EXTRA_DIR_PREVIEW)
                                            + "/preview.jpg");
            File srcPosterImage = new File(FsPath.fileExtra(src, FsPath.EXTRA_DIR_PREVIEW)
                                           + "/poster.jpg");
            File tarPosterImage = new File(FsPath.fileExtra(tar, FsPath.EXTRA_DIR_PREVIEW)
                                           + "/poster.jpg");

            File srcPreviewVideo = new File(FsPath.fileExtra(src, FsPath.EXTRA_DIR_PREVIEW)
                                            + "/preview.mp4");
            File tarPreviewVideo = new File(FsPath.fileExtra(tar, FsPath.EXTRA_DIR_PREVIEW)
                                            + "/preview.mp4");
            try {
                // 图片
                if ("image".equals(tar.getCate())) {
                    Files.copyFile(srcPreviewImage, tarPreviewImage);

                }
                // 视频
                else if ("video".equals(tar.getCate())) {
                    Files.copyFile(srcPreviewImage, tarPreviewImage);
                    Files.copyFile(srcPreviewVideo, tarPreviewVideo);
                    Files.copyFile(srcPosterImage, tarPosterImage);
                }
                // TODO 其他未实现呢
                else {
                    throw Lang.noImplement();
                }
                return true;
            }
            catch (IOException e) {
                log.error(e);
            }
        }
        return false;
    }

    /**
     * 进行转换
     * 
     * @param doc
     */
    public void makeTrans(Document doc, TransInfo tinfo) {
        if (tinfo == null) {
            return;
        }
        // 没有转换过
        if (doc.isHasTrans() && !doc.isTransDone()) {
            if ("video".equals(doc.getCate())) {
                VideoConvRequest vcRequest = new VideoConvRequest();
                vcRequest.setDocId(doc.getId());
                vcRequest.setDocName(doc.getName());
                vcRequest.setCreateTime(new Date());
                vcRequest.setCreateThumb(tinfo.isHasThumb());
                vcRequest.setCreatePreview(tinfo.isHasPreview());
                vcRequest.setCreateTrans(tinfo.isHasTrans());
                vcRequest.setTransCutAs(String.format("%dx%d", tinfo.getCutX(), tinfo.getCutY()));
                dao.insert(vcRequest);
            }
        }
    }
}
