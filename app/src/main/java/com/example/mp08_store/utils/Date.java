package com.example.mp08_store.utils;

import android.net.ParseException;

public class Date {
    private final int day, month, year;
    private boolean isValid = true;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        if(this.day == 0) this.isValid = false;
    }

    public String getDate(String infix) {
        if(infix == null) infix = "/";
        return this.formatNumber(this.day) + infix + this.formatNumber(this.month) + infix + this.year;
    }

    public String getDateAmerican(String infix) {
        if(infix == null) infix = "/";
        return this.formatNumber(this.month) + infix + this.formatNumber(this.day) + infix + this.year;
    }

    public String getDateSql(String infix) {
        if(infix == null) infix = "/";
        return this.year + infix + this.formatNumber(this.month) + infix + this.formatNumber(this.day);
    }

    public static Date parse(String date, String infix) {
        String[] numbers = date.split(infix == null ? "/" : infix);
        try {
            int d = Integer.parseInt(numbers[0]);
            int m = Integer.parseInt(numbers[1]);
            int y = Integer.parseInt(numbers[2]);
            return new Date(d, m, y);
        } catch (ParseException | NumberFormatException e) {
            return new Date(0, 0, 0);
        }
    }
    public static Date parseAmerican(String date, String infix) {
        String[] numbers = date.split(infix == null ? "/" : infix);
        try {
            int d = Integer.parseInt(numbers[1]);
            int m = Integer.parseInt(numbers[0]);
            int y = Integer.parseInt(numbers[2]);
            return new Date(d, m, y);
        } catch (ParseException | NumberFormatException e) {
            return new Date(0, 0, 0);
        }
    }
    public static Date parseSql(String date, String infix) {
        String[] numbers = date.split(infix == null ? "/" : infix);
        try {
            int d = Integer.parseInt(numbers[2]);
            int m = Integer.parseInt(numbers[1]);
            int y = Integer.parseInt(numbers[0]);
            return new Date(d, m, y);
        } catch (ParseException | NumberFormatException e) {
            return new Date(0, 0, 0);
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    private String formatNumber(int n) {
        return n < 10 ? "0" + n : n+"";
    }
}
