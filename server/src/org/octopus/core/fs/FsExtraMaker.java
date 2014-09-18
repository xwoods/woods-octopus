package org.octopus.core.fs;

import java.io.File;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.octopus.core.bean.Document;

@IocBean
public class FsExtraMaker {

    @Inject("refer:dao")
    protected Dao dao;

    private String PREIVEW_IMAGE = "/fs/preview/image.jpg";
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
                Files.write(pi, FsExtraMaker.class.getResourceAsStream(PREIVEW_IMAGE));
                // TODO 生成缩略图
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
