package com.negahetazehco.cafetelegram;

import android.content.SharedPreferences;
import android.graphics.Color;

import org.telegram.messenger.ApplicationLoader;

public class Setting {

    public static String THEME_COLOR_LABLE = "ThemeColor";
    public static int THEME_COLOR_DEF_VALUE = -5373056;
    public static String DATE_FARSI_STATUS_LABLE = "DateFarsi";
    public static boolean DATE_FARSI_STATUS_VALUE = true;
    public static String HIDDEN_MODE_STATUS_LABLE = "HiddenMode";
    public static boolean HIDDEN_MODE_STATUS_VALUE = false;
    public static String HIDDEN_TYPING_STATUS_LABLE = "HiddenTyping";
    public static boolean HIDDEN_TYPING_STATUS_VALUE = false;
    public static String FONT_FAMILY_LABLE = "FontFamily";
    public static String FONT_FAMILY_VALUE = "iransans.ttf";
    public static String HIDDEN_FORWARDED_STATUS_LABLE = "HiddenForwarded";
    public static boolean HIDDEN_FORWARDED_STATUS_VALUE = false;
    public static String HIDDEN_OPTION_STATUS_LABLE = "HiddenOptions";
    public static String HIDDEN_OPTION_STATUS_VALUE = "dsadw84adsw8d4asd";
    public static String IS_FIRST_TIME_STATUS_LABLE = "IsFirstTime";
    public static int IS_FIRST_TIME_STATUS_VALUE = 0;
    private static SharedPreferences settings;

    public static void init(SharedPreferences preferences) {
        settings = preferences;
        set(THEME_COLOR_LABLE, get(THEME_COLOR_LABLE, THEME_COLOR_DEF_VALUE));
        set(DATE_FARSI_STATUS_LABLE, get(DATE_FARSI_STATUS_LABLE, DATE_FARSI_STATUS_VALUE));
    }

    public static int get(String lable, int defaultValue) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        try{
            defaultValue = settings.getInt(lable, defaultValue);
        } catch (Exception e) {}
        return defaultValue;
    }

    public static void set(String lable, int value) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(lable, value);
        editor.commit();
    }

    public static boolean get(String lable, boolean defaultValue) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        try{
            defaultValue = settings.getBoolean(lable, defaultValue);
        } catch (Exception e) {}
        return defaultValue;
    }

    public static void set(String lable, boolean value) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(lable, value);
        editor.commit();
    }

    public static String get(String lable, String defaultValue) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        try{
            defaultValue = settings.getString(lable, defaultValue);
        } catch (Exception e) {}
        return defaultValue;
    }

    public static void set(String lable, String value) {
        settings = ApplicationLoader.applicationContext.getSharedPreferences("telegram_config", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(lable, value);
        editor.commit();
    }
}
