package com.tkuo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mac on 2017/2/27.
 */
public class MethodsTest {

    Methods mthd = new Methods();
    @Test
    public void cosVecSim() throws Exception {
        ArrayList<Integer> vec1 = new ArrayList<>(Arrays.asList(9, 3, 5));
        ArrayList<Integer> vec2 = new ArrayList<>(Arrays.asList(10, 3, 5));
        // [Test] CosineSimilarity = 0.998896211071594
        System.out.println("[Test] CosineSimilarity = " + mthd.CosVecSim(vec1, vec2));
    }
}