package com.wse;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Query {
    private Lexicon[] lexicons;
    private WordList[] wordLists;
    private Url[] urls;
    public Query()  {
        try {
            FilePath filePath = new FilePath();

            this.lexicons = loadingLexicon(FilePath.LEXICON);
            System.out.println("Loading lexicons successfully!");

            this.wordLists = loadingWordList(FilePath.WORD_LIST_SORTED);
            System.out.println("Loading wordLists successfully!");

            this.urls = loadingUrlList(FilePath.URL_TABLE_SORTED);
            System.out.println("Loading urlList successfully");
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    public String[] query(String word) throws IOException {
        int wordId = getWordIdbyWord(word);
        int[] urlIds = peakingFetch(this.lexicons, wordId);
        if (urlIds.length < 3) {
            String[] res = new String[urlIds.length];
            for (int i = 0; i < urlIds.length; i++) {
                res[i] = getUrlByDocId(urlIds[i]);
            }
            return res;
        } else {
            // Should rank here

            String[] res = new String[3];
            for (int i = 0; i < 3; i++) {
                res[i] = getUrlByDocId(urlIds[i]);
            }
            return res;
        }
    }

    private Lexicon[] loadingLexicon(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        List<Lexicon> lexicons = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            lexicons.add(new Lexicon(Integer.valueOf(params[0]), Integer.valueOf(params[1]),
                    Integer.valueOf(params[2]), Integer.valueOf(params[3])));
        }
        bufferedReader.close();
        return lexicons.toArray(new Lexicon[lexicons.size()]);
    }

    private WordList[] loadingWordList(String fileName) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        List<WordList> wordLists = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            wordLists.add(new WordList(params[0], Integer.valueOf(params[1])));
        }
        bufferedReader.close();
        return wordLists.toArray(new WordList[wordLists.size()]);
    }

    private Url[] loadingUrlList(String fileName) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        List<Url> urls = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            urls.add(new Url(Integer.valueOf(params[0]), params[1]));
        }
        bufferedReader.close();
        return urls.toArray(new Url[urls.size()]);
    }

    private int getWordIdbyWord(String word) {
        Comparator<WordList> c = new Comparator<WordList>() {
            public int compare(WordList w1, WordList w2) {
                return w1.getWord().compareTo(w2.getWord());
            }
        };

        int res = Arrays.binarySearch(wordLists, new WordList(word,0), c);
        return wordLists[res].getWordId();
    }

    private String getUrlByDocId(int docId) {
        Comparator<Url> c = new Comparator<Url>() {
            public int compare(Url u1, Url u2) {
                return u1.getDocId() - u2.getDocId();
            }
        };

        int res = Arrays.binarySearch(urls, new Url(docId, ""), c);
        return urls[res].getUrl();
    }


    private int[] peakingFetch(Lexicon[] lexicons, int wordId) throws IOException{
        Comparator<Lexicon> c = new Comparator<Lexicon>() {
            public int compare(Lexicon l1, Lexicon l2) {
                return l1.getWordId() - l2.getWordId();
            }
        };
        int res = Arrays.binarySearch(lexicons, new Lexicon(wordId, 0, 0 ,0), c);

        // Peaking the first chuck
        FileInputStream fileInputStream = new FileInputStream(FilePath.INVERTED_INDEX);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        int skip = dataInputStream.skipBytes(lexicons[res].getOffset());
        System.out.println(skip);
        int numsBlk = dataInputStream.readInt();
        int startBlk = dataInputStream.readInt();
        int endBlk = dataInputStream.readInt();
        int docLength = dataInputStream.readInt();
        int freLength = dataInputStream.readInt();

        System.out.println("MetaData:" + numsBlk + "start:" + startBlk + " end:" + endBlk + "docL:" + docLength +
                "freL:" + freLength);
        byte[] docIds = new byte[lexicons[res].getLength() - 20];
        dataInputStream.read(docIds, 0, docLength);

        int[] result = VbyteCompress.decode(docIds, numsBlk);
        System.out.println(result.length);
        System.out.print(result[0] + " ");
        for (int i = 1; i < result.length; i++) {
            System.out.print(result[i] + result[i-1] + " ");
            result[i] += result[i-1];
        }
        return result;
    }


}
