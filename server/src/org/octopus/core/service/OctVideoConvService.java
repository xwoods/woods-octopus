package org.octopus.core.service;

import java.util.Date;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.runner.NutLock;
import org.nutz.runner.NutRunner;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.VideoConvRequest;
import org.octopus.core.fs.FsIO;
import org.octopus.core.fs.FsPath;
import org.octopus.core.service.vc.MockVideoConvTask;
import org.octopus.core.service.vc.VideoConvTask;

import com.danoo.videox.ConvTask;
import com.danoo.videox.bean.VideoxTask;

@IocBean(create = "init", depose = "destory", name = "videoConvService")
public class OctVideoConvService {

    @Inject("refer:thumbnailService")
    private OctThumbnailService thumbnailService;
    @Inject
    private Dao dao;
    @Inject
    private FsIO fsIO;
    @Inject("java:$conf.getBoolean('vc-mock', false)")
    private boolean useMock;

    private NutRunner convRunner;
    private NutLock convLock;
    private boolean stopRunner;

    private VideoConvRequest getConvRequest() {
        return dao.fetch(VideoConvRequest.class, Cnd.where("isFinish", "=", false)
                                                    .asc("createTime"));
    }

    public void init() {
        stopRunner = false;
        convRunner = new NutRunner("video.conv.runner") {
            @Override
            public long exec() throws Exception {
                if (stopRunner) {
                    return 1000 * 3600;
                }
                VideoConvRequest vcReq = getConvRequest();
                while (vcReq != null) {
                    convertVideo(vcReq);
                    vcReq = getConvRequest();
                }
                return 10 * 1000;
            }
        };
        convLock = convRunner.getLock();
    }

    public void destory() {
        if (convLock != null && convRunner != null) {
            stopRunner = true;
            convLock.stop().wakeup();
            convLock = null;
            convRunner = null;
        }
    }

    public void start() {
        new Thread(convRunner).start();
    }

    public void stop() {
        destory();
    }

    public void convertVideo(VideoConvRequest vcReq) {
        Document doc = fsIO.fetch(vcReq.getDocId());
        VideoxTask task = getVideoxTask(doc, vcReq);
        //
        Chain uChain = Chain.make("startTime", new Date());
        try {
            getConvTask(doc).runTask(task);
            // 调整一下thumb
            if (task.getThumb() != null) {
                thumbnailService.createThumbnail(task.getThumb(), task.getThumb(), 0, 0);
            }
        }
        catch (Exception e) {
            uChain.add("hasError", true);
            uChain.add("errorMsg", e.getMessage());
            // fuck, but make tran done
            doc.setTransDone(true);
            doc.setTransFail(true);
            dao.update(doc, "transDone|transFail");
        }
        uChain.add("endTime", new Date());
        uChain.add("isFinish", true);
        dao.update(VideoConvRequest.class, uChain, Cnd.where("id", "=", vcReq.getId()));
    }

    private ConvTask getConvTask(Document doc) {
        if (useMock) {
            return new MockVideoConvTask(dao, doc);
        } else {
            return new VideoConvTask(dao, doc);
        }
    }

    private VideoxTask getVideoxTask(Document doc, VideoConvRequest vcReq) {
        String srcPath = FsPath.file(doc);
        String infoPath = FsPath.fileExtra(doc, FsPath.EXTRA_FILE_INFO);
        String transPath = FsPath.fileExtra(doc, FsPath.EXTRA_DIR_TRANS);
        String previewPath = FsPath.fileExtra(doc, FsPath.EXTRA_DIR_PREVIEW);
        VideoxTask task = new VideoxTask();
        task.setSrc(srcPath);
        if (vcReq.isCreateTrans()) {
            task.setCutAs(vcReq.getTransCutAs());
            task.setDestinfo(infoPath);
            task.setDest(transPath);
        }
        if (vcReq.isCreatePreview()) {
            task.setPreview(previewPath + "/preview.mp4");
        }
        if (vcReq.isCreateThumb()) {
            task.setThumb(previewPath + "/preview.jpg");
        }
        return task;
    }

}
