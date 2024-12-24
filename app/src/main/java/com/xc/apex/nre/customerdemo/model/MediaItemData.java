package com.xc.apex.nre.customerdemo.model;

public class MediaItemData {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int type;
    private String url;
    private int srcId;

    public MediaItemData(int type, String url) {
        this.type = type;
        this.url = url;
    }

    public MediaItemData(int type, int srcId) {
        this.type = type;
        this.srcId = srcId;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }
}
