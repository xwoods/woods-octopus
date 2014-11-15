package org.octopus.core.service.vc;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.octopus.core.bean.Document;

import com.danoo.videox.bean.VideoInfo;
import com.danoo.videox.bean.VideoxTask;

public class VideoConvTask extends AbstractConvTask {

    public VideoConvTask(Dao dao, Document doc) {
        super(dao, doc);
    }

    @Override
    public int[] previewSize(VideoInfo info) {
        // 源视频的1/4
        int previewWidth = (info.getWidth() + 1) / 2;
        int previewHeight = (info.getHeight() + 1) / 2;

        if (previewHeight > 1000 || previewWidth > 1000) {
            log.debug("ConvertVideo, Preview Size Too Big: " + previewWidth + "x" + previewHeight);
            previewWidth = previewWidth / 2;
            previewHeight = previewHeight / 2;
        }

        if (previewWidth % 2 != 0) {
            previewWidth++;
        }
        if (previewHeight % 2 != 0) {
            previewHeight++;
        }
        return new int[]{previewWidth, previewHeight};
    }

    @Override
    public String previewShell(VideoxTask task, VideoInfo info) {
        int[] psize = previewSize(info);
        int previewWidth = psize[0];
        int previewHeight = psize[1];

        // log.debug("ConvertVideo, Preview Size: " + previewWidth + "x" +
        // previewHeight);
        Context ctx = Lang.context();
        ctx.set("srcPath", task.getSrc());
        ctx.set("previewPath", task.getPreview());
        ctx.set("previewWidth", previewWidth);
        ctx.set("previewHeight", previewHeight);
        ctx.set("ffmpeg_thread", "" + cores);
        ctx.set("bitrate", task.getPreviewBitRate() + "k");

        CharSegment tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("preview.tpl"))));
        return tpl.render(ctx).toString();
    }

    public String thumbShell(VideoxTask task, VideoInfo info) {
        int[] psize = previewSize(info);
        int previewWidth = psize[0];
        int previewHeight = psize[1];

        Context ctx = Lang.context();
        ctx.set("srcPath", task.getSrc());
        ctx.set("thumbPath", task.getThumb());
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
        ctx.set("srcPath", task.getSrc());
        ctx.set("bitrate", task.getBitRate() + "k");

        CharSegment tpl = new CharSegment(Streams.readAndClose(new InputStreamReader(getClass().getResourceAsStream("main.tpl"))));
        log.debugf("ConvertVideo, Cut: X=%d Y=%d", task.getCutX(), task.getCutY());
        if (task.getCutX() == 1 && task.getCutY() == 1) {
            String dst = "";
            if (task.getDest().endsWith(".mp4")) {
                dst = task.getDest();
            } else {
                dst = task.getDest() + "/1_1.mp4";
            }
            ctx.set("other", "-vcodec libx264 " + dst);
            return new String[]{tpl.render(ctx).toString()};
        }

        List<String> shells = new ArrayList<String>();
        List<String> mp4s = new ArrayList<String>();
        for (int i = 0; i < task.getCutX(); i++) {
            for (int j = 0; j < task.getCutY(); j++) {
                String other = String.format("  -an -vf  \"scale=%d:%d,crop=%d:%d:%d:%d\"",
                                             task.getTotalWidth(),
                                             task.getTotalHeight(),
                                             task.getCutWidth(),
                                             task.getCutHeight(),
                                             i * task.getCutWidth(),
                                             j * task.getCutHeight());
                String mp4Path = String.format("%s/%s_%s.mp4", task.getDest(), i + 1, j + 1);
                ctx.set("other", other + " " + mp4Path);
                shells.add(tpl.render(ctx).toString() + "  \n\n");
                mp4s.add(mp4Path);
            }
        }
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

    @Override
    public VideoInfo readInfo(VideoxTask task) {
        return readVideoInfo(task.getSrc());
    }

}
