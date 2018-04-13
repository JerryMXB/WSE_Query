package com.wse;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class WordList {
    private String word;
    private int wordId;

    public WordList(String word, int wordId) {
        this.word = word;
        this.wordId = wordId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
