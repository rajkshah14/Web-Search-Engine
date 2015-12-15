package project08.misc.log;

import java.text.DecimalFormat;

/**
 * Created by nicos on 24.November.15.
 */
public class Log {

    /*
    Level 0 = none
    Level 1 = Error
    Level 2 = Info
     */

    private static int loglevel = 2;

    public static void setlogLevel(int level){
        loglevel = level;
    }

    public static void logMessage(String message, int level){
        if(level <= loglevel )
        System.out.println(message);
    }

    public static void logError(String message){
        logMessage(message, 1);
    }

    public static void logInfo(String message){
        logMessage(message, 2);
    }

    public static void logException(Exception e){
        if(loglevel > 0)
            e.printStackTrace();
    }

    public static long startTimer(){
        return System.currentTimeMillis();
    }

    public static void logTimer(String tag, long timer){
        if(loglevel>=2){
            long stop =  System.currentTimeMillis();
            long time = stop-timer;
            DecimalFormat df = new DecimalFormat("#.00");
            System.out.println(tag + ": " + df.format(time/1000));
        }
    }

}
