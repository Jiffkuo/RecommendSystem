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
    public void TestCosineVectorSimilarity() throws IOException {
        System.out.println("Start to test User-Based Collaborative Filtering ...");
        // setTrainFile
        System.out.println("Set " + train + " data");
        sol.setTrainDataSet(train);
        // execution
        String path = "./Results/UserBased/CosineVecSim/";
        for (int i = 0; i < text.length; i++) {
            // Doing CosineVectorSimilarity
            System.out.println("Execuing with Cosine Similarity method");
            sol.CosineSimilarity(text[i]);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(path, result[i]);
        }
    }

}