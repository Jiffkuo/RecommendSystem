package com.tkuo;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Tzu-Chi Kuo on 2017/2/27.
 * Purpose:
 *  test all functions
 */
public class UserBasedCollaborativeFilteringTest {
    UserBasedCollaborativeFiltering sol = new UserBasedCollaborativeFiltering();
    String train = "train.txt";
    // input files
    String[] text = {"test5.txt", "test10.txt", "test20.txt"};
    // output files
    String[] result = {"result5.txt", "result10.txt", "result20.txt"};

    @Test
    public void TestUserBasedCollaborativeFiltering() throws IOException {
        System.out.println("Start to test User-Based Collaborative Filtering ...");
        // setTrainFile
        String inPath = "./GeneralPatterns/";
        //String inPath = "./SimplePatterns/";
        String outPath = "./Results/UserBased/";
        String trainFile = inPath + train;
        String testFile = "";
        System.out.println("Set " + trainFile + " data");
        sol.setTrainDataSet(trainFile);

        // execution
        // run Cosine Vector Similarity
        for (int i = 0; i < text.length; i++) {
            System.out.println("Execuing with Cosine Similarity method");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.CosVecSim);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "CosineVecSim/", result[i]);
        }

        // run Pearson Correlation
        for (int i = 0; i < text.length; i++) {
            System.out.println("Execuing with Pearon Correlation method");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.PearsonCorr);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorr/", result[i]);
        }
    }

}