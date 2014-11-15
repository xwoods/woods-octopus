package org.octopus.core.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("video_conv_req")
public class VideoConvRequest {

    @Id
    private long id;

    private String docId;

    private String docName;

    private Date createTime;

    private Date startTime;

    private Date endTime;

    private boolean isFinish;

    private boolean hasError;

    @ColDefine(type = ColType.TEXT)
    private String errorMsg;

    private boolean createThumb;

    private boolean createPreview;

    private boolean createTrans;

    // 默认是1x1, 不分割
    private String transCutAs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isCreateThumb() {
        return createThumb;
    }

    public void setCreateThumb(boolean createThumb) {
        this.createThumb = createThumb;
    }

    public boolean isCreatePreview() {
        return createPreview;
    }

    public void setCreatePreview(boolean createPreview) {
        this.createPreview = createPreview;
    }

    public boolean isCreateTrans() {
        return createTrans;
    }

    public void setCreateTrans(boolean createTrans) {
        this.createTrans = createTrans;
    }

    public String getTransCutAs() {
        return transCutAs;
    }

    public void setTransCutAs(String transCutAs) {
        this.transCutAs = transCutAs;
    }

}
