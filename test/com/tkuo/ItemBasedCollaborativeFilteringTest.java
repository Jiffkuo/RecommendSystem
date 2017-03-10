package com.tkuo;

import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.*;

/**
 * Created by mac on 2017/3/9.
 */
public class ItemBasedCollaborativeFilteringTest {


    @Test
    public void SelfTraining() throws IOException {
        ItemBasedCollaborativeFiltering sol = new ItemBasedCollaborativeFiltering(1000, 160);
        String selftrain = "gentrain.txt";
        String[] selftext = {"gentest5.txt", "gentest10.txt", "gentest15.txt"};
        String[] selfresult = {"genresult5.txt", "genresult10.txt", "genresult15.txt"};

        System.out.println("Start to do self training for Item-Based...");
        // setTrainFile
        String inPath = "./SimplePatterns/";
        String outPath = "./SimplePatterns/";
        String trainFile = inPath + selftrain;
        String testFile = "";
        // set case amplifcation rho parameter
        System.out.println("Set " + trainFile + " data");
        sol.setTrainDataSetAndInitialize(trainFile);

        // execution
        // run Cosine Vector Similarity
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with Adjusted Cosine Similarity method");
            sol.SetTestDataAndPredict(inPath + selftext[i]);

            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "AdjustedCosineVecSim/", selfresult[i]);
        }
    }

    @Test
    public void TestItemBasedCollaborativeFiltering() throws IOException {
        ItemBasedCollaborativeFiltering sol = new ItemBasedCollaborativeFiltering(1000, 200);
        String train = "train.txt";
        String[] text = {"test5.txt", "test10.txt", "test15.txt"};
        String[] result = {"result5.txt", "result10.txt", "result15.txt"};

        System.out.println("Start to do Item-Based Collaborative Filtering ...");
        // setTrainFile
        String inPath = "./GeneralPatterns/";
        String outPath = "./Results/ItemBased/";
        String trainFile = inPath + train;
        String testFile = "";
        // set case amplifcation rho parameter
        System.out.println("Set " + trainFile + " data");
        sol.setTrainDataSetAndInitialize(trainFile);

        // execution
        // run Cosine Vector Similarity
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with Adjusted Cosine Similarity method");
            sol.SetTestDataAndPredict(inPath + text[i]);

            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "AdjustedCosineVecSim/", result[i]);
        }
    }
}