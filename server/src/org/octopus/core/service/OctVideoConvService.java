package org.octopus.core.service;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.runner.NutLock;
import org.nutz.runner.NutRunner;
import org.octopus.core.bean.VideoConvRequest;

@IocBean(create = "init", depose = "destory")
public class OctVideoConvService {

    private Log log = Logs.get();

    @Inject
    private Dao dao;

    private NutRunner convRunner;
    private NutLock convLock;
    private boolean stopRunner;

    public void init() {
        stopRunner = false;
        convRunner = new NutRunner("video.conv.runner") {
            @Override
            public long exec() throws Exception {
                if (stopRunner) {
                    return 1000 * 3600;
                }
                VideoConvRequest vcReq = dao.fetch(VideoConvRequest.class,
                                                   Cnd.where("isFinish", "=", false)
                                                      .asc("createTime"));
                while (vcReq != null) {
                    // 进行视频转换

                    // 查找下一个
                    vcReq = dao.fetch(VideoConvRequest.class, Cnd.where("isFinish", "=", false)
                                                                 .asc("createTime"));
                }
                return 10 * 1000;
            }
        };
        convLock = convRunner.getLock();
    }

    public void destory() {
        stopRunner = true;
        convLock.stop().wakeup();
        convLock = null;
        convRunner = null;
    }

}
