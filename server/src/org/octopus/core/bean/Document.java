package org.octopus.core.bean;

import java.util.Map;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.json.Json;
import org.octopus.core.fs.FsHelper;
import org.octopus.core.fs.ReadType;

@Table("t_document")
@TableIndexes({@Index(name = "i_document_name", fields = {"name"}, unique = false),
               @Index(name = "i_document_module", fields = {"module"}, unique = false),
               @Index(name = "i_document_define", fields = {"define"}, unique = false),
               @Index(name = "i_document_type", fields = {"type"}, unique = false),
               @Index(name = "i_document_cate", fields = {"cate"}, unique = false),
               @Index(name = "i_document_readAs", fields = {"readAs"}, unique = false),
               @Index(name = "i_document_parentId", fields = {"parentId"}, unique = false),
               @Index(name = "i_document_mf_time", fields = {"modifyTime"}, unique = false),
               @Index(name = "i_document_mf_user", fields = {"modifyUser"}, unique = false),
               @Index(name = "i_document_ct_time", fields = {"createTime"}, unique = false),
               @Index(name = "i_document_ct_user", fields = {"createUser"}, unique = false)})
public class Document extends BeanCreateModify {

    // 属于什么模块
    private String module;
    // 目录定义
    private String define;
    // 父节点id (如果是根节点的话)
    private String parentId;
    // 文件名
    @ColDefine(width = 512)
    private String name;
    // 文件后缀
    private String type;
    // 文件分类 (音乐, 视频, 压缩包等, 各种文件类型归为一类)
    private String cate;
    // 文件mime
    @ColDefine(type = ColType.VARCHAR, width = 256)
    private String mime;
    // 文件读取类型
    private ReadType readAs;
    // 文件大小
    private long size;
    // 是否私有(不公开时只有自己可以访问)
    private boolean isPrivate;
    // ----------------------------- 可读,可写,可删除是针对公开文件设置的权限, 文件创造者访问本文件不存在权限问题
    // 可读
    private boolean canRead;
    // 可写
    private boolean canWrite;
    // 可删除
    private boolean canRemove;
    // 有预览(图片与视频生成对应缩略图)
    private boolean hasPreview;
    // 有额外信息
    private boolean hasInfo;
    // 有转换后文件
    private boolean hasTrans;
    // 转换进度 0-100
    private int transRate;
    // 是否转换完成
    private boolean transDone;
    // -----------------------------
    // 元数据
    @ColDefine(type = ColType.TEXT)
    private String meta;
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

    public ReadType getReadAs() {
        return readAs;
    }

    public void setReadAs(ReadType readAs) {
        this.readAs = readAs;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isHasPreview() {
        return hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    public boolean isHasInfo() {
        return hasInfo;
    }

    public void setHasInfo(boolean hasInfo) {
        this.hasInfo = hasInfo;
    }

    public boolean isHasTrans() {
        return hasTrans;
    }

    public void setHasTrans(boolean hasTrans) {
        this.hasTrans = hasTrans;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public int getTransRate() {
        return transRate;
    }

    public void setTransRate(int transRate) {
        this.transRate = transRate;
    }

    public boolean isTransDone() {
        return transDone;
    }

    public void setTransDone(boolean transDone) {
        this.transDone = transDone;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    /**
     * @return 当前对象的meta对象
     */
    public Map<String, Object> myMeta() {
        return Json.fromJsonAsMap(Object.class, meta);
    }

    /**
     * @return 返回一个带着meta属性的map对象, 默认值都被设置好了
     */
    public Map<String, Object> dfMeta() {
        return FsHelper.dfMeta(type);
    }

    public boolean isBinary() {
        return readAs == ReadType.BIN;
    }

    public boolean isDir() {
        return readAs == ReadType.DIR;
    }

    public boolean isComplex() {
        return readAs == ReadType.CPX;
    }
}
