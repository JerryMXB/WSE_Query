package com.wse;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Lexicon {
    private int wordId;
    private int offset;
    private int length;
    private int count;

    public Lexicon(int wordId, int offset, int length, int count) {
        this.wordId = wordId;
        this.offset = offset;
        this.length = length;
        this.count = count;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
