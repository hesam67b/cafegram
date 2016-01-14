package com.negahetazehco.cafetelegram;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.Random;
import java.util.UUID;

public class Helper {

    public static void resetApp(Activity activity) {
        PendingIntent localPendingIntent = PendingIntent.getActivity(activity, 0,
                activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager)activity.getSystemService("alarm")).set(1, 150L + System.currentTimeMillis(), localPendingIntent);
        System.exit(2);
    }

    public static boolean isPackageInstalled(Context context, String packagename) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo;
        try {
            //pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            appInfo = pm.getApplicationInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public static String getRandomString() {

        int sizeOfRandomString = getRandom(8, 13);
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static int getRandom(int start, int end) {

        Random r = new Random();
        return r.nextInt(end - start) + start;

    }

    public static String getUniquePsuedoID()
    {
        String str1 = "91" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10;
        try
        {
            String str3 = Build.class.getField("SERIAL").get(null).toString();
            String str4 = new UUID(str1.hashCode(), str3.hashCode()).toString();
            return str4;
        }
        catch (Exception localException)
        {
            String str2 = new UUID(str1.hashCode(), "serial".hashCode()).toString();
            return str2;
        }
    }
}
