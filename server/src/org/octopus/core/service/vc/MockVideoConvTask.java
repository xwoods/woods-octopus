package org.octopus.core.service.vc;

import org.nutz.dao.Dao;
import org.octopus.core.bean.Document;

import com.danoo.videox.bean.VideoInfo;
import com.danoo.videox.bean.VideoxTask;

public class MockVideoConvTask extends AbstractConvTask {

    public MockVideoConvTask(Dao dao, Document doc) {
        super(dao, doc);
    }

    @Override
    public VideoInfo readInfo(VideoxTask task) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String previewShell(VideoxTask task, VideoInfo vinfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String thumbShell(VideoxTask task, VideoInfo vinfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] mainShell(VideoxTask task, VideoInfo vinfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] previewSize(VideoInfo vinfo) {
        return null;
    }

}
