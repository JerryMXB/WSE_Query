package com.wse;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FilePath {
    public static String INTERMEDIATE_POSTING;
    public static String INTERMEDIATE_POSTING_SORTED;
    public static String LEXICON;
    public static String INVERTED_INDEX;
    public static String WORD_LIST;
    public static String WORD_LIST_SORTED;
    public static String URL_TABLE;
    public static String LOG_PATH;
    public FilePath() {
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);
            String workspace = prop.getProperty("workspace");
            LOG_PATH = workspace + "/log.txt";
            INTERMEDIATE_POSTING = workspace + "/IntermediatePosting/posting.txt";
            INTERMEDIATE_POSTING_SORTED = workspace + "/IntermediatePosting/posting_sorted.txt";
            LEXICON = workspace + "/index/lexicon";
            INVERTED_INDEX = workspace + "/index/inverted_index";
            WORD_LIST = workspace + "/index/word_list";
            URL_TABLE = workspace + "/index/url_table";
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
