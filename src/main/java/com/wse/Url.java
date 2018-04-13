package com.wse;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Url implements Comparable {
    private int docId;
    private String url;
    private int length;
    private double score = 0d;

    public Url(int docId, String url, int length) {
        this.docId = docId;
        this.url = url;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        Url other = (Url) o;
        return this.score < other.score ? 1 : -1;
    }
}
