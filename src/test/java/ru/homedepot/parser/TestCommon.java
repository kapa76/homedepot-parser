package ru.homedepot.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TestCommon {

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);


        String inputPattern = "dd MMM HH:mm";
        String localDateTime = "30 Oct 03:23";
        try {
            cal.setTime((new SimpleDateFormat(inputPattern, Locale.ENGLISH).parse(localDateTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("cal: " + cal.getTime());


    }
}
