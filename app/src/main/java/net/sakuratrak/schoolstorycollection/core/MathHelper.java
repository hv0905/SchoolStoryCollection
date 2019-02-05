package net.sakuratrak.schoolstorycollection.core;

import java.util.Random;

public final class MathHelper {

    public static double calcVariance(double avg,double[] values){
        double squareSum = 0;
        for (double val :
                values) {
            squareSum+=Math.pow(val - avg,2);
        }
        return squareSum / values.length;
    }

    public static double calcVariance(double avg,int[] values) {
        double squareSum = 0;
        for (int val :
                values) {
            squareSum+=Math.pow(val - avg,2);
        }
        return squareSum / values.length;
    }

    public static double calcVariance(double[] vals){
        return calcVariance(calcAvg(vals),vals);
    }

    public static double calcVariance(int[] vals){
        return calcVariance(calcAvg(vals),vals);
    }

    public static double calcAvg(double... values){
        return calcSum(values) / values.length;
    }

    public static double calcAvg(int... values){
        return calcSum(values) / (double)values.length;
    }

    public static int calcSum(int... values) {
        int sum = 0;
        for (int val :
                values) {
            sum+=val;
        }
        return sum;
    }

    public static double calcSum(double... values) {
        double sum = 0;
        for (double val :
                values) {
            sum+=val;
        }
        return sum;
    }

    public static int getSingleRandomItemWithProportion(Random random, int[] items, int sum) {
        int randomNum = random.nextInt(sum);
        int lop = 0;
        for (int i = 0; i < items.length; i++) {
            lop += items[i];
            if (randomNum < lop) return i;
        }
        return -1;
    }



    public static int getSingleRandomItemWithProportion(int[] items) {
        return getSingleRandomItemWithProportion(items, calcSum(items));
    }

    private static int getSingleRandomItemWithProportion(int[] items, int sum) {
        return getSingleRandomItemWithProportion(new Random(),items, sum);
    }

    public static int[] getMultiRandomItemWithProportion(int[] items, int n, int sum) {
        Random rd = new Random();
        if(items.length < n){
            throw new IllegalArgumentException();
        }
        int[] result = new int[n];
        for(int i = 0;i<n;i++){
            int index = getSingleRandomItemWithProportion(rd,items,sum);
            boolean goFlag = true;
            for(int j = 0;j<i;j++){
                if(result[j] == index){
                    goFlag = false;
                    i--;
                    break;
                }
            }
            if(goFlag) result[i] = index;

        }

        return result;
    }

    public static int[] getMultiRandomItemWithProportion(int[] items, int n) {
        return getMultiRandomItemWithProportion(items, n, calcSum(items));
    }

    public static int[] getMultiRandomItem(int in,int n){
        Random rd = new Random();
        if(in < n){
            throw new IllegalArgumentException();
        }
        int[] result = new int[n];
        for(int i = 0;i<n;i++){
            int index = rd.nextInt(in);
            boolean goFlag = true;
            for(int j = 0;j < i;j++){
                if(result[j] == index){
                    goFlag = false;
                    i--;
                    break;
                }
            }
            if(goFlag) result[i] = index;

        }

        return result;
    }
}
