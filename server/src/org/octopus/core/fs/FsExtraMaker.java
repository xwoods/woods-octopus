package org.octopus.core.fs;

import java.awt.image.BufferedImage;
import java.io.File;

import org.nutz.dao.Dao;
import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.octopus.core.bean.Document;

@IocBean
public class FsExtraMaker {

    @Inject("refer:dao")
    protected Dao dao;

    // private String PREIVEW_IMAGE = "/fs/preview/image.jpg";
    private String PREIVEW_VIDEO = "/fs/preview/video.jpg";

    /**
     * 生成对应的preview文件
     * 
     * @param doc
     */
    public void makePreview(Document doc) {
        if (doc.isHasPreview()) {
            // 图片
            if ("image".equals(doc.getCate())) {
                File pi = new File(FsPath.fileExtra(doc, "preview"), "preview.jpg");
                if ("gif".equals(doc.getType())) {
                    Files.copy(new File(FsPath.file(doc)), pi);
                } else {
                    BufferedImage im = Images.read(Streams.fileIn(FsPath.file(doc)));
                    BufferedImage im2 = Images.zoomScale(im, 256, 256, null);
                    Images.write(im2, pi);
                }
            }
            // 视频
            if ("video".equals(doc.getCate())) {
                File pv = new File(FsPath.fileExtra(doc, "preview"), "preview.jpg");
                Files.write(pv, FsExtraMaker.class.getResourceAsStream(PREIVEW_VIDEO));
                // TODO 视频转换服务
            }
        }
    }
}
