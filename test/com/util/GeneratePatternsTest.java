package com.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Tzu-Chi Kuo on 2017/3/8.
 * Purpose:
 *    generate train data and test data from original train data (200 users and 1000 movies)
 *    calculate Mean Absolute Error
 */
public class GeneratePatternsTest {
    GeneratePatterns sol = new GeneratePatterns(200, 1000);
    String path = "./SimplePatterns/";
    String golden = "train.txt";
    String gentrain = "gentrain.txt";
    String genans = "genans.txt";

    String[] gentest = {"gentest5.txt", "gentest10.txt", "gentest15.txt"};
    String[] gentestans = {"gentestans5.txt","gentestans10.txt", "gentestans15.txt"};

    String[] genresult = {"genresult5.txt", "genresult10.txt", "genresult15.txt"};

    int numofgolden = 160; // 80/20 rules
    int[] numoftest = {5, 10, 15};
    @Test
    public void readPattern() throws IOException {
        sol.readPattern("./GeneralPatterns/" + golden);
        // generate 40 users test pattern and 160 golden pattern
        sol.genPattern(path + gentrain, path + genans, numofgolden);
    }

    @Test
    public void genPattern() throws IOException {
        for (int i = 0; i < gentestans.length; i++) {
            sol.generaeTestPattern(path + genans, path + gentestans[i], path + gentest[i], numofgolden, numoftest[i]);
            System.out.println("Generate random pattern: " + gentest[i]);
        }
    }

    @Test
    public void testMAE() throws IOException {
        // calculate cosine vector similarity
        System.out.println("\tMAE for Cosine Vector Similarity");
        for (int i = 0; i < gentestans.length; i++) {
            sol.produceMAE(path + gentestans[i], path + "CosineVecSim/" + genresult[i]);
        }
        // calculate Pearson correlation
        System.out.println("\n\tMAE for Pearson correlation");
        for (int i = 0; i < gentestans.length; i++) {
            sol.produceMAE(path + gentestans[i], path + "PearsonCorr/" + genresult[i]);
        }

        // calculate Pearson correlation + IUF
        System.out.println("\n\tMAE for Pearson correlation + IUF ");
        for (int i = 0; i < gentestans.length; i++) {
            sol.produceMAE(path + gentestans[i], path + "PearsonCorrIUF/" + genresult[i]);
        }

        // calculate Pearson correlation + Case
        System.out.println("\n\tMAE for Pearson correlation + Case ");
        for (int i = 0; i < gentestans.length; i++) {
            sol.produceMAE(path + gentestans[i], path + "PearsonCorrCaseAmp/" + genresult[i]);
        }

        // calculate Pearson correlation + Case
        System.out.println("\n\tMAE for My Own Method");
        for (int i = 0; i < gentestans.length; i++) {
            sol.produceMAE(path + gentestans[i], path + "MyMethod/" + genresult[i]);
        }
    }
}