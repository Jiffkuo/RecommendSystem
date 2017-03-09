package com.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by user on 2017/3/8.
 */
public class GeneratePatternsTest {
    GeneratePatterns sol = new GeneratePatterns(200, 1000);
    String path = "./GeneralPatterns/";
    String golden = "train.txt";
    String gentrain = "gentrain.txt";
    String genans = "genans.txt";
    int numofgolden = 150;
    @Test
    public void readPattern() throws Exception {
        sol.readPattern(path + golden);
        // generate 50 users test pattern and 150 golden pattern
        sol.genPattern(path + gentrain, path + genans, numofgolden);
    }

    @Test
    public void genPattern() throws Exception {
    }
}