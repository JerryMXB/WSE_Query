package com.wse;

/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class test {
    public static void main(String[] args) {
        Query query = new Query();
        String[] words = new String[] {"Tandon", "School"};
        String[] docs = query.andQuery(words);
        StringBuffer sb = new StringBuffer();
        for (String s : docs) {
            sb.append(s);
            sb.append(' ');
        }
        System.out.println(sb.toString());
    }
}
