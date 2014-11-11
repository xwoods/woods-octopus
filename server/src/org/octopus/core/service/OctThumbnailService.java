package org.octopus.core.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.nutz.img.Images;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class OctThumbnailService {

    private Log log = Logs.get();

    public boolean createThumbnail(String srcPath, String tarPath, int width, int height) {
        if (srcPath.toLowerCase().endsWith(".gif")) {
            try {
                Files.copyFile(new File(srcPath), new File(tarPath));
            }
            catch (IOException e) {
                log.error(e);
                return false;
            }
        } else {
            try {
                BufferedImage img = Images.read(Streams.fileIn(srcPath));
                BufferedImage thumb = Images.zoomScale(img, width, height, null);
                Images.write(thumb, Files.createFileIfNoExists(tarPath));
            }
            catch (IOException e) {
                log.error(e);
                return false;
            }
        }
        return true;
    }
}
