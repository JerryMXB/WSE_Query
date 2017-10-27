package com.wse;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chaoqunhuang on 10/27/17.
 */
public class Main {
    public static void main(String[] args) {
        try {
            FilePath filePath = new FilePath();
            FileInputStream fileInputStream = new FileInputStream(FilePath.LEXICON);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            Lexicon[] lexicons = loadingLexicon(bufferedReader);
            System.out.println("Loading lexicons successfully!");
            peakingFetch(lexicons, 672761);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static Lexicon[] loadingLexicon(BufferedReader bufferedReader) throws IOException {
        List<Lexicon> lexicons = new ArrayList<>();
        String buffer;
        while((buffer = bufferedReader.readLine()) != null) {
            String[] params = buffer.split(" ");
            lexicons.add(new Lexicon(Integer.valueOf(params[0]), Integer.valueOf(params[1]),
                    Integer.valueOf(params[2]), Integer.valueOf(params[3])));
        }
        return lexicons.toArray(new Lexicon[lexicons.size()]);
    }

    public static void peakingFetch(Lexicon[] lexicons, int wordId) throws IOException{
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
    }


}
