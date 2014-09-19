package org.octopus.core.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.Json;
import org.octopus.core.fs.ReadType;

@Table("t_document_type")
public class DocumentType {

    @Name
    private String name;

    private ReadType readAs;

    private boolean hasPreview;

    private boolean hasInfo;

    private boolean hasTrans;

    @ColDefine(type = ColType.VARCHAR, width = 256)
    private String mime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReadType getReadAs() {
        return readAs;
    }

    public void setReadAs(ReadType readAs) {
        this.readAs = readAs;
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

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    @Override
    public String toString() {
        return Json.toJson(this);
    }

}
