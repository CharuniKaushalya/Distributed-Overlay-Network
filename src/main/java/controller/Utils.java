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
}
