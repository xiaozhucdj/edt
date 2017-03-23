package com.onyx.android.sdk.utils;

import java.util.Random;

/**
 * Created by zhuzeng on 7/20/16.
 */
public class TestUtils {

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

}
