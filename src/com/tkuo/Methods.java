package com.tkuo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tzu-Chi Kuo on 2017/2/27.
 * Purpose:
 *   implement several methods for recommend system usage
 *   - Cosine Vector Similarity
 *   - Pearson Correlation
 */

public class Methods {

    // private data variable
    // store weight and average rating for each user (userID: 1 - 200)
    private List<Double> weightList;
    private List<Double> similarUsersAvgRating;
    private double       activeUserAvgRating;
    private double[]     iufArray;
    private double       caseampRHO;
    private double[]     movieAvgRating;

    // Support four methods
    public enum MethodType {
        CosVecSim,
        PearsonCorr,
        PearsonCorrIUF,
        PearsonCorrCase
    }

    // constructor
    public Methods() {
        weightList = new ArrayList<>();
        similarUsersAvgRating = new ArrayList<>();
    }

    // clean weightList for new user prediction
    public void clearWeightList() {
        weightList.clear();
    }

    /*
     * Initialization:
     * - Inverse uer frequency
     * - rated movie average per similar user
     * Can only calculate once
     */
    public void initialization(List<HashMap<Integer, Integer>> trainDataSet, double rho) {
        double totalNumUser = trainDataSet.size();
        int totalRatedValue = 0;
        int totalRatedCnt = 0;
        int totalMovies = trainDataSet.get(0).size();
        iufArray = new double[totalMovies];
        movieAvgRating = new double[totalMovies];

        // find number of users have rated movie
        for (int userID = 0; userID < trainDataSet.size(); userID++) {
            HashMap<Integer, Integer> allMovies = trainDataSet.get(userID);
            for (Map.Entry<Integer, Integer> pair : allMovies.entrySet()) {
                if (pair.getValue() != 0) {
                    // item: movie
                    iufArray[pair.getKey()-1]++;
                    movieAvgRating[pair.getKey() - 1] += movieAvgRating[pair.getKey() -1];
                    // value: rating
                    totalRatedValue += pair.getValue();
                    totalRatedCnt++;
                }
            }
            similarUsersAvgRating.add((double) (totalRatedValue / totalRatedCnt));
        }

        // calculate IUF = log(totalNumber / number of user rated movie)
        for (int movieID = 0; movieID < totalMovies; movieID++) {
            if (iufArray[movieID] != 0) {
                movieAvgRating[movieID] = movieAvgRating[movieID] / iufArray[movieID];
                iufArray[movieID] = Math.log10(totalNumUser / iufArray[movieID]);
            }
        }

        // for case amplification usage
        caseampRHO = rho;
        // debug
        System.out.println("[Info] Size of similarUsersAvgRating = " + similarUsersAvgRating.size());
        System.out.println("[Info] Size of iufArray = " + iufArray.length);
    }

