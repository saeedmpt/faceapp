package com.ai.chatapp.component;


import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ai.chatapp.BuildConfig;
import com.ai.chatapp.R;
import com.ai.chatapp.net.ApiService;
import com.ai.chatapp.utility.MyConstants;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import io.paperdb.Paper;


public class Global extends Application implements LifecycleObserver {

    public static Context context;
    public static int actionBarHeight;


    //محاسبه سال تاریخ تولد
    public static String convertBirthdateToAge(String birthdate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convertedDate = dateFormat.parse(birthdate);

            Calendar c = Calendar.getInstance();
            c.setTime(convertedDate);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);

            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();

            dob.set(year, month, day);
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            Integer ageInt = new Integer(age);
            if (ageInt < 0) {
                ageInt = 0;
            }
            String ageS = ageInt.toString();
            return ageS;

        } catch (Exception e) {
            e.printStackTrace();
            return birthdate;
        }
    }

    //زمان الان UTC
    public static String getCurrentTimeUtc() {
        /*Timestamp timeS = new Timestamp(cal.getTimeInMillis()
                - cal.get(Calendar.ZONE_OFFSET)
                - cal.get(Calendar.DST_OFFSET));*/
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    //زمان الان
    public static String getCurrentTime() {
        /*Timestamp timeS = new Timestamp(cal.getTimeInMillis()
                - cal.get(Calendar.ZONE_OFFSET)
                - cal.get(Calendar.DST_OFFSET));*/
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return df.format(date);
    }

    //فرمت زمان هر پیام
    public static String getTimeFromTimeStamp(String timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        CharSequence relative = null;
        try {
            /*relative = DateUtils.getRelativeTimeSpanString(df.parse(timestamp).getTime(), new Date().getTime(),
                    0L, DateUtils.FORMAT_ABBREV_ALL);*/
            relative = new SimpleDateFormat("HH:mm").format(df.parse(timestamp).getTime());
        } catch (ParseException e) {
            Log.e("Parse Exception adapter", "created at", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (null == relative) {
            return timestamp;
        } else {
            return relative.toString().replace(".", " ");
        }
    }

    //فرمت last seen کاربر در چت
    public static String getTimeRelative(String timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String relative = null;
        try {
            //  relative = getTimeAgo(df.parse(timestamp).getTime(), this);
            long longTimePm = df.parse(timestamp).getTime();
            relative = (String) DateUtils.getRelativeTimeSpanString(longTimePm, new Date().getTime(), 0L
                    , DateUtils.FORMAT_SHOW_DATE);
            if (relative.equalsIgnoreCase("yesterday")) {
                String time = new SimpleDateFormat("HH:mm").format(longTimePm);
                relative += " at " + time;
            }
            if (relative.contains("seconds")) {
                relative = "Just now";
            }
        } catch (ParseException e) {
            Log.e("Parse Exception adapter", "created at", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (relative == null) {
            return timestamp;
        } else {
            return relative.replace(".", " ");
        }
    }

    //فرمت بخش گروه ها
    public static String getTimeByDateFromTimeStamp(String timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        CharSequence relative = null;
        try {
            String laterdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(df.parse(timestamp).getTime());
            /*relative = DateUtils.getRelativeTimeSpanString(df.parse(timestamp).getTime(), new Date().getTime(),
                    0L, DateUtils.FORMAT_ABBREV_ALL);*/
            long diff = daysBetween(getCurrentTime(), laterdate);
            if (diff == 0) {
                relative = new SimpleDateFormat("HH:mm").format(df.parse(timestamp).getTime());
            } else if (diff == 1) {
                relative = new SimpleDateFormat("EEE").format(df.parse(timestamp).getTime());
            } else if (diff > 1 && diff < 365) {
                relative = new SimpleDateFormat("dd MMM").format(df.parse(timestamp).getTime());
            } else {
                relative = new SimpleDateFormat("yyyy-MM-dd").format(df.parse(timestamp).getTime());
            }
        } catch (ParseException e) {
            Log.e("Parse Exception adapter", "created at", e);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (null == relative) {
            return timestamp;
        } else {
            return relative.toString().replace(".", " ");
        }
    }

    //بخش بندی لیست پیام ها
    public static String getDateFromTimeStamp(String timestamp) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            String currentdate = new SimpleDateFormat("MMM dd").format(df.parse(timestamp).getTime());
            return currentdate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    //محاسبه تفاوت روز
    public static int daysBetween(String later, String before) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        myFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        int daysBetween = 0;
        try {
            Date dateBefore = myFormat.parse(before);
            Date dateAfter = myFormat.parse(later);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            daysBetween = (int) (difference / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daysBetween;
    }

    //تبدیل میلی ثانیه به فرمت دقیقه:ثانیه
    public static String getTimeFileForamated(long timeInmillisec) {
        return String.format(Locale.US, "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeInmillisec),
                TimeUnit.MILLISECONDS.toSeconds(timeInmillisec)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInmillisec)));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static String distanceUnitKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        float km = (float) (dist * 1.609344f);
        return String.format(Locale.US, "%.02f", km) + " km";
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public static String generateTempId() {
        return UUID.randomUUID().toString();
    }

    public static StringBuilder getTumbnailByName(String fullName) {
        if (fullName != null) {
            String[] nameSplit = fullName.split(" ");
            StringBuilder tumbName = new StringBuilder();
            for (int i = 0; i < Math.min(nameSplit.length, 2); i++) {
                tumbName.append(nameSplit[i].charAt(0));
            }
            return tumbName;
        } else {
            return new StringBuilder();
        }
    }


    private static PlayerProvider playerProvider;

    public static PlayerProvider getPlayerProvider(Context context) {
        if (playerProvider == null) {
            playerProvider = new PlayerProvider(context);
        }
        return playerProvider;
    }

    public static void checkStopPlayer(Context context, String groupId) {
        if (Global.getPlayerProvider(context).getGroupId().equals(groupId)) {
            Global.getPlayerProvider(context).stopPlayer();
        }
    }


    /*private static ShowNotificationProvider showNotificationProvider;

    public static ShowNotificationProvider getShowNotificationProvider() {
        if (showNotificationProvider == null) {
            showNotificationProvider = new ShowNotificationProvider();
        }
        return showNotificationProvider;
    }*/


    public static void sendEmail(Context context, String feedback) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{MyConstants.EMAIL_FEEDBACK});
        email.putExtra(Intent.EXTRA_SUBJECT, "feedback");
        email.putExtra(Intent.EXTRA_TEXT, feedback);

        //need this to prompts email client only
        email.setType("message/rfc822");

        context.startActivity(Intent.createChooser(email, "Choose an Email client :"));

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("TAG", "onConfigurationChanged: ");


    }


    public static boolean checkPermissionStorage(Context contInst) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            boolean value = ContextCompat.checkSelfPermission(contInst,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (!value)
                return false;
            return ContextCompat.checkSelfPermission(contInst,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }


    public static Integer getPermissionStorage(Activity context) {
        int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 123;
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                context.requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
                    context.startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    context.startActivityForResult(intent, 2296);
                }
            }
        } else {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                context.requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }
        return ASK_MULTIPLE_PERMISSION_REQUEST_CODE;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Paper.init(context);
        //creashReport
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(true) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(false) //default: true
                .trackActivities(true) //default: false
                .minTimeBetweenCrashesMs(3000) //default: 3000
                .errorDrawable(R.mipmap.ic_launcher) //default: bug image
                .apply();
        /*ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(MyConstants.FONT_NAME)
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());*/

        MobileAds.initialize(context);
    }


    private static ApiService apiService;

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = new ApiService();
        }
        return apiService;

    }


    public static String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public static void hideKeyboard(Context activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = ((Activity) activity).getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showKeyboard(Context activity, EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            editText.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String convertObjToString(Object clsObj) {
        //convert object  to string json
        String jsonSender = new Gson().toJson(clsObj, new TypeToken<Object>() {
        }.getType());
        return jsonSender;
    }

    public static <T> ArrayList<T> convertStringToList(String strListObj) {
        //convert string json to object List
        return new Gson().fromJson(strListObj, new TypeToken<List<T>>() {
        }.getType());
    }

    public static <T> T convertStringToObj(String strObj, Class<T> classOfT) {
        //convert string json to object
        return new Gson().fromJson(strObj, (Type) classOfT);
    }

    public static JsonObject convertStringToJsonObj(String strObj) {
        //convert string json to object
        return new Gson().fromJson(strObj, JsonObject.class);
    }

    public static <T> String convertListObjToString(List<T> listObj) {
        //convert object list to string json for
        return new Gson().toJson(listObj, new TypeToken<List<T>>() {
        }.getType());
    }

    public static <T> String convertTreeMapToString(List<T> listObj, int pos) {
        //convert object  to string json
        String json = new Gson().toJson(listObj.get(pos), TreeMap.class);
        return json;
    }


    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }


    public static Context getAppContext() {
        return context;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "");

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getFileSize(long bytes) {

        long logSize = (long) log2(bytes);
        final String[] suffixes = new String[]{" B", " KiB", " MiB", " GiB", " TiB", " PiB", " EiB", " ZiB", " YiB"};

        int suffixIndex = (int) (logSize / 10); // 2^10 = 1024

        double displaySize = bytes / Math.pow(2, suffixIndex * 10);
        DecimalFormat df = new DecimalFormat("#.##");
        String unit = "";
        switch (suffixIndex) {
            case 0:
                unit = "بایت";
                break;
            case 1:
                unit = "کیلوبایت";
                break;
            case 2:
                unit = "مگابایت";
                break;
            case 3:
                unit = "گیگابایت";
                break;
        }
        return df.format(displaySize) + unit;
    }

    public static double log2(long n) {
        // Implement this but without inaccuracies due to FP math.
        // Just count the number of leading zeros and do the math.
        return (Math.log(n) / Math.log(2));
    }


    public static String humanReadableByteCountMg(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %s", bytes / Math.pow(unit, exp), pre);
    }

    public static int converDpToPixel(int dp, Context context) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        ));
    }


    public static boolean NetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;

        }
    }


    public static int getSizeScreen(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }


    public static int getSizeScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height;
    }


    public static void intentToPackage(Context context, String link, String package_name) {
        String url = link;
        Intent i = new Intent();
        i.setPackage(package_name);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static void openBrowser(Context context, String link) {
        if (isPackageInstalled(package_chrome) &&
                isPackageInstalledCheckVersion(package_chrome, 60)) {
            intentToPackage(context, link, package_chrome);
        } else if (isPackageInstalled(package_firefox) &&
                isPackageInstalledCheckVersion(package_firefox, 60)) {
            intentToPackage(context, link, package_firefox);
        } else if (isPackageInstalled(package_opera) &&
                isPackageInstalledCheckVersion(package_opera, 47)) {
            intentToPackage(context, link, package_opera);
        } else if (isPackageInstalled(package_chrome)) {
            intentToPackage(context, link, package_chrome);
        } else if (isPackageInstalled(package_firefox)) {
            intentToPackage(context, link, package_firefox);
        } else if (isPackageInstalled(package_opera)) {
            intentToPackage(context, link, package_opera);
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(browserIntent);
        }
    }

    private static boolean isPackageInstalled(String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String BASE_URL_INSTAGRAM = "http://instagram.com/";

    public static void openInstagram(Context context, String link) {
        Uri uri = Uri.parse("http://instagram.com/_u/" + link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(INSTAGRAM_PACKAGE);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(BASE_URL_INSTAGRAM + link));
            context.startActivity(intent);
        }
    }


    private static boolean isPackageInstalledCheckVersion(String packageName, int version) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            int versionCode = pInfo.versionCode;
            return versionCode >= version;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static String package_chrome = "com.android.chrome";
    static String package_firefox = "org.mozilla.firefox";
    static String package_opera = "com.opera.browser";

}
