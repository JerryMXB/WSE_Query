package com.wse;

import java.io.IOException;

/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class test {
    public static void main(String[] args) {
        Query query = new Query();
        String[] words = new String[] {"guilty", "cat", "dog"};
        String[] docs = query.andQuery(words);
        for(String s : docs) {
            System.out.println(s);
        }
    }
}
