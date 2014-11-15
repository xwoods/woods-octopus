package org.octopus.core.fs;

public class TransInfo {

    private int cutX;

    private int cutY;

    private boolean hasThumb;

    private boolean hasPreview;

    private boolean hasTrans;

    public boolean isHasThumb() {
        return hasThumb;
    }

    public void setHasThumb(boolean hasThumb) {
        this.hasThumb = hasThumb;
    }

    public boolean isHasPreview() {
        return hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    public boolean isHasTrans() {
        return hasTrans;
    }

    public void setHasTrans(boolean hasTrans) {
        this.hasTrans = hasTrans;
    }

    public int getCutX() {
        return cutX;
    }

    public void setCutX(int cutX) {
        this.cutX = cutX;
    }

    public int getCutY() {
        return cutY;
    }

    public void setCutY(int cutY) {
        this.cutY = cutY;
    }

}
