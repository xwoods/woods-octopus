package org.octopus.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.Times;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.video.h264.Mp4Box;
import org.octopus.core.bean.Document;
import org.octopus.core.fs.FsIO;

import com.danoo.videox.ConvTask;
import com.danoo.videox.VideoConvException;
import com.danoo.videox.bean.VideoInfo;
import com.danoo.videox.bean.VideoxDest;
import com.danoo.videox.bean.VideoxTask;

/**
 * 修改来自 SimpleVideoTask
 * 
 * @author pw
 * 
 */
public class OctVideoConvTask implements ConvTask {

    public static int cores = Runtime.getRuntime().availableProcessors();

    private static final Log log = Logs.get();

    private static final boolean KEEY_SHELL = true;

    private Dao dao;

    private Document transDoc;

    private FsIO fsIO;

    public OctVideoConvTask() {}

    public VideoInfo readInfo(VideoxTask task) {
        String srcPath = getRealPath(task.getSrc());
        return readVideoInfo(srcPath);
    }

    public String previewShell(VideoxTask task, VideoInfo info) {

        // 源视频的1/4
        int previewWidth = (info.getWidth() + 1) / 2;
        int previewHeight = (info.getHeight() + 1) / 2;

        if (previewHeight > 1000 || previewWidth > 1000) {
            log.debug("Preview Size too big : " + previewWidth + "x" + previewHeight);
            previewWidth = previewWidth / 2;
            previewHeight = previewHeight / 2;
        }

        if (previewWidth % 2 != 0) {
            previewWidth++;
        }
        if (previewHeight % 2 != 0) {
            previewHeight++;
        }

        log.debug("Preview Size : " + previewWidth + "x" + previewHeight);

        Context ctx = Lang.context();
        ctx.set("srcPath", getRealPath(task.getSrc()));
        ctx.set("previewPath", getRealPath(task.getPreview()));
        ctx.set("previewWidth", previewWidth);
        ctx.set("previewHeight", previewHeight);
        ctx.set("ffmpeg_thread", "" + cores);
        ctx.set("bitrate", task.getPreviewBitRate() + "k");

        log.debug("Preview Shell render Ctx:\n" + Json.toJson(ctx));

        CharSegment tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("preview.tpl"))));
        return tpl.render(ctx).toString();
    }

    public String thumbShell(VideoxTask task, VideoInfo info) {
        // 源视频的1/4
        int previewWidth = (info.getWidth() + 1) / 2;
        int previewHeight = (info.getHeight() + 1) / 2;
        Context ctx = Lang.context();

        ctx.set("srcPath", getRealPath(task.getSrc()));
        ctx.set("thumbPath", getRealPath(task.getThumb()));
        if (task.getThumb_sz() != null) {
            ctx.set("thumbSize", task.getThumb_sz());
        } else {
            ctx.set("thumbSize", "" + previewWidth + "x" + previewHeight);
        }

        CharSegment tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("thumb.tpl"))));
        return tpl.render(ctx).toString();
    }

    public String[] mainShell(VideoxTask task, VideoInfo vinfo) {
        Context ctx = Lang.context();
        ctx.set("ffmpeg_thread", "" + cores);
        ctx.set("srcPath", getRealPath(task.getSrc()));
        ctx.set("bitrate", task.getBitRate() + "k");

        CharSegment tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("main.tpl"))));
        log.debugf("cut: x=%d y=%d", task.getCutX(), task.getCutY());
        if (task.getCutX() == 1 && task.getCutY() == 1) {
            String dst = "";
            if (task.getDest().endsWith(".mp4")) {
                dst = getRealPath(task.getDest());
            } else {
                dst = getRealPath(task.getDest() + "/1_1.mp4");
            }
            ctx.set("other", "-vcodec libx264 " + dst);
            return new String[]{tpl.render(ctx).toString()};
        }

        List<String> shells = new ArrayList<String>();
        List<String> mp4s = new ArrayList<String>();
        // 如果是无缩放的,那就是高速模式咯
        // TODO 如果内核数超过14, 会挂掉
        /*
         * if (cores > 1 && task.getTotalWidth() == vinfo.getWidth() &&
         * task.getTotalHeight() == task.getTotalHeight()) {
         * log.info("Using Fast mode , cores = " + cores); ctx.set("bitrate",
         * "2000k"); int count = 0; StringBuilder sb = new StringBuilder(); for
         * (int i = 0; i < task.getCutX(); i++) { for (int j = 0; j <
         * task.getCutY(); j++) { count ++; String other =
         * String.format("  -an -vf  \"crop=%d:%d:%d:%d\" ", task.getCutWidth(),
         * task.getCutHeight(), i*task.getCutWidth(), j*task.getCutHeight());
         * String mp4Path = getRealPath(String.format("%s/%s_%s.mp4",
         * task.getDest(), i+1, j+1)); mp4s.add(mp4Path);
         * 
         * sb.append(other).append(" ").append(mp4Path).append("  \\\n"); if
         * (count % cores == 0) { ctx.set("other", sb.toString());
         * shells.add(tpl.render(ctx).toString() + "\n\n"); sb.setLength(0);
         * count = 0; } } } if (count > 0) { ctx.set("other", sb.toString());
         * shells.add(tpl.render(ctx).toString() + "\n\n"); } } else {
         */
        // 那就是普通模式, 一个一个转咯
        for (int i = 0; i < task.getCutX(); i++) {
            for (int j = 0; j < task.getCutY(); j++) {
                String other = String.format("  -an -vf  \"scale=%d:%d,crop=%d:%d:%d:%d\"",
                                             task.getTotalWidth(),
                                             task.getTotalHeight(),
                                             task.getCutWidth(),
                                             task.getCutHeight(),
                                             i * task.getCutWidth(),
                                             j * task.getCutHeight());
                String mp4Path = getRealPath(String.format("%s/%s_%s.mp4",
                                                           task.getDest(),
                                                           i + 1,
                                                           j + 1));
                ctx.set("other", other + " " + mp4Path);
                shells.add(tpl.render(ctx).toString() + "  \n\n");
                mp4s.add(mp4Path);
            }
        }
        /*
         * }
         */

        // 这部分是后处理,移动mp4的moov box
        StringBuilder sb = new StringBuilder();
        tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("after.tpl"))));
        for (String mp4Path : mp4s) {
            ctx = Lang.context();
            ctx.set("path", mp4Path);
            sb.append(tpl.render(ctx).toString());
        }

        shells.add(sb.toString());

        return shells.toArray(new String[]{});
    }

    public void runTask(VideoxTask task) {
        Stopwatch sw = Stopwatch.begin();
        String srcPath = getRealPath(task.getSrc());
        log.debug("Start: " + srcPath);
        VideoInfo srcInfo = readInfo(task);
        if (srcInfo == null) {
            throw new VideoConvException("Bad Video" + srcPath);
        }

        if (srcInfo.getLength() < 1) {
            throw new VideoConvException("Video Length too short < 1s?! " + srcPath);
        }
        if (srcInfo.getLength() > 3600 * 24) {
            throw new VideoConvException("Video Length too large > 24h?! " + srcPath);
        }

        task.setupVideoInfo(srcInfo);

        String initShell = initShell(task, srcInfo);
        if (initShell != null) {
            log.debug("Do Init: " + srcPath);
            try {
                runShell(initShell);
            }
            catch (Throwable e) {
                throw new VideoConvException("Init Shell Fail " + srcPath, e);
            }
            log.debug("Done Init: " + srcPath);
        }

        if (task.getPreview() != null && (task.isForce() || !isVaild(task.getPreview()))) {
            log.debug("Preview target : " + task.getPreview());
            String shell = previewShell(task, srcInfo);
            try {
                runShell(shell);
            }
            catch (Throwable e) {
                throw new VideoConvException("Make Preview Fail " + srcPath, e);
            }
            log.debug("Done Preview: " + srcPath);
        }

        if (task.getThumb() != null && (task.isForce() || !isVaild(task.getThumb()))) {
            log.debug("Thumb target : " + task.getThumb());
            String shell = thumbShell(task, srcInfo);
            try {
                runShell(shell);
            }
            catch (Throwable e) {
                throw new VideoConvException("Make Thumb Fail " + srcPath, e);
            }
            log.debug("Done Thumb: " + srcPath);
        }

        if (!task.isForce() && isVaild(task.getDest()))
            return;
        log.debug("Prapare Main Shell");
        String[] mainShells = mainShell(task, srcInfo);
        try {
            log.debug("Do Main: " + srcPath);
            for (int i = 0; i < mainShells.length; i++) {
                log.debug("Run main shell index=" + i);
                runShell(mainShells[i]);
            }
            log.debug("Done Main: " + srcPath);
        }
        catch (Throwable e) {
            throw new VideoConvException("Main Convent Fail " + task.getSrc(), e);
        }

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

        sw.stop();
        log.info("Task Done:\n" + Json.toJson(task) + "\n" + sw);
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
            log.debug("Run shell: " + f.getAbsolutePath());
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

    public String initShell(VideoxTask task, VideoInfo vinfo) {
        return null;
    }

    public String getRealPath(String path) {
        if (path.startsWith("/"))
            return path;
        if (path.startsWith("~/"))
            return Disks.normalize(path);
        throw Lang.impossible();
    }

    public boolean isVaild(String path) {
        File f = new File(path);
        return f.isFile() && f.exists() && f.length() > 0;
    }
}
