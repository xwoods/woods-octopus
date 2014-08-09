package org.octopus.bean.core;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.octopus.Octopus;
import org.octopus.bean.BeanCreateModify;
import org.octopus.fs.FileAs;

@Table("t_document")
@TableIndexes({@Index(name = "t_document_name", fields = {"name"}, unique = false),
               @Index(name = "t_document_type", fields = {"type"}, unique = false),
               @Index(name = "t_document_cate", fields = {"cate"}, unique = false),
               @Index(name = "t_document_ctuser", fields = {"createUser"}, unique = false)})
public class Document extends BeanCreateModify {
    // 文件名
    @ColDefine(width = 512)
    private String name;
    // 文件后缀
    private String type;
    // 文件分类 (音乐, 视频, 压缩包等, 各种文件类型归为一类)
    private String cate;
    // 文件mime
    private String mime;
    // 文件读取类型
    private FileAs fileAs;
    // 文件大小
    private long size;
    // 父节点id (如果是根节点的话, 那就是用户的id)
    private String parentId;
    // 是否私有(不公开时只有自己可以访问)
    private boolean isPrivate;
    // ----------------------------- 可读,可写,可删除是针对公开文件设置的权限, 文件创造者访问本文件不存在权限问题
    // 可读
    private boolean canRead;
    // 可写
    private boolean canWrite;
    // 可删除
    private boolean canRemove;
    // 可预览(图片与视频生成对应缩略图)
    private boolean canPreview;
    // 预览文件id
    private String previewlId;
    // 文件备注
    @ColDefine(width = 512)
    private String remark;

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public FileAs getFileAs() {
        return fileAs;
    }

    public void setFileAs(FileAs fileAs) {
        this.fileAs = fileAs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    public boolean isCanPreview() {
        return canPreview;
    }

    public void setCanPreview(boolean canPreview) {
        this.canPreview = canPreview;
    }

    public String getPreviewlId() {
        return previewlId;
    }

    public void setPreviewlId(String previewlId) {
        this.previewlId = previewlId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String evalPath() {
        return Octopus.evalPath(id);
    }
}
