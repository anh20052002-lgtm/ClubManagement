package clubmanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class DateUtils {
    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private DateUtils() {
    }

    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        format.setLenient(false);

        try {
            format.parse(date);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }
}