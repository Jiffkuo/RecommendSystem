package com.tkuo;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tzu-Chi Kuo on 2017/2/27.
 * Purpose:
 *   implement User-Based Collaborative Filtering Algorithm
 *   Cosine similarity method
 *   Pearson Correlation method
 */

public class UserBasedCollaborativeFiltering {

    // private data variable
    private List<HashMap<Integer, Integer>> trainDataSet;
    private List<List<Integer>> resultSet;

    // Constructor
    public UserBasedCollaborativeFiltering() {
        trainDataSet = new ArrayList<>();
        resultSet = new ArrayList<>();
    }

    // Set train.txt to trainDataSet
    public void setTrainDataSet(String fName) throws IOException{
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
        // debug purpose
        System.out.println("[Info]Number of user = " + trainDataSet.size());
        System.out.println("[Info]Number of movie = " + trainDataSet.get(0).size());
    }

    // generate result to text file
    public void getRecommendResult(String fName) throws IOException {
        FileWriter fWriter = new FileWriter(fName);
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

    // CosineVectorSimilarity method
    public void CosineSimilarity(String fName) throws IOException{
        // Read Predication file
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        String line = "";
        List<Integer> ratingList = new ArrayList<>();
        List<Integer> movieIDList = new ArrayList<>();
        List<Integer> result;
        int curUserID = 0;
        // Method function
        Methods mthds = new Methods();

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
                if (curUserID != userID) {
                    mthds.clearWeightList();
                    mthds.execCosVecSim(trainDataSet, movieIDList, ratingList);
                    curUserID = userID;
                    movieIDList.clear();
                    ratingList.clear();
                }
                result = new ArrayList<>();
                result.add(userID);
                result.add(movieID);
                result.add(mthds.PredictByCosVecSim(trainDataSet, movieID));
                resultSet.add(result);
            } else {
                System.err.println("[Error] invalid data (MovieID: "
                                   + movieID + ", " + rating +")");
            }
        }
        fReader.close();
    }

}
