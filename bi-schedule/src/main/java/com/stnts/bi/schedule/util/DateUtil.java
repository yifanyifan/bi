package com.stnts.bi.schedule.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {


    public static String foramtDateFromNow(String format, int period) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        LocalDate today = LocalDate.now();

        LocalDate localDate = today.plusDays(period);

        return formatter.format(localDate);
    }

    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }
}