    // CosineVectorSimilarity
    // assume vec1.length = vec2.length
    // vec1: active user, vec2: similar user
    public double CosVecSim(ArrayList<Integer> vec1, ArrayList<Integer> vec2) {
        int dotProduct = 0;
        int norm1 = 0;
        int norm2 = 0;

        if (vec1.size() != vec2.size()) {
            System.err.println("[Error] Cannot calculate Cosine Vector Similarity weight");
            return 0.0;
        }

        // accumulate weight
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }
        // Don't do Math if the numerator is zero
        if (dotProduct == 0) {
            return 0;
        }
        // return weight
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // Pearson Correlation
    // assume vec1.length = vec2.length
    // vec1: active user, vec2: similar user
    // Similar with Cosine Vector Similarity, need to calculate average rating
    public double PearsonCorr(ArrayList<Integer> vec1, ArrayList<Integer> vec2, int userID) {
        int dotProduct = 0;
        int norm1 = 0;
        int norm2 = 0;
        double avgVec1 = 0;
        double avgVec2;
        int lenVec = vec1.size();

        if (lenVec != vec2.size()) {
            System.err.println("[Error] Cannot calculate Pearson Correction weight");
            return 0.0;
        }

        // calculate rating average
        for (int i = 0; i < lenVec; i++) {
            avgVec1 += vec1.get(i);
        }
        avgVec1 /= lenVec;

        // record average for prediction usage
        activeUserAvgRating = avgVec1;
        avgVec2 = similarUsersAvgRating.get(userID);

        // accumulate weight
        for (int i = 0; i < lenVec; i++) {
            if (vec2.get(i) == 0) {
                continue;
            }

            dotProduct += (vec1.get(i) - avgVec1) * (vec2.get(i) - avgVec2);
            norm1 += Math.pow((vec1.get(i) - avgVec1), 2);
            norm2 += Math.pow((vec2.get(i) - avgVec2), 2);
        }

        // Don't do Math if the numerator is zero
        if (dotProduct == 0) {
            return 0;
        }
        // return weight [-1, 1]
        double result = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));

        if (result < -1) {
            return -1;
        } else if (result > 1) {
            return 1;
        }

        return result;
    }

    // Use specific method to calculate weight for each user
    public void executeWeight(List<HashMap<Integer, Integer>> trainDataSet,
                              List<Integer> mList, List<Integer> rList, MethodType type) {
        // vec1: active user, vec2: similar user
        ArrayList<Integer> vec1;
        ArrayList<Integer> vec2;

        // find weight for each user (userID: 1 - 200)
        for (int user = 0; user < trainDataSet.size(); user++) {
            // find common movieID
            HashMap<Integer, Integer> traindata = trainDataSet.get(user);
            vec1 = new ArrayList<>();
            vec2 = new ArrayList<>();
            int rating = 0;
            for (int movieID = 0; movieID < mList.size(); movieID++) {
                int key = mList.get(movieID);
                rating = traindata.get(key);
                vec1.add(rList.get(movieID));
                vec2.add(rating);
            }
            // store weight for each user (userID: 1 - 200)
            switch (type.name()) {
                case "CosVecSim":
                    weightList.add(CosVecSim(vec1, vec2));
                    break;
                case "PearsonCorr":
                case "PearsonCorrIUF":
                case "PearsonCorrCase":
                    weightList.add(PearsonCorr(vec1, vec2, user));
                    break;
                default:
                    System.out.println("[Error]: [2] Cannot recognize the method");
                    System.exit(1);
            }
        }
        // debug purpose
        // System.out.println("[Info] Test data size = " + mList.size() + "(" + rList.size()+")");
        // System.out.println("[Info] Total " + weightList.size() + " users are weighted");
    }

    // Based on Cosine Similarity weight information to do rating prediction
    public double PredictByCosVecSim(List<HashMap<Integer, Integer>> trainDataSet, int movieID) {
        // default prediction rating = 3 if no relevant info
        double predictRating = 3;
        double totalWeight = 0;
        double totalRating = 0;
        int rating;

        // The number of user should be the same
        if (trainDataSet.size() != weightList.size()) {
            System.err.println("[Error] Cannot predict because invalid dataset");
            return 0;
        }
        // Rating prediction (Cosine Similarity)
        // TODO: Assume all users are the most similar users, should find top K users
        for (int user = 0; user < trainDataSet.size(); user++) {
            HashMap<Integer, Integer> traindata = trainDataSet.get(user);
            if (traindata.containsKey(movieID)) {
                rating = traindata.get(movieID);
            } else {
                System.err.println("[Error] No record (userID: " + (user+1) + "movieID: " + movieID + ")");
                rating = 0; // no rating record
            }

            // Cannot predict if rating = 0
            if (rating == 0) {
                continue;
            }

            totalWeight += weightList.get(user);
            totalRating += weightList.get(user) * rating;
        }
        if (totalWeight != 0) {
            predictRating = totalRating / totalWeight;
        }
        return predictRating;
    }

    // Based on Pearon Correlation weight information to do rating prediction
    public double PredictByPearsonCorr(List<HashMap<Integer, Integer>> trainDataSet, int movieID, MethodType type) {
        // set active user average rating if no relevant info
        double predictRating = activeUserAvgRating;
        double totalWeight = 0;
        double totalRating = 0;
        double rating;
        double weight;

        // The number of user should be the same
        int similarUserSize = trainDataSet.size();
        if (similarUserSize != weightList.size() || similarUserSize != similarUsersAvgRating.size() ) {
            System.err.print("[Error] Cannot predict because invalid dataset:");
            System.err.print(" Size of User = " + similarUserSize);
            System.err.print(" Size of weight = " + weightList.size());
            System.err.print(" Size of AvgRating = " + similarUsersAvgRating.size());
            return 0;
        }
        // Rating prediction (Predict Correlation)
        // TODO: Assume all users are the most similar users, should find K users
        for (int user = 0; user < trainDataSet.size(); user++) {
            HashMap<Integer, Integer> traindata = trainDataSet.get(user);
            if (traindata.containsKey(movieID)) {
                rating = traindata.get(movieID);
            } else {
                System.err.println("[Error] No record (userID: " + (user+1) + "movieID: " + movieID + ")");
                rating = 0; // no rating record
            }
            weight = weightList.get(user);

            switch (type.name()) {
                case "PearsonCorr":
                    break;
                // IUF: multiply original rated movie
                case "PearsonCorrIUF":
                    rating *= iufArray[movieID - 1];
                    //weight *= iufArray[movieID - 1];
                    break;
                // Case Amplification: transform original weight
                case "PearsonCorrCase":
                    weight *= Math.pow(Math.abs(weight), caseampRHO - 1);
                    break;
                default:
                    System.out.println("[Error]: [3] Cannot recognize the method: " + type.name());
                    System.exit(1);
            }

            // Cannot predict if rating = 0, it seems that it doesn't affect the result
            if (rating == 0) {
                continue;
            }


            totalWeight += Math.abs(weight);
            totalRating += weight * (rating - similarUsersAvgRating.get(user));
        }
        // return
        if (totalWeight != 0) {
            predictRating = activeUserAvgRating + totalRating / totalWeight;
        }
        return predictRating;
    }

}