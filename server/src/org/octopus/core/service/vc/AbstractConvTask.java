package org.octopus.core.service.vc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.video.h264.Mp4Box;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.MetaInfo;

import com.danoo.videox.ConvTask;
import com.danoo.videox.VideoConvException;
import com.danoo.videox.bean.VideoInfo;
import com.danoo.videox.bean.VideoxDest;
import com.danoo.videox.bean.VideoxTask;

public abstract class AbstractConvTask implements ConvTask {

    protected Dao dao;
    protected Document doc;
    public static final Log log = Logs.get();
    public static final int cores = Runtime.getRuntime().availableProcessors();
    public static final boolean KEEY_SHELL = true;

    public AbstractConvTask(Dao dao, Document doc) {
        this.dao = dao;
        this.doc = doc;
    }

    public abstract VideoInfo readInfo(VideoxTask task);

    public abstract String previewShell(VideoxTask task, VideoInfo vinfo);

    public abstract int[] previewSize(VideoInfo vinfo);

    public abstract String thumbShell(VideoxTask task, VideoInfo vinfo);

    public abstract String[] mainShell(VideoxTask task, VideoInfo vinfo);

    public void runTask(VideoxTask task) {
        String srcPath = task.getSrc();
        log.debugf("ConvertVideo, Type: %s, Name: %s(%s)", doc.getType(), doc.getName(), srcPath);
        // 读取视频信息
        VideoInfo srcInfo = readInfo(task);
        if (srcInfo == null) {
            throw new VideoConvException("Bad Video" + srcPath);
        }

        if (srcInfo.getLength() < 1) {
            throw new VideoConvException("Video Length too Short < 1s?! " + srcPath);
        }
        if (srcInfo.getLength() > 3600 * 24) {
            throw new VideoConvException("Video Length too Large > 24h?! " + srcPath);
        }
        task.setupVideoInfo(srcInfo);

        MetaInfo mi = doc.meta();
        mi.set("transCutX", task.getCutX());
        mi.set("transCutY", task.getCutY());
        mi.set("transCutWidth", task.getCutWidth());
        mi.set("transCutHeight", task.getCutHeight());

        // Preview
        if (task.getPreview() != null) {
            log.debugf("ConvertVideo, Preview: %s", task.getPreview());
            String shell = previewShell(task, srcInfo);
            try {
                runShell(shell);
            }
            catch (Throwable e) {
                throw new VideoConvException("Make Preview Fail " + srcPath, e);
            }
            log.debugf("ConvertVideo, Done Preview: %s", srcPath);
            int[] psize = previewSize(srcInfo);
            mi.set("previewWidth", psize[0]);
            mi.set("previewHeight", psize[1]);
        }

        // Thumbnail
        if (task.getThumb() != null) {
            log.debugf("ConvertVideo, Thumbnail: %s", task.getThumb());
            String shell = thumbShell(task, srcInfo);
            try {
                runShell(shell);
            }
            catch (Throwable e) {
                throw new VideoConvException("Make Thumb Fail " + srcPath, e);
            }
            log.debugf("ConvertVideo, Done Thumbnail: %s", srcPath);
        }

        // Trans
        if (task.getDest() != null) {
            log.debugf("ConvertVideo, CutBy %s, Trans: %s, ", task.getCutAs(), task.getDest());
            String[] mainShells = mainShell(task, srcInfo);
            try {
                if (mainShells.length == 1) {
                    doc.setTransRate(50);
                    dao.update(doc, "transRate");
                }
                for (int i = 0; i < mainShells.length; i++) {
                    log.debugf("ConvertVideo, TransPercent: %d / %d", i, mainShells.length);
                    runShell(mainShells[i]);
                    // 完成转换
                    doc.setTransRate(i * 100 / mainShells.length);
                    dao.update(doc, "transRate");
                }
                log.debugf("ConvertVideo, Done Trans: %s", srcPath);
                // 获取转换后视频的信息
                File simpleVideoFile = new File(task.getDest());
                if (simpleVideoFile.isDirectory())
                    simpleVideoFile = new File(task.getDest(), "1_1.mp4");
                if (!isVaild(simpleVideoFile.getAbsolutePath())) {
                    throw new VideoConvException("Convent Fail " + task.getSrc());
                }
                VideoInfo vinfo = readVideoInfo(simpleVideoFile.getAbsolutePath());
                vinfo.setFrameRate(24);
                InputStream in = null;
                try {
                    in = new FileInputStream(simpleVideoFile);
                    Mp4Box mp4 = new Mp4Box();
                    mp4.load(in);
                    vinfo.setFrameCount(mp4.getChunkOffsets().length);
                }
                catch (Exception e) {
                    throw new VideoConvException("BAD Video", e);
                }
                finally {
                    Streams.safeClose(in);
                }

                if (task.getDestinfo() == null)
                    return;

                VideoxDest destinfo = new VideoxDest(simpleVideoFile.getAbsolutePath(),
                                                     Times.sDT(Times.D(simpleVideoFile.lastModified())),
                                                     vinfo);
                Json.toJsonFile(new File(task.getDestinfo()), destinfo);
                log.debugf("ConvertVideo, Done DestInfo: %s", srcPath);
                mi.set("duration", (int) destinfo.getH264info().getLength());
            }
            catch (Throwable e) {
                throw new VideoConvException("Main Convent Fail " + task.getSrc(), e);
            }
        }
        if (mi.isChanged()) {
            doc.setMeta(mi.toString());
        }
        doc.setTransDone(true);
        dao.update(doc, "transDone|transRate|meta");
    }

    public VideoInfo readVideoInfo(String path) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"read_info.sh", path, "ALL"});
            Reader r = new InputStreamReader(p.getInputStream());
            return Json.fromJson(VideoInfo.class, r);
        }
        catch (IOException e) {}

        return null;
    }

    public VideoInfo readSwfInfo(String path) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"paser_swf", path, "json"});
            Reader r = new InputStreamReader(p.getInputStream());
            VideoInfo vinfo = Json.fromJson(VideoInfo.class, r);

            vinfo.setLength(vinfo.getFrameCount() - vinfo.getFrameRate());
            log.debug("Swf Source Info\n" + Json.toJson(vinfo));
            return vinfo;
        }
        catch (IOException e) {}

        return null;
    }

    public void runShell(String shell) throws VideoConvException, IOException, InterruptedException {
        File f = File.createTempFile("videox", ".sh");
        Files.write(f, "#!/bin/bash\n" + shell.replaceAll("\r\n", "\n"));
        Runtime runtime = Runtime.getRuntime();

        try {
            log.debugf("ConvertVideo, RunShell: %s", f.getAbsolutePath());
            if (!f.setExecutable(true))
                throw new VideoConvException("Fail to set conv shell as executable!");
            final Process p = runtime.exec(new String[]{"nohup", f.getAbsolutePath()});
            new Thread() {
                public void run() {
                    try {
                        Streams.readAndClose(new InputStreamReader(p.getInputStream()));
                    }
                    catch (Throwable e) {}
                };
            }.start();
            new Thread() {
                public void run() {
                    try {
                        Streams.readAndClose(new InputStreamReader(p.getErrorStream()));
                    }
                    catch (Throwable e) {}
                };
            }.start();
            int re = p.waitFor();
            if (re != 0) {
                throw new VideoConvException("Exec FAIL");
            }
        }
        finally {
            if (!KEEY_SHELL)// for debug
                f.delete();
        }
    }

    public boolean isVaild(String path) {
        File f = new File(path);
        return f.isFile() && f.exists() && f.length() > 0;
    }

}
