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
    public void SelfTraining() throws IOException {
        String selftrain = "gentrain.txt";
        String[] selftext = {"gentest5.txt", "gentest10.txt", "gentest15.txt"};
        String[] selfresult = {"genresult5.txt", "genresult10.txt", "genresult15.txt"};
        System.out.println("Start to do self training ...");
        // setTrainFile
        String inPath = "./SimplePatterns/";
        String outPath = "./SimplePatterns/";
        String trainFile = inPath + selftrain;
        String testFile = "";
        // set case amplifcation rho parameter
        double caseAmpRHO = 1.5;
        int Ktop = 60; // total user is 160
        System.out.println("Set " + trainFile + " data");
        sol.setTrainDataSetAndInitialize(trainFile, caseAmpRHO);

        // execution
        // run Cosine Vector Similarity
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with Cosine Similarity method");
            sol.SetTestDataAndPredict(inPath + selftext[i], Methods.MethodType.CosVecSim, Ktop);

            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "CosineVecSim/", selfresult[i]);
        }
        System.out.println("==========================================");
        // run Pearson Correlation
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with Pearon Correlation method");
            sol.SetTestDataAndPredict(inPath + selftext[i], Methods.MethodType.PearsonCorr, Ktop);
            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorr/", selfresult[i]);
        }
        System.out.println("==========================================");
        // run Pearson Correlation + IUF modification
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with Pearon Correlation method + IUF modification");
            sol.SetTestDataAndPredict(inPath + selftext[i], Methods.MethodType.PearsonCorrIUF, Ktop);
            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorrIUF/", selfresult[i]);
        }
        System.out.println("==========================================");
        // run Pearson Correlation + Case Amplification modification
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with Pearon Correlation method + Case Amplification modification");
            sol.SetTestDataAndPredict(inPath + selftext[i], Methods.MethodType.PearsonCorrCase, Ktop);
            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorrCaseAmp/", selfresult[i]);
        }
        System.out.println("==========================================");
        // run My Own Method
        for (int i = 0; i < selftext.length; i++) {
            System.out.println("Executing with My Own Method");
            sol.SetTestDataAndPredict(inPath + selftext[i], Methods.MethodType.MyMethod, Ktop);
            // generate result to txt file
            System.out.println("Generate " + selfresult[i] + " file");
            sol.getRecommendResult(outPath + "MyMethod/", selfresult[i]);
        }
    }

    @Test
    public void TestUserBasedCollaborativeFiltering() throws IOException {
        System.out.println("Start to test User-Based Collaborative Filtering ...");
        // setTrainFile
        String inPath = "./GeneralPatterns/";
        //String inPath = "./SimplePatterns/";
        String outPath = "./Results/UserBased/";
        String trainFile = inPath + train;
        String testFile = "";
        // set case amplifcation rho parameter
        double caseAmpRHO = 2.5;
        int Ktop = 75; // total user is 200
        System.out.println("Set " + trainFile + " data");
        sol.setTrainDataSetAndInitialize(trainFile, caseAmpRHO);

        // execution
        // run Cosine Vector Similarity
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with Cosine Similarity method");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.CosVecSim, Ktop);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "CosineVecSim/", result[i]);
        }

        // run Pearson Correlation
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with Pearon Correlation method");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.PearsonCorr, Ktop);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorr/", result[i]);
        }

        // run Pearson Correlation + IUF modification
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with Pearon Correlation method + IUF modification");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.PearsonCorrIUF, Ktop);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorrIUF/", result[i]);
        }

        // run Pearson Correlation + Case Amplification modification
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with Pearon Correlation method + Case Amplification modification");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.PearsonCorrCase, Ktop);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "PearsonCorrCaseAmp/", result[i]);
        }

        // run My Own Method
        for (int i = 0; i < text.length; i++) {
            System.out.println("Executing with My Own Method");
            sol.SetTestDataAndPredict(inPath + text[i], Methods.MethodType.MyMethod, Ktop);
            // generate result to txt file
            System.out.println("Generate " + result[i] + " file");
            sol.getRecommendResult(outPath + "MyMethod/", result[i]);
        }
    }

}