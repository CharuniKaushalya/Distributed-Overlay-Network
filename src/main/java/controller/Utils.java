package main.java.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Thilini on 10/24/2017.
 */
public class Utils {

	public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
	
    public static double getStandardDeviation(Object[] arr, double mean){
    double variance = 0, sd =0;
    double [] temp =  new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            temp[i] = (double)(Integer)arr[i] - mean;
            temp[i] = Math.pow(temp[i], 2); //to get the (x-average)2
            variance += temp[i];
        }
        variance = variance / (arr.length-1); // sample variance
        sd = Math.sqrt(variance);
        return sd;
    }
}
