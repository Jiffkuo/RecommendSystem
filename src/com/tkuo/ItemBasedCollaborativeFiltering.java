package com.tkuo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/3/9.
 */
public class ItemBasedCollaborativeFiltering {
    // private global variable
    int[][] trainDataSet;
    List<List<Integer>> resultSet;
    double[]   avgRatingPerUser;
    List<Double> weightList;

    public ItemBasedCollaborativeFiltering(int row, int col) {
        trainDataSet = new int[row][col];
        avgRatingPerUser = new double[col];
    }

    // Set train.txt to trainDataSet
    public void setTrainDataSetAndInitialize(String fName, double caseampRHO) throws IOException {
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        int userid = 0;
        String line = "";

        // userid (row): 1-200, movieid (col): 1-1000
        // trainDataSet[movie][user]
        // each line = userID
        while ((line = bufReader.readLine()) != null) {
            String[] ratings = line.split("\t");
            for (int movieid = 0; movieid < ratings.length; movieid++) {
                int rate = Integer.valueOf(ratings[movieid]);
                trainDataSet[movieid][userid] = rate;
            }
            userid++;
        }
        fReader.close();

        // calculate the average of movie rates per user
        double rates = 0.0;
        double cnt = 0.0;
        for (int user = 0; user < trainDataSet[0].length; user++) {
            for (int movie = 0; movie < trainDataSet.length; movie++) {
                if (trainDataSet[movie][user] != 0) {
                    rates += trainDataSet[movie][user];
                    cnt++;
                }
            }
            avgRatingPerUser[user] = rates / cnt;
            rates = 0.0;
            cnt = 0.0;
        }

        // debug purpose
        System.out.println("[Info] Number of movie = " + trainDataSet.length);
        System.out.println("[Info] Number of user = " + trainDataSet[0].length);
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

    // read test file and start to predict
    // only implement Adjusted Consine Similarity
    public void SetTestDataAndPredict(String fName) throws IOException {
        // Read test file
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        // local variable to store input test data
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
                if (curUserID != userID) {
                    curUserID = userID;
                    // clean movie and rating list after weighting
                    movieIDList.clear();
                    ratingList.clear();
                }
                // same order
                movieIDList.add(movieID);
                ratingList.add(rating);
            } else if (rating == 0) {
                // generate prediction result
                double predictRating = 0;
                result = new ArrayList<>();
                executeWeights(movieIDList, ratingList, movieID);
                predictRating = predictByAdjustedCosSim();
                //System.out.println(outRating);
                // round off
                predictRating = Math.round(predictRating);
                if (predictRating > 5) {
                    predictRating = 5;
                } else if (predictRating < 1) {
                    predictRating = 1;
                }
                result.add(userID);
                result.add(movieID);
                result.add((int)predictRating);
                resultSet.add(result);
            } else {
                System.err.println("[Error] invalid data (MovieID: "
                        + movieID + ", " + rating +")");
            }
        }
        fReader.close();
    }

    // calculate adjusted cosine similarity
    public void executeWeights(List<Integer> mlist, List<Integer> rlist, int movieID) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        double weight = 0;
        // each pair is needed to calculate new weight
        weightList.clear();

        if (mlist.size() != rlist.size()) {
            System.err.println("[Error] Cannot calculate Cosine Vector Similarity weight");
            return;
        }

        // Find pair with item i (similar) and j (active)
        // find all users have rated for item i (active also rates)
        // find all users have rated for item j (recommends to active)
        for (int movie = 0; movie < mlist.size(); movie++) {
            for (int user = 0; user < trainDataSet[0].length; user++) {
                int pair_i = trainDataSet[mlist.get(movie) - 1][user];
                int pair_j = trainDataSet[movieID - 1][user];
                if (pair_i == rlist.get(movie) && pair_j != 0) {
                //if (pair_i !=0 && pair_j != 0) {
                    dotProduct += (pair_i - avgRatingPerUser[user]) * (pair_j - avgRatingPerUser[user]);
                    norm1 += Math.pow((pair_i - avgRatingPerUser[user]), 2);
                    norm2 += Math.pow((pair_j - avgRatingPerUser[user]), 2);
                }
            }
            // active user have 5 movies to calculate weights
            if (dotProduct == 0) {
                weight = 0.0;
            } else {
                weight = dotProduct / (norm1 * norm2);
                if (weight < -1) {
                    weight = -1;
                } else if (weight > 1) {
                    weight = 1;
                }
            }
            weightList.add(weight);
            dotProduct = 0.0;
            norm1 = 0.0;
            norm2 = 0.0;
        }
    }

    // predication
    public double predictByAdjustedCosSim() {
        double result = 0.0;

        return result;
    }

}
