package com.util;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Tzu-Chi Kuo on 2017/3/8.
 *   Purpose:
 *      create patterns to improve recommend system accuracy
 *      calculate Mean Absolute Error
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

        // generate answer data
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

    public void generaeTestPattern(String gName, String aName, String tName, int usrid, int numoftest) throws IOException {
        FileReader gReader = new FileReader(gName); // golden answer
        FileWriter aWriter = new FileWriter(aName); // test answer
        FileWriter tWriter = new FileWriter(tName); // test pattern
        BufferedReader bufgReader = new BufferedReader(gReader);
        BufferedWriter bufaWriter = new BufferedWriter(aWriter);
        BufferedWriter buftWriter = new BufferedWriter(tWriter);
        HashMap<Integer, String> map = new HashMap<>();

        // read golden answer and store into hashmap <movieid, line>
        String line;
        int movieid = 0;
        int curid = usrid + 1;
        while((line = bufgReader.readLine()) != null) {
            String[] text = line.split(" ");
            // assume no repeat movieID
            if (curid == Integer.valueOf(text[0])) {
                movieid = Integer.valueOf(text[1]);
                map.put(movieid, line);
                continue;
            } else {
                // select random movieid as visible data
                // use latest movieid as random seed
                Random rand = new Random();
                int cnt = 0;
                while (cnt < numoftest) {
                    int randkey = rand.nextInt(movieid) + 1;
                    if (map.containsKey(randkey)) {
                        String info = map.get(randkey);
                        //bufaWriter.write(info + "\n");
                        buftWriter.write(info + "\n");
                        buftWriter.flush();
                        map.remove(randkey);
                        cnt++;
                    }
                }
                // write rest of data to test answer and test
                for (Map.Entry<Integer, String> pair : map.entrySet()) {
                    String rest = pair.getValue();
                    bufaWriter.write(rest + "\n");
                    bufaWriter.flush();
                    StringBuilder sb = new StringBuilder();
                    String[] test = rest.split(" ");
                    sb.append(test[0] + " ");
                    sb.append(test[1] + " ");
                    sb.append(0 + "\n");
                    buftWriter.write(sb.toString());
                    buftWriter.flush();
                }
                map.clear();
                movieid = Integer.valueOf(text[1]);
                map.put(movieid, line);
                curid = Integer.valueOf(text[0]);
            }
        }
        bufgReader.close();
        bufaWriter.close();
        buftWriter.close();
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
        //System.out.println("sum of abs(golden-predict) = " + sum + " and total number is " + cnt);
        System.out.println("\tMAE of " + tName + " = " + (double)sum/cnt);
        gReader.close();
        tReader.close();
    }
}
