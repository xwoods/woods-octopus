package org.octopus.core.fs;

import java.io.File;
import java.io.IOException;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.core.bean.Document;
import org.octopus.core.service.OctThumbnailService;
import org.octopus.core.service.OctVideoConvService;

@IocBean
public class FsExtra {

    private Log log = Logs.get();

    @Inject
    private OctThumbnailService thumbnailService;

    @Inject
    private OctVideoConvService videoConvService;

    @Inject("java:$conf.getInt('thumbnail-width', 256)")
    protected int thumbnailWidth;

    @Inject("java:$conf.getInt('thumbnail-height', 256)")
    protected int thumbnailHeight;

    private String PREIVEW_VIDEO = "/fs/preview/video.jpg";

    /**
     * 生成meta信息
     * 
     * @param doc
     */
    public void makeMeta(Document doc) {

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
                thumbnailService.createThumbnail(srcPath, tarPath, thumbnailWidth, thumbnailHeight);
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
            tinfo = new TransInfo();
            tinfo.setCutX(1);
            tinfo.setCutY(1);
            // TODO 其他属性...
        }

    }
}
