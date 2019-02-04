package net.sakuratrak.schoolstorycollection.core;

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
        double sum = 0;
        for (double val :
                values) {
            sum+=val;
        }
        return sum / values.length;
    }

    public static double calcAvg(int... values){
        double sum = 0;
        for (int val :
                values) {
            sum+=val;
        }
        return sum / values.length;
    }
}
