package org.octopus.core.fs;

public class FileType {

    private String name;

    private FileAs as;

    private String mime;

    private String cate;

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileAs getAs() {
        return as;
    }

    public void setAs(FileAs as) {
        this.as = as;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

}
