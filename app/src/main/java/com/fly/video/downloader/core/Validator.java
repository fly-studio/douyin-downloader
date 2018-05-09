package com.fly.video.downloader.core;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean equals(String str, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? str.equals(matcher.group(0)) : false;
    }

    public static boolean contains(String str, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static boolean containsUrl(String str)
    {
        return contains(str, Patterns.WEB_URL);
    }
}
