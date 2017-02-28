package com.tkuo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tzu-Chi Kuo on 2017/2/27.
 * Purpose:
 *   implement several methods for recommend system usage
 *   - CosineVectorSimilarity
 *   - pearsonCorrelation
 */
public class Methods {

    // private data variable
    private List<Double> weightList;

    // constructor
    public Methods() {
        weightList = new ArrayList<>();
    }

    // clearn weightList for new user prediction
    public void clearWeightList() {
        weightList.clear();
    }

    // CosineVectorSimilarity
    // assume vec1.legnth = vec2.length
    public double CosVecSim(ArrayList<Integer> vec1, ArrayList<Integer> vec2) {
        int dotProduct = 0;
        int norm1 = 0;
        int norm2 = 0;

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

    // Using CosineVectorSimilarity to find weight for each user
    public void execCosVecSim(List<HashMap<Integer, Integer>> trainDataSet, List<Integer> mList, List<Integer> rList) {
        ArrayList<Integer> vec1; // train data
        ArrayList<Integer> vec2; // test data
        // find weight for each user (userID: 1 - 200)
        for (int user = 0; user < trainDataSet.size(); user++) {
            // find common movieID for CosVecSim
            HashMap<Integer, Integer> traindata = trainDataSet.get(user);
            vec1 = new ArrayList<>();
            vec2 = new ArrayList<>();
            for (int movieID = 0; movieID < mList.size(); movieID++) {
                int key = mList.get(movieID);
                if (traindata.containsKey(key)) {
                    vec1.add(traindata.get(key));
                    vec2.add(rList.get(movieID));
                }
            }
            // store weight for each user (userID: 1 - 200)
            weightList.add(CosVecSim(vec1, vec2));
        }
        // debug purpose
        // System.out.println("[Info] Test data size = " + mList.size() + "(" + rList.size()+")");
        // System.out.println("[Info] Total " + weightList.size() + " users are weighted");
    }

    // Based on weight information to do rating prediction
    public int PredictByCosVecSim(List<HashMap<Integer, Integer>> trainDataSet, int movieID) {
        // default prediction rating = 3 if no relevant info
        int predictRating = 3;
        double totalWeight = 0;
        double totalRating = 0;
        int rating = 0;
        // The number of user should be the same
        if (trainDataSet.size() != weightList.size()) {
            System.err.println("[Error] Cannot predict because invalid dataset");
            return 0;
        }
        // Rating prediction (Cosine Similarity)
        // Assume all users are the most similar users
        for (int user = 0; user < trainDataSet.size(); user++) {
            HashMap<Integer, Integer> traindata = trainDataSet.get(user);
            if (traindata.containsKey(movieID)) {
                rating = traindata.get(movieID);
            } else {
                System.err.println("[Error] No record (userID: " + (user+1) + "movieID: " + movieID + ")");
                rating = 0; // no rating record
            }
            if (rating == 0) {
                continue;
            }
            totalWeight += weightList.get(user);
            totalRating += weightList.get(user) * rating;
        }
        if (totalWeight != 0) {
            predictRating = (int) Math.round(totalRating / totalWeight);
        }
        return predictRating;
    }

}
