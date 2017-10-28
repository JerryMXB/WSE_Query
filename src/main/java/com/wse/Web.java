package com.wse;

/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class Web {
    int docId;
    String url;
    String content;

    public Web(int docId, String url, String content) {
        this.docId = docId;
        this.url = url;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
