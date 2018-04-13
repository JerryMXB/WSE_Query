package com.wse;

/**
 * Created by chaoqunhuang on 10/29/17.
 */
public class Ranking {
    private static final double K_1 = 1.2;
    private static final double B = 0.75;

    public static double calculateBM25(Url u, int Ft, int Fdt, double Davg, int n) {
        return BM25(Ft, Fdt, u.getLength(), Davg, n);
    }

    /**
     * Calculate BM 25 given a document
     * @param Ft number of documents that contain term t
     * @param Fdt frequency of term t in document d
     * @param d length of document d
     * @param Davg avag of documents
     * @param n total number of documents
     * @return BM25 score
     */
    public static double BM25(int Ft, int Fdt, int d, double Davg, int n) {
        double K = K_1 * ((1 - B) + B * d / Davg);
        double bm25 = Math.log((n - Ft + 0.5) / (Ft + 0.5) * (K_1 + 1) * Fdt / (K + Fdt));
        return bm25;
    }

}
