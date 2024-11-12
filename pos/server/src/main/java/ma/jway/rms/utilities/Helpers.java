package ma.jway.rms.utilities;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dfWithTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public static Date parseDate(Object date) {
        try {
            return df.parse((String) date);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Date parseDateWithTime(Object date) {
        try {
            return dfWithTime.parse((String) date);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(value);
    }
}
