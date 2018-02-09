package edu.unl.hcc;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeParser {

    public TimeParser(){}

    public static void main(String[] args) {

    }

    public String getHRTime(long timestamp) {
        Locale locale = new Locale("UTF-8");
        DateTimeZone timeZone = DateTimeZone.forID("America/Los_Angeles");
        DateTimeFormatter formatter = DateTimeFormat.forPattern("'year'-YYYY/'month'-MM/'day'-dd/'hour'-HH/").withZone(timeZone).withLocale(locale);

        DateTime bucket = new DateTime(formatter.getZone().convertLocalToUTC(timestamp, false));

        return bucket.toString(formatter);
    }
}
