package com.omkarmoghe.pokemap.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Nikolay Unuchek on 25.07.2016.
 */
public class PokeUtils {
    public static String getDurationBreakdown(long millis) {
        if(millis < 0) {
            return "Expired";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return(sb.toString());
    }
}
