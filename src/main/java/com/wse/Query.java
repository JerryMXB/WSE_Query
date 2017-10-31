package com.wse;

import java.io.*;
import java.util.*;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Query {
    private Lexicon[] lexicons;
    private Map<String, Integer> wordMap = new HashMap<>();
    private Map<Integer, Url> urlMap = new HashMap<>();
    private double dAvg;
    public Query()  {
        try {
            FilePath filePath = new FilePath();

            this.lexicons = loadingLexicon(FilePath.LEXICON);
            System.out.println("Loading lexicons successfully!");

            loadingWordList(FilePath.WORD_LIST_SORTED);
            System.out.println("Loading wordLists successfully!");

            loadingUrlList(FilePath.URL_TABLE_SORTED);
            System.out.println("Loading urlList successfully");

            this.dAvg = documentAvg();
            System.out.println("The Document avarage length is:" + this.dAvg);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }


    public String[] query(String word) throws IOException {
        if (getWordIdByWord(word) ==  -1) {
            System.out.println("Cannot find the word, the wordtable size is:" + this.wordMap.size());
            return new String[3];
        }
        InvertedIndexPointer word1 = new InvertedIndexPointer(getLexiconByWordId(getWordIdByWord(word)));
        System.out.println("Start finding blocks");
        Map<Integer, Integer> word1DocFre;
        int word1Min = word1.readBlockMeta();
        word1DocFre = word1.getRemainingDocFre();
        System.out.println("Start Calculating BM25");
        PriorityQueue<Url> queryResults = new PriorityQueue<>();
        for (Integer i : word1DocFre.keySet()) {
            Url u = new Url(i, getUrlByDocId(i), getUrlLengthByDocId(i));
            u.setScore(Ranking.calculateBM25(u, getLexiconByWordId((getWordIdByWord(word))).getCount(), word1DocFre.get(i),
                    dAvg, urlMap.size()));
            queryResults.add(u);
        }
        Url[] urls= new Url[]{queryResults.poll(), queryResults.poll(), queryResults.poll(),
                queryResults.poll(), queryResults.poll(), queryResults.poll()};
        System.out.println("Start generating Snippets");
        return generateSnippets(urls, new String[]{word}, 0);
    }

    public String[] andQuery(String[] words) {
        String[] res = new String[3];
        InvertedIndexPointer[] invertedIndexPointers = new InvertedIndexPointer[words.length];
        System.out.println("Start finding blocks");
        int start = 0;
        int min = 0;
        for (int i=0; i < words.length; i++) {
            invertedIndexPointers[i] = new InvertedIndexPointer(getLexiconByWordId(getWordIdByWord(words[i])));
            invertedIndexPointers[i].readBlockMeta();
            start = invertedIndexPointers[i].getStartDocId() > start ? invertedIndexPointers[i].getStartDocId() : start;
        }
        Map<Integer, Integer>[] invertedIndexList = new Map[words.length];
        Map<Integer, Integer>[] invertedIndexResult = new Map[words.length];
        for (int i = 0; i < words.length; i++) {
            invertedIndexList[i] = new HashMap<>();
            invertedIndexResult[i] = new HashMap<>();
        }
        for (int i=0; i < words.length; i++) {
            invertedIndexList[i] = invertedIndexPointers[i].getGEQ(start);
            min = invertedIndexList[i].size() < min ? i : min;
        }
        List<String> urls = new ArrayList<>();
        for (Integer i : invertedIndexList[min].keySet()) {
            int flag = 0;
            for (int j = 0; j < words.length; j++) {
                if (j == min) {
                    flag++;
                    continue;
                }
                if (invertedIndexList[j].containsKey(i)) {
                    flag++;
                }
            }
            if (flag == words.length) {
                for (int h = 0; h < words.length; h++) {
                    invertedIndexResult[h].put(i, invertedIndexList[h].get(i));
                }
            }
        }
        PriorityQueue<Url> queryResults = new PriorityQueue<>();

        for (Integer i : invertedIndexResult[0].keySet()) {
            for (int j = 0; j < words.length; j++) {
                Url u = new Url(i, getUrlByDocId(i), getUrlLengthByDocId(i));
                u.setScore(u.getScore() + Ranking.calculateBM25(u, getLexiconByWordId((getWordIdByWord(words[j]))).getCount(), invertedIndexResult[j].get(i),
                        dAvg, this.urlMap.size()));
                queryResults.add(u);
            }
        }
        Url[] results= new Url[]{queryResults.poll(), queryResults.poll(), queryResults.poll(),
                queryResults.poll(), queryResults.poll(), queryResults.poll()};
        System.out.println("There are :" + queryResults.size() + " results.");
        return generateSnippets(results, words, 0);
    }

    public String[] orQuery(String[] words) {
        Map<Integer, Integer>[] invertedIndexList = new Map[words.length];
        for (int i = 0; i < words.length; i++) {
            invertedIndexList[i] = new HashMap<>();
        }

        InvertedIndexPointer[] invertedIndexPointers = new InvertedIndexPointer[words.length];

        for (int i=0; i < words.length; i++) {
            try {
                invertedIndexPointers[i] = new InvertedIndexPointer(getLexiconByWordId(getWordIdByWord(words[i])));
            } catch (Exception e) {
                invertedIndexPointers[i] = null;
                continue;
            }
            invertedIndexPointers[i].readBlockMeta();
        }

        PriorityQueue<Url> queryResults = new PriorityQueue<>();

        for (int i=0; i < words.length; i++) {
            if (invertedIndexPointers[i] != null) {
                invertedIndexList[i] = invertedIndexPointers[i].getRemainingDocFre();
                for (Integer j : invertedIndexList[i].keySet()) {
                    Url u = new Url(j, getUrlByDocId(j), getUrlLengthByDocId(j));
                    u.setScore(u.getScore() + Ranking.calculateBM25(u, getLexiconByWordId((getWordIdByWord(words[i]))).getCount(), invertedIndexList[i].get(j),
                            dAvg, this.urlMap.size()));
                    queryResults.add(u);
                }
            }
        }

        Url[] results= new Url[]{queryResults.poll(), queryResults.poll(), queryResults.poll(),
                queryResults.poll(), queryResults.poll(), queryResults.poll()};
        System.out.println("There are :" + queryResults.size() + " results.");
        return generateSnippets(results, words,1);
    }

    private String[] generateSnippets(Url[] urls, String[] words, int flag) {
        String[] res = new String[5];
        int count = 0;
        for (int i = 0; i < urls.length; i++) {
            if (count == 5) break;
            if (urls[i] != null) {
                String snippet;
                if (flag == 0) {
                    snippet = Snippet.generateSnippet(urls[i].getDocId(), words[0]);
                } else {
                    snippet = Snippet.generateOrSnippets(urls[i].getDocId(), words);
                }
                if (!"".equals(snippet)) {
                    res[count] = getUrlByDocId(urls[i].getDocId()) + "$$$" + snippet + "$$$" + urls[i].getScore();
                    System.out.println(urls[i].getUrl() + " " + urls[i].getScore());
                    count++;
                }
            }
        }
        return res;
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

    private void loadingWordList(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        //List<WordList> wordLists = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            //wordLists.add(new WordList(params[0], Integer.valueOf(params[1])));
            this.wordMap.put(params[0], Integer.valueOf(params[1]));
        }
        bufferedReader.close();
        //return wordLists.toArray(new WordList[wordLists.size()]);
    }

    private void loadingUrlList(String fileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        //List<Url> urls = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            //urls.add(new Url(Integer.valueOf(params[0]), params[1], Integer.valueOf(params[2])));
            urlMap.put(Integer.valueOf(params[0]), new Url(Integer.valueOf(params[0]), params[1], Integer.valueOf(params[2])));
        }
        bufferedReader.close();
    }

    private int getWordIdByWord(String word) {

        return this.wordMap.get(word);
    }

    private String getUrlByDocId(int docId) {
        return urlMap.get(docId).getUrl();
    }

    private int getUrlLengthByDocId(int docId) {
        return this.urlMap.get(docId).getLength();
    }

    private Lexicon getLexiconByWordId(int wordId) {
        Comparator<Lexicon> c = new Comparator<Lexicon>() {
            public int compare(Lexicon l1, Lexicon l2) {
                return l1.getWordId() - l2.getWordId();
            }
        };
        int res = Arrays.binarySearch(this.lexicons, new Lexicon(wordId, 0, 0 ,0), c);
        return this.lexicons[res];
    }

    private double documentAvg() {
        double sum = 0d;
        for(Url u: this.urlMap.values()) {
            sum += u.getLength();
        }
        return sum / this.urlMap.size();
    }
}
