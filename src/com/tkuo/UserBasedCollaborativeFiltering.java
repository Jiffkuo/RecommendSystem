package com.tkuo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tzu-Chi Kuo on 2017/2/27.
 * Purpose:
 *   implement User-Based Collaborative Filtering Algorithm
 *   Cosine Similarity method
 *   Pearson Correlation method
 */

public class UserBasedCollaborativeFiltering {

    // private data variable
    private List<HashMap<Integer, Integer>> trainDataSet;
    private List<List<Integer>> resultSet;
    Methods mthds;

    // Constructor
    public UserBasedCollaborativeFiltering() {
        trainDataSet = new ArrayList<>();
        mthds = new Methods();
    }

    // Set train.txt to trainDataSet
    public void setTrainDataSetAndInitialize(String fName, double caseampRHO) throws IOException{
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        String line = "";
        HashMap<Integer, Integer> movieRating;

        // userid (row): 1-200, movieid (col): 1-1000
        // each line = userID
        while ((line = bufReader.readLine()) != null) {
            String[] ratings = line.split("\t");
            movieRating = new HashMap<>();
            // set <movieID, rating> per UserID
            for (int movieID = 1; movieID <= ratings.length; movieID++) {
                movieRating.put(movieID, Integer.valueOf(ratings[movieID - 1]));
            }
            // length of traindataSet = # of user = 200
            trainDataSet.add(movieRating);
        }
        fReader.close();

        // calculate inverse user frequency and similar user rated movie average
        mthds.initialization(trainDataSet, caseampRHO);

        // debug purpose
        System.out.println("[Info] Number of user = " + trainDataSet.size());
        System.out.println("[Info] Number of movie = " + trainDataSet.get(0).size());
    }

    // generate result to text file
    public void getRecommendResult(String path, String fName) throws IOException {
        File directory = new File(path);
        // create directory
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // write file
        FileWriter fWriter = new FileWriter(path + fName);
        BufferedWriter bufWriter = new BufferedWriter(fWriter);
        String prefix = "";
        for (List<Integer> result : resultSet) {
            String text = prefix + result.get(0) + " " + result.get(1) + " " + result.get(2);
            bufWriter.write(text);
            bufWriter.flush();
            prefix = "\n";
        }
        fWriter.close();
    }

    // Assume userID is sorted order, so the sequence in List or Hash is based on the sorted userID
    public void SetTestDataAndPredict(String fName, Methods.MethodType type, int K) throws IOException{
        // Read Predication file
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        String line = "";
        List<Integer> ratingList = new ArrayList<>();
        List<Integer> movieIDList = new ArrayList<>();
        List<Integer> result;
        int curUserID = 0;

        // set new resultSet
        resultSet = new ArrayList<>();
        while ((line = bufReader.readLine()) != null) {
            String[] oneSet = line.split(" ");
            int userID = Integer.valueOf(oneSet[0]);
            int movieID = Integer.valueOf(oneSet[1]);
            int rating = Integer.valueOf(oneSet[2]);

            // rating between 1 and 5, 0 means to predict
            if (rating >= 1 && rating <= 5) {
                // same order
                movieIDList.add(movieID);
                ratingList.add(rating);
            } else if (rating == 0) {
                // new user to predict
                if (curUserID != userID) {
                    mthds.clearWeightList();
                    mthds.executeWeight(trainDataSet, movieID, movieIDList, ratingList, type);
                    curUserID = userID;
                    // clean movie and rating list after weighting
                    movieIDList.clear();
                    ratingList.clear();
                }
                // generate prediction result
                double outRating = 0;
                result = new ArrayList<>();
                switch (type.name()) {
                    case "CosVecSim":
                        outRating = mthds.PredictByCosVecSim(trainDataSet, movieID, K);
                        break;
                    case "PearsonCorr":
                    case "PearsonCorrIUF":
                    case "PearsonCorrCase":
                        outRating = mthds.PredictByPearsonCorr(trainDataSet, movieID, type, K);
                        break;
                    case "MyMethod":
                        double outCosine = mthds.PredictByCosVecSim(trainDataSet, movieID, K);
                        double outPearson = mthds.PredictByPearsonCorr(trainDataSet, movieID, type, K);
                        outRating = (0.3 * outCosine) + (0.7 * outPearson);
                        break;
                    default:
                        System.out.println("[Error]: [1]Cannot recognize the method");
                }
                // debug purpose
                //System.out.println(outRating);
                // round off
                outRating = Math.round(outRating);
                if (outRating > 5) {
                    outRating = 5;
                } else if (outRating < 1) {
                    outRating = 1;
                }
                result.add(userID);
                result.add(movieID);
                result.add((int)outRating);
                resultSet.add(result);
            } else {
                System.err.println("[Error] invalid data (MovieID: "
                        + movieID + ", " + rating +")");
            }
        }
        fReader.close();
    }

}