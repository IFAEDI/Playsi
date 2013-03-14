package controllers.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Benjamin
 * Date: 14/03/13
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy à HH:mm:ss");

    public static String formaterDate(Date d) {
        return sdf.format(d);
    }
}
