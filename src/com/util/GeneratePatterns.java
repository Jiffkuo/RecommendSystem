package com.util;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tzu-Chi Kuo on 2017/3/8.
 *   Purpose:
 *      create patterns to improve recommend system accuracy
 */
public class GeneratePatterns {

    int[][] trainDataSet;

    public GeneratePatterns(int row, int col) {
        trainDataSet = new int [row][col];
    }

    public void readPattern(String fName) throws IOException {
        FileReader fReader = new FileReader(fName);
        BufferedReader bufReader = new BufferedReader(fReader);
        String line;
        int userID = 0;
        // userid (row): 1-200, movieid (col): 1-1000
        // each line = userID
        while ((line = bufReader.readLine()) != null) {
            String[] ratings = line.split("\t");
            for (int movieID = 0; movieID < ratings.length; movieID++ ) {
                trainDataSet[userID][movieID] = Integer.valueOf(ratings[movieID]);
            }
            userID++;
        }
        fReader.close();

        // debug purpose
        System.out.println("[Info] data[0][0] = " + trainDataSet[0][0]);
        System.out.println("[Info]Number of user = " + trainDataSet.length);
        System.out.println("[Info]Number of movie = " + trainDataSet[0].length);
    }

    public void genPattern(String gName, String tName, int numofgolden) throws IOException {
        FileWriter gWriter = new FileWriter(gName);
        FileWriter tWriter = new FileWriter(tName);
        BufferedWriter bufgWriter = new BufferedWriter(gWriter);
        BufferedWriter buftWriter = new BufferedWriter(tWriter);
        String prefix = "";
        // generate train data
        for (int i = 0; i < numofgolden; i++) {
            for (int j = 0; j < trainDataSet[i].length; j++) {
                String text = prefix + String.valueOf(trainDataSet[i][j]);
                bufgWriter.write(text);
                bufgWriter.flush();
                prefix = "\t";
            }
            bufgWriter.write("\n");
            prefix = "";
        }
        gWriter.close();

        // generate test data
        prefix = "";
        for (int i = numofgolden; i < trainDataSet.length; i++) {
            for (int j = 0 ; j < trainDataSet[i].length; j++) {
                if (trainDataSet[i][j] != 0) {
                    int userid = i + 1;
                    int movieid = j + 1;
                    String text = prefix + userid + " " + movieid + " " + String.valueOf(trainDataSet[i][j]);
                    buftWriter.write(text);
                    buftWriter.flush();
                    prefix = "\n";
                }
            }
        }
        tWriter.close();
    }

    public void produceMAE(String gName, String tName) throws IOException {
        FileReader gReader = new FileReader(gName);
        FileReader tReader = new FileReader(tName);
        BufferedReader bufgReader = new BufferedReader(gReader);
        BufferedReader buftReader = new BufferedReader(tReader);
        String answer;
        String test;
        int userid = 0;
        int movieid = 0;
        int rating = 0;
        int sum = 0;
        int cnt = 0;

        // read golden answer: genans.txt
        while ((answer = bufgReader.readLine()) != null) {
            String[] ansInfo = answer.split(" ");
            userid = Integer.valueOf(ansInfo[0]);
            movieid = Integer.valueOf(ansInfo[1]);
            rating = Integer.valueOf(ansInfo[2]);
            test = buftReader.readLine();
            String[] testInfo = test.split(" ");
            if (userid == Integer.valueOf(testInfo[0]) && movieid == Integer.valueOf(testInfo[1])) {
                sum += Math.abs(rating - Integer.valueOf(testInfo[2]));
                cnt++;
            } else {
                System.out.println("[Error] tuple information cannot match to do MAE");
                System.out.println(" - userID = " + userid + ":" + Integer.valueOf(testInfo[0]));
                System.out.println(" - movieID = " + movieid + ":" + Integer.valueOf(testInfo[1]));
                break;
            }
        }
        System.out.println("MAE of " + tName + " = " + (double)sum/cnt);
        gReader.close();
        tReader.close();
    }
}
