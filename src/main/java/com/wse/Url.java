package com.wse;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Url {
    private int docId;
    private String url;

    public Url(int docId, String url) {
        this.docId = docId;
        this.url = url;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
