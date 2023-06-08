package admin.perfect.trader.Constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App_Utils {

    public static String GROW_LINK="https://groww.app.link/refe/hairskin";


    public static String getTimeAgo(long time) {
        final long diff = System.currentTimeMillis() - time;

        if (diff < 1) {
            return " just now";
        }
        if (diff < 60 * 1000) {
            //60 second
            if (diff / 1000 < 2) {
                return diff / 1000 + " second ago";
            } else {
                return diff / 1000 + " seconds ago";
            }
        } else if (diff < 60 * (60 * 1000)) {
            //60 minutes
            if (diff / (60 * 1000) < 2) {
                return diff / (60 * 1000) + " minute ago";
            } else {
                return diff / (60 * 1000) + " minutes ago";
            }
        } else if (diff < 24 * (60 * (60 * 1000))) {
            //24 hours
            if (diff / (60 * (60 * 1000)) < 2) {
                return diff / (60 * (60 * 1000)) + " hour ago";
            } else {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        } else {

            if (diff / (24 * (60 * (60 * 1000))) < 2) {
                return "Yesterday";
            } else {

                Date date = new Date(time);
                DateFormat f1 = new SimpleDateFormat("dd/MM/yyyy");
                return f1.format(date);
            }
        }
    }

}
