package com.wse;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaoqunhuang on 10/28/17.
 */
public class InvertedIndexPointer {
    private DataInputStream dataInputStream;
    private int startDocId;
    private int lastDocId;
    private int blockNum;
    private int numsBlk;
    private int jumpLength;
    private int docLength;
    private int freLength;
    private Map<Integer, Integer> docFre = new HashMap<>();

    public InvertedIndexPointer(Lexicon lexicon) {
        try {
            this.dataInputStream = new DataInputStream(new FileInputStream(FilePath.INVERTED_INDEX));
            dataInputStream.skipBytes(lexicon.getOffset());
            System.out.println(lexicon.getWordId() + " " + lexicon.getCount());
            this.blockNum = lexicon.getCount() / 128 + 1;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException("Cannot open inverted index");
        }
    }

    public int getLastDocId() {
        return lastDocId;
    }

    public int getStartDocId() {
        return startDocId;
    }

    public Map<Integer, Integer> getGEQ(int docId) {

        while (this.getLastDocId() < docId) {
            System.out.println("Jumping block");
            if (readNextBlockMeta() == -1) {
                break;
            }
            System.out.println(this.lastDocId);
        }
        return getRemainingDocFre();
    }

    public int readBlockMeta() {
        try {
            this.numsBlk = dataInputStream.readInt();
            this.startDocId = dataInputStream.readInt();
            this.lastDocId = dataInputStream.readInt();
            this.docLength = dataInputStream.readInt();
            this.freLength = dataInputStream.readInt();
            this.jumpLength = this.docLength + this.freLength;
            this.blockNum -= 1;
            return this.lastDocId;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException("Reading meta error");
        }
    }

    public int readNextBlockMeta() {
        try {
            if (this.blockNum <= 0 ) {
                return -1;
            }
            this.dataInputStream.skipBytes(this.jumpLength);
            this.numsBlk = dataInputStream.readInt();
            this.startDocId = dataInputStream.readInt();
            this.lastDocId = dataInputStream.readInt();
            this.docLength = dataInputStream.readInt();
            this.freLength = dataInputStream.readInt();
            this.jumpLength = this.docLength + this.freLength;
            this.blockNum -= 1;
            return this.lastDocId;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException("Reading meta error");
        }
    }

    public Map<Integer, Integer> getRemainingDocFre() {
        readDocIds();
        while (blockNum > 0) {
            readBlockMeta();
            readDocIds();
            blockNum -= 1;
        }
        return this.docFre;
    }

    private void readDocIds() {
        try {
            byte[] compressedDoc = new byte[this.docLength];
            byte[] compressedFre = new byte[this.freLength];
            dataInputStream.read(compressedDoc, 0, this.docLength);
            System.out.println(this.numsBlk);
            int[] decompressedDoc = VbyteCompress.decode(compressedDoc, this.numsBlk);
            for (int i = 1; i < decompressedDoc.length; i++) {
                decompressedDoc[i] += decompressedDoc[i-1];
            }

            dataInputStream.read(compressedFre, 0, this.freLength);
            int[] decompressedFre = VbyteCompress.decode(compressedFre, this.numsBlk);
            for (int i = 1; i < decompressedFre.length; i++) {
                decompressedFre[i] += decompressedFre[i-1];
            }

            for (int i = 0; i < this.numsBlk; i++) {
                this.docFre.put(decompressedDoc[i], decompressedFre[i]);
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            throw new RuntimeException("Read docId and Frequency error");
        }
    }
}
