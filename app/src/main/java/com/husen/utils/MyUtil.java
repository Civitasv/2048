package com.husen.utils;

public class MyUtil {

    /**
     * 二维数组转String
     *
     * @return
     */
    public static String arrayToString(int[][] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                str = str + array[i][j] + ",";
            }
        }
        return str;
    }

    /**
     * String转二维数组
     */
    public static int[][] stringToArray(String str) {
        int array[][] = new int[4][4];
        String[] strs = str.split(",");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                array[i][j] = Integer.parseInt(strs[4*i + j]);
            }
        }
        return array;
    }
}
