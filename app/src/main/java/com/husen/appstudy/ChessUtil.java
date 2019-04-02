package com.husen.appstudy;

import java.util.Random;

public class ChessUtil {

    // 分数
    static int score = 0;
    // 最大分数
    static int maxScore = 0;

    //标志是否进行了某种操作
    static int operate = 1;

    /**
     * 在数组中随机生成2
     *
     * @param array
     */
    public static void randTwo(int array[][]) {
        //以时间为种子，生成随机数
        long t = System.currentTimeMillis();
        Random random = new Random(t);
        int i = Math.abs(random.nextInt(100) % 4);
        int j = Math.abs(random.nextInt(100) % 4);
        //不能在已经是大于0的位置生成2
        while (array[i][j] != 0) {
            i = Math.abs(random.nextInt(100) % 4);
            j = Math.abs(random.nextInt(100) % 4);
        }
        array[i][j] = 2;
    }

    /**
     * 判断区域内是否含有空位置
     *
     * @param array
     * @return
     */
    public static boolean hasEmpty(int array[][]) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (array[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否已经满位置且不可合并，即游戏结束
     *
     * @param array
     * @return
     */
    public static boolean isFull(int array[][]) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (array[i][j] == array[i + 1][j] || array[i][j] == array[i][j + 1]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void slide(int c, int array[][]) {
        if (c == 'z') {
            operate = 0;
            //扫描开始
            for (int i = 0; i < 4; i++) {
                //同一行的操作次数
                int op = 0;
                //特殊情况


                if (array[i][0] != 0 && array[i][2] != 0 && array[i][0] == array[i][1] && array[i][2] == array[i][3]) {
                    array[i][0] *= 2;
                    array[i][1] = array[i][2] * 2;
                    array[i][2] = 0;
                    array[i][3] = 0;
                    score += array[i][2];
                    continue;
                }
                for (int j = 0; j < 4; j++) {
                    int k = j;
                    //当左边存在0时，向左移动
                    while (k > 0 && array[i][k] > 0 && array[i][k - 1] == 0) {
                        array[i][k - 1] = array[i][k];
                        array[i][k] = 0;
                        k--;
                        operate = 1;
                    }
                    //k = j;
                    //判断相邻的两个是否相等，相等即向左移动
                    if (k > 0 && array[i][k] == array[i][k - 1] && op == 0) {
                        if (array[i][k] != 0) {
                            operate = 1;
                            score += array[i][k];
                            op++;
                        }
                        array[i][k - 1] *= 2;
                        array[i][k] = 0;
                    }
                }
            }


        } else if (c == 'y') {
            operate = 0;
            for (int i = 0; i < 4; i++) {
                //同一行的操作次数
                int op = 0;
                //特殊情况
                if (array[i][0] != 0 && array[i][2] != 0 && array[i][2] != 0 && array[i][0] == array[i][1] && array[i][2] == array[i][3]) {
                    array[i][3] *= 2;
                    array[i][2] = array[i][1] * 2;
                    array[i][1] = 0;
                    array[i][0] = 0;
                    continue;
                }
                for (int j = 3; j >= 0; j--) {
                    int k = j;

                    while (k < 3 && array[i][k] > 0 && array[i][k + 1] == 0) {
                        array[i][k + 1] = array[i][k];
                        array[i][k] = 0;
                        k++;
                        operate = 1;
                    }
                    //k = j;
                    //判断相邻的两个是否相等，相等即向右移动
                    if (k < 3 && array[i][k] == array[i][k + 1] && op == 0) {
                        if (array[i][k] != 0) {
                            operate = 1;
                            score += array[i][k];
                            op++;
                        }
                        array[i][k + 1] *= 2;
                        array[i][k] = 0;

                    }
                }
            }

        } else if (c == 's') {
            operate = 0;
            for (int j = 0; j < 4; j++) {

                //特殊情况，同一列4个数完全相同
                if (array[0][j] != 0 && array[0][j] == array[1][j] && array[2][j] == array[3][j]) {
                    array[0][j] *= 2;
                    array[1][j] = array[2][j] * 2;
                    array[2][j] = 0;
                    array[3][j] = 0;
                    continue;
                }
                //同一列的操作次数
                int op = 0;
                for (int i = 0; i < 4; i++) {
                    int k = i;
                    while (k > 0 && array[k][j] > 0 && array[k - 1][j] == 0) {
                        array[k - 1][j] = array[k][j];
                        array[k][j] = 0;
                        k--;
                        operate = 1;
                    }
                    //k = i;
                    //判断相邻的两个是否相等，相等即向上移动
                    if (k > 0 && array[k][j] == array[k - 1][j] && op == 0) {
                        if (array[k][j] != 0) {
                            score += array[k][j];
                            operate = 1;
                            op++;
                        }
                        array[k - 1][j] *= 2;
                        array[k][j] = 0;
                    }
                }
            }
        } else if (c == 'x') {
            operate = 0;
            for (int j = 3; j >= 0; j--) {
                //特殊情况
                if (array[0][j] != 0 && array[0][j] == array[1][j] && array[2][j] == array[3][j]) {
                    array[2][j] = array[1][j] * 2;
                    array[1][j] = 0;
                    array[0][j] = 0;
                    array[3][j] *= 2;
                    continue;
                }
                int op = 0;
                for (int i = 3; i >= 0; i--) {
                    int k = i;

                    while (k < 3 && array[k][j] > 0 && array[k + 1][j] == 0) {
                        array[k + 1][j] = array[k][j];
                        array[k][j] = 0;
                        k++;
                        operate = 1;
                    }
                    //k = i;
                    //判断相邻的两个是否相等，相等即向下移动
                    if (k < 3 && array[k][j] == array[k + 1][j] && op == 0) {
                        if (array[k][j] != 0) {
                            score += array[k][j];
                            operate = 1;
                            op++;
                        }
                        array[k + 1][j] *= 2;
                        array[k][j] = 0;
                    }
                }
            }

        }
    }
}
