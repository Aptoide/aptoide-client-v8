/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.permissions.ApkPermission;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UnknownFormatConversionException;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.net.ConnectivityManager.TYPE_ETHERNET;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by neuro on 26-05-2016.
 */
public class AptoideUtils {

  public static class Core {

    private static final String TAG = "Core";

    public static String getDefaultVername(Context context) {
      String verString = "";
      try {
        verString = context.getPackageManager()
            .getPackageInfo(context.getPackageName(), 0).versionName;
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }

      return "aptoide-" + verString;
    }

    public static int getVerCode(Context context) {
      PackageManager manager = context.getPackageManager();
      try {
        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
        return info.versionCode;
      } catch (PackageManager.NameNotFoundException e) {
        Logger.e(TAG, e);
        return -1;
      }
    }
  }

  public static class AlgorithmU {

    private static final String TAG = AlgorithmU.class.getName();

    public static String computeSha1(String text) {
      try {
        return convToHex(computeSha1(text.getBytes("iso-8859-1")));
      } catch (UnsupportedEncodingException e) {
        Logger.e(TAG, "computeSha1(String)", e);
      }
      return "";
    }

    private static String convToHex(byte[] data) {
      final StringBuilder buffer = new StringBuilder();
      for (byte b : data) {
        buffer.append(Integer.toString((b & 0xff) + 0x100, 16)
            .substring(1));
      }
      return buffer.toString();
    }

    private static byte[] computeSha1(byte[] bytes) {
      MessageDigest md;
      try {
        md = MessageDigest.getInstance("SHA-1");
        md.update(bytes, 0, bytes.length);
        return md.digest();
      } catch (NoSuchAlgorithmException e) {
        Logger.e(TAG, e);
      }

      return new byte[0];
    }

    public static String computeHmacSha1(String value, @NonNull String keyString) {
      try {
        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));
        return convToHex(bytes);
      } catch (NoSuchAlgorithmException e) {
        Logger.e(TAG, e);
      } catch (UnsupportedEncodingException e) {
        Logger.e(TAG, e);
      } catch (InvalidKeyException e) {
        Logger.e(TAG, e);
      }
      return "";
    }

    public static String computeSha1WithColon(byte[] bytes) {
      return convToHexWithColon(computeSha1(bytes)).toUpperCase(Locale.ENGLISH);
    }

    private static String convToHexWithColon(byte[] data) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < data.length; i++) {
        int halfbyte = (data[i] >>> 4) & 0x0F;
        int two_halfs = 0;
        do {
          if ((0 <= halfbyte) && (halfbyte <= 9)) {
            buf.append((char) ('0' + halfbyte));
          } else {
            buf.append((char) ('a' + (halfbyte - 10)));
          }
          halfbyte = data[i] & 0x0F;
        } while (two_halfs++ < 1);

        if (i < data.length - 1) {
          buf.append(":");
        }
      }
      return buf.toString();
    }

    public static String computeMd5(@NonNull PackageInfo packageInfo) {

      String sourceDir = packageInfo.applicationInfo.sourceDir;
      File apkFile = new File(sourceDir);
      return computeMd5(apkFile);
    }

    public static String computeMd5(File f) {
      long time = System.currentTimeMillis();
      byte[] buffer = new byte[1024];
      int read, i;
      String md5hash;
      try {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new FileInputStream(f);
        while ((read = is.read(buffer)) > 0) {
          digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        md5hash = bigInt.toString(16);
        is.close();
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }

      if (md5hash.length() != 33) {
        String tmp = "";
        for (i = 1; i < (33 - md5hash.length()); i++) {
          tmp = tmp.concat("0");
        }
        md5hash = tmp.concat(md5hash);
      }
      Logger.v(TAG, "computeMd5: duration: " + (System.currentTimeMillis() - time) + " ms");
      return md5hash;
    }

    // deprecated since no usage was found.
    @Deprecated public static int randomBetween(int min, int max) {
      int skewedMax = max - min;
      if (skewedMax <= 0) {
        throw new IllegalStateException("Minimum < maximum");
      }
      return new Random().nextInt(skewedMax + 1) + min;
    }
  }

  public static final class MathU {

    public static int leastCommonMultiple(int[] input) {
      int result = input[0];
      for (int i = 1; i < input.length; i++)
        result = leastCommonMultiple(result, input[i]);
      return result;
    }

    /**
     * Uses formulae: lcm(a,b)= ( ( |a| / gcm(a,b) ) * |b| ) <p> <p>Where gcd(a,b) is the function
     * {@link #greatestCommonDivisor(int, int)}</p>
     *
     * @return The least commong multiple between a and b.
     */
    private static int leastCommonMultiple(int a, int b) {
      //return a * (b / greatestCommonDivisor(a, b));
      if (a == 0 && b == 0) {
        return 0;
      }
      return (Math.abs(a) / greatestCommonDivisor(a, b)) * Math.abs(b);
    }

    /**
     * Uses Euclid's algorithm: <p>gcd(a,0) = 0</p> <p>gcd(a,b) = gcd(b, a mod b)</p>
     *
     * @return The greatest common divisor between a and b.
     */
    private static int greatestCommonDivisor(int a, int b) {
      while (b > 0) {
        int temp = b;
        b = a % b; // % is remainder
        a = temp;
      }
      return a;
    }

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh,
        double toLow, double toHigh) {
      return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
      return Math.min(Math.max(value, low), high);
    }
  }

  public static class RegexU {

    private static final String STORE_ID_FROM_GET_URL = "store_id\\=(\\d+)";
    private static final String STORE_NAME_FROM_GET_URL = "store_name\\/(.*?)\\/";
    private static final String SPLIT_URL_EXTENSION = "\\.(?=[^\\.]+$)";

    private static Pattern STORE_ID_FROM_GET_URL_PATTERN;
    private static Pattern STORE_NAME_FROM_GET_URL_PATTERN;

    public static Pattern getStoreIdFromGetUrlPattern() {
      if (STORE_ID_FROM_GET_URL_PATTERN == null) {
        STORE_ID_FROM_GET_URL_PATTERN = Pattern.compile(STORE_ID_FROM_GET_URL);
      }

      return STORE_ID_FROM_GET_URL_PATTERN;
    }

    public static Pattern getStoreNameFromGetUrlPattern() {
      if (STORE_NAME_FROM_GET_URL_PATTERN == null) {
        STORE_NAME_FROM_GET_URL_PATTERN = Pattern.compile(STORE_NAME_FROM_GET_URL);
      }

      return STORE_NAME_FROM_GET_URL_PATTERN;
    }
  }

  public static final class ScreenU {

    public static final float REFERENCE_WIDTH_DPI = 360;

    private static ScreenUtilsCache screenWidthInDipCache = new ScreenUtilsCache();

    private static int displayWidthCacheLandscape = -1;
    private static int displayWidthCachePortrait = -1;

    public static int getCachedDisplayWidth(int orientation, WindowManager windowManager) {
      if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        if (displayWidthCacheLandscape == -1) {
          Display display = windowManager.getDefaultDisplay();
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            display.getSize(point);
            displayWidthCacheLandscape = point.x;
          } else {
            displayWidthCacheLandscape = display.getWidth();
          }
        }
        return displayWidthCacheLandscape;
      } else {
        if (displayWidthCachePortrait == -1) {
          Display display = windowManager.getDefaultDisplay();
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point point = new Point();
            display.getSize(point);
            displayWidthCachePortrait = point.y;
          } else {
            displayWidthCachePortrait = display.getHeight();  // test this if you use it please
          }
        }
        return displayWidthCachePortrait;
      }
    }

    public static float getScreenWidthInDip(WindowManager windowManager, Resources resources) {
      if (getCurrentOrientation(resources) != screenWidthInDipCache.orientation) {
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay()
            .getMetrics(dm);
        screenWidthInDipCache.set(getCurrentOrientation(resources), dm.widthPixels / dm.density);
      }

      return screenWidthInDipCache.value;
    }

    public static int getCurrentOrientation(Resources resources) {
      return resources.getConfiguration().orientation;
    }

    public static int getPixelsForDip(int dipValue, Resources resources) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
          resources.getDisplayMetrics());
    }

    public static int getNumericScreenSize(Resources resources) {
      int size = getScreenSizeInt(resources);
      return (size + 1) * 100;
    }

    public static String getScreenSize(Resources resources) {
      return Size.values()[getScreenSizeInt(resources)].name()
          .toLowerCase(Locale.ENGLISH);
    }

    private static int getScreenSizeInt(Resources resources) {
      return resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    public static int getDensityDpi(WindowManager windowManager) {
      DisplayMetrics metrics = new DisplayMetrics();
      windowManager.getDefaultDisplay()
          .getMetrics(metrics);

      int dpi = metrics.densityDpi;

      if (dpi <= 120) {
        dpi = 120;
      } else if (dpi <= 160) {
        dpi = 160;
      } else if (dpi <= 213) {
        dpi = 213;
      } else if (dpi <= 240) {
        dpi = 240;
      } else if (dpi <= 320) {
        dpi = 320;
      } else if (dpi <= 480) {
        dpi = 480;
      } else {
        dpi = 640;
      }

      return dpi;
    }

    public static File takeScreenshot(Activity a, String mPath, String fileName) {
      Bitmap bitmap;
      FileUtils.createDir(mPath);
      View v1 = a.getWindow()
          .getDecorView()
          .getRootView();
      v1.setDrawingCacheEnabled(true);
      try {
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
      } catch (Exception e) {
        Logger.e("FeedBackActivity-screenshot", "Exception: " + e.getMessage());
        return null;
      }

      OutputStream fout = null;
      File imageFile = new File(mPath, fileName);
      try {
        imageFile.createNewFile();
        fout = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
        fout.flush();
        fout.close();
      } catch (FileNotFoundException e) {
        Logger.e("FeedBackActivity-screenshot", "FileNotFoundException: " + e.getMessage());
        return null;
      } catch (IOException e) {
        Logger.e("FeedBackActivity-screenshot", "IOException: " + e.getMessage());
        return null;
      }
      return imageFile;
    }

    /**
     * get screen size in "pixels", i.e. touchevent/view units.
     * on my droid 4, this is 360x640 or 540x960
     * depending on whether the app is in screen compatibility mode
     * (i.e. targetSdkVersion<=10 in the manifest) or not.
     */
    // method deprecated since no usage was found.
    @Deprecated public static String getScreenSizePixels(Resources resources) {
      Configuration config = resources.getConfiguration();
      DisplayMetrics dm = resources.getDisplayMetrics();
      // Note, screenHeightDp isn't reliable
      // (it seems to be too small by the height of the status bar),
      // but we assume screenWidthDp is reliable.
      // Note also, dm.widthPixels,dm.heightPixels aren't reliably pixels
      // (they get confused when in screen compatibility mode, it seems),
      // but we assume their ratio is correct.
      double screenWidthInPixels = (double) config.screenWidthDp * dm.density;
      double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;
      return (int) (screenWidthInPixels + .5) + "x" + (int) (screenHeightInPixels + .5);
    }

    // deprecated since no usage was found
    @Deprecated public enum Size {
      notfound, small, normal, large, xlarge;

      private static final String TAG = Size.class.getSimpleName();

      public static Size lookup(String screen) {
        try {
          return valueOf(screen);
        } catch (Exception e) {
          Logger.e(TAG, e);
          return notfound;
        }
      }
    }

    private static class ScreenUtilsCache {

      private int orientation = -1;
      private float value;

      public void set(int currentOrientation, float value) {
        this.orientation = currentOrientation;
        this.value = value;
      }
    }
  }

  public static final class StringU {

    /**
     * <p>Joins the elements of the provided {@code Iterable} into a single String containing the
     * provided elements.</p> <p> <p>No delimiter is added
     * before
     * or after the list. A {@code null} separator is the same as an empty String ("").</p>
     *
     * @param iterable the {@code Iterable} providing the values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     *
     * @return the joined String, {@code null} if null iterator input
     *
     * @since 2.3
     */
    public static String join(final Iterable<?> iterable, final String separator) {
      if (iterable == null) {
        return null;
      }
      return join(iterable.iterator(), separator);
    }

    /**
     * <p>Joins the elements of the provided {@code Iterator} into a single String containing the
     * provided elements.</p> <p> <p>No delimiter is added
     * before
     * or after the list. A {@code null} separator is the same as an empty String ("").</p>
     *
     * @param iterator the {@code Iterator} of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     *
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterator<?> iterator, final String separator) {

      // handle null, zero and one elements before building a buffer
      if (iterator == null) {
        return null;
      }
      if (!iterator.hasNext()) {
        return "";
      }
      final Object first = iterator.next();
      if (!iterator.hasNext()) {
        @SuppressWarnings("deprecation") // ObjectUtils.toString(Object) has been deprecated in 3.2
        final String result = toString(first);
        return result;
      }

      // two or more elements
      final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
      if (first != null) {
        buf.append(first);
      }

      while (iterator.hasNext()) {
        if (separator != null) {
          buf.append(separator);
        }
        final Object obj = iterator.next();
        if (obj != null) {
          buf.append(obj);
        }
      }
      return buf.toString();
    }

    public static String toString(Object obj) {
      return obj == null ? "" : obj.toString();
    }

    public static String withSuffix(long count) {
      if (count < 1000) {
        return String.valueOf(count);
      }
      int exp = (int) (Math.log(count) / Math.log(1000));
      return String.format(Locale.ENGLISH, "%d %c", (int) (count / Math.pow(1000, exp)),
          "kMBTPE".charAt(exp - 1));
    }

    public static String formatBytesToBits(long bytes, boolean speed) {
      bytes *= 8;
      int unit = 1024;
      if (bytes < unit) {
        return bytes + " B";
      }
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      String pre = ("KMGTPE").charAt(exp - 1) + "";
      String string = String.format(Locale.ENGLISH, "%.1f %sb", bytes / Math.pow(unit, exp), pre);
      return speed ? string + "ps" : string;
    }

    /**
     * @param bytes file size
     *
     * @return formatted string for file file showing a Human perceptible file size
     */
    public static String formatBytes(long bytes, boolean speed) {
      int unit = 1024;
      if (bytes < unit) {
        return bytes + " B";
      }
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      String pre = ("KMGTPE").charAt(exp - 1) + "";
      String string = String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
      return speed ? string + "/s" : string;
    }

    public static String getResString(@StringRes int stringResId, Resources resources) {
      return resources.getString(stringResId);
    }

    public static String getFormattedString(@StringRes int resId, Resources resources,
        Object... formatArgs) {
      String result;
      try {
        result = resources.getString(resId, formatArgs);
      } catch (UnknownFormatConversionException ex) {
        final String resourceEntryName = resources.getResourceEntryName(resId);
        final String displayLanguage = Locale.getDefault()
            .getDisplayLanguage();
        Logger.e("UnknownFormatConversion",
            "String: " + resourceEntryName + " Locale: " + displayLanguage);
        result = ResourseU.getString(resId, resources);
      }
      return result;
    }

    public static String commaSeparatedValues(List<?> list) {
      String s = "";

      if (list.size() > 0) {
        s = list.get(0)
            .toString();

        for (int i = 1; i < list.size(); i++) {
          s += "," + list.get(i)
              .toString();
        }
      }

      return s;
    }

    public static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
      Map<String, String> query_pairs = new LinkedHashMap<>();
      String query = uri.getQuery();
      if (query != null) {
        String[] pairs = query.split("&");
        if (pairs != null) {
          for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx + 1 < pair.length()) {
              query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                  URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
          }
        }
      }
      return query_pairs;
    }
  }

  public static class SystemU {

    public static final String TERMINAL_INFO =
        getModel() + "(" + getProduct() + ")" + ";v" + getRelease() + ";" + System.getProperty(
            "os.arch");
    private static final String TAG = "SystemU";

    public static String getProduct() {
      return Build.PRODUCT.replace(";", " ");
    }

    public static String getModel() {
      return Build.MODEL.replaceAll(";", " ");
    }

    public static String getRelease() {
      return Build.VERSION.RELEASE.replaceAll(";", " ");
    }

    public static String getGlEsVer(ActivityManager activityManager) {
      return activityManager.getDeviceConfigurationInfo()
          .getGlEsVersion();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) @SuppressWarnings("deprecation")
    public static String getAbis() {
      final String[] abis = getSdkVer() >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS
          : new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i < abis.length; i++) {
        builder.append(abis[i]);
        if (i < abis.length - 1) {
          builder.append(",");
        }
      }
      return builder.toString();
    }

    public static int getSdkVer() {
      return Build.VERSION.SDK_INT;
    }

    public static String getCountryCode(Resources resources) {
      return resources.getConfiguration().locale.getLanguage()
          + "_"
          + resources.getConfiguration().locale.getCountry();
    }

    public static PackageInfo getPackageInfo(String packageName, PackageManager packageManager) {
      try {
        return packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      return null;
    }

    // method deprecated since no usage was found.

    /**
     * Use InstallManager or other entity such as the Installed repository in the engine to obtain
     * the installed apps
     */
    @Deprecated public static List<PackageInfo> getUserInstalledApps(
        PackageManager packageManager) {
      List<PackageInfo> tmp = new LinkedList<>();

      for (PackageInfo packageInfo : getAllInstalledApps(packageManager)) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
          tmp.add(packageInfo);
        }
      }

      return tmp;
    }

    public static List<PackageInfo> getAllInstalledApps(PackageManager packageManager) {
      return packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
    }

    public static String getApkLabel(PackageInfo packageInfo, PackageManager packageManager) {
      return packageInfo.applicationInfo.loadLabel(packageManager)
          .toString();
    }

    public static String getApkIconPath(PackageInfo packageInfo) {
      return "android.resource://"
          + packageInfo.packageName
          + "/"
          + packageInfo.applicationInfo.icon;
    }

    public static void openApp(String packageName, PackageManager packageManager, Context context) {
      Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);

      if (launchIntentForPackage != null) {
        context.startActivity(launchIntentForPackage);
      }
    }

    public static String getConnectionType(ConnectivityManager connectivityManager) {
      final ConnectivityManager manager = connectivityManager;
      final NetworkInfo info = manager.getActiveNetworkInfo();

      if (info != null && info.getTypeName() != null) {
        switch (info.getType()) {
          case TYPE_ETHERNET:
            return "ethernet";
          case TYPE_WIFI:
            return "wifi";
          case TYPE_MOBILE:
            return "mobile";
        }
      }
      return "unknown";
    }

    public static String getCarrierName(TelephonyManager telephonyManager) {
      return telephonyManager.getNetworkOperatorName();
    }

    public static File readLogs(String mPath, String fileName) {

      Process process = null;
      try {
        process = Runtime.getRuntime()
            .exec("logcat -d");
      } catch (IOException e) {
        Logger.e("FeedBackActivity-readLogs", "IOException: " + e.getMessage());
        return null;
      }
      FileOutputStream outputStream;
      FileUtils.createDir(mPath);
      File logsFile = new File(mPath, fileName);
      StringBuilder log = new StringBuilder();
      log.append("Android Build Version: " + Build.VERSION.SDK_INT + "\n");
      log.append("Build Model: " + Build.MODEL + "\n");
      log.append("Device: " + Build.DEVICE + "\n");
      log.append("Brand: " + Build.BRAND + "\n");
      log.append("CPU: " + Build.CPU_ABI + "\n");
      log.append("\nLogs:\n");
      try {
        outputStream = new FileOutputStream(logsFile);
        BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = null;
        int linecount = 0;
        while (linecount < 100 && (line = bufferedReader.readLine()) != null) {

          log.append(line + "\n");
          linecount++;
        }
        outputStream.write(log.toString()
            .getBytes());
      } catch (IOException e) {
        Logger.e(TAG, e);
        return logsFile;
      }

      return logsFile;
    }

    public static List<ApkPermission> parsePermissions(Context context,
        List<String> permissionArray) {
      List<ApkPermission> list = new ArrayList<>();
      CharSequence csPermissionGroupLabel;
      CharSequence csPermissionLabel;
      PackageManager pm = context.getPackageManager();

      List<PermissionGroupInfo> lstGroups =
          pm.getAllPermissionGroups(PackageManager.PERMISSION_GRANTED);
      for (String permission : permissionArray) {

        for (PermissionGroupInfo pgi : lstGroups) {
          try {
            List<PermissionInfo> lstPermissions =
                pm.queryPermissionsByGroup(pgi.name, PackageManager.PERMISSION_GRANTED);
            for (PermissionInfo pi : lstPermissions) {
              if (pi.name.equals(permission)) {
                csPermissionLabel = pi.loadLabel(pm);
                csPermissionGroupLabel = pgi.loadLabel(pm);
                list.add(new ApkPermission(csPermissionGroupLabel.toString(),
                    csPermissionLabel.toString()));
              }
            }
          } catch (Exception e) {
            Logger.e(TAG, e);
            throw new RuntimeException(e);
          }
        }
      }

      Collections.sort(list, (lhs, rhs) -> lhs.getName()
          .compareTo(rhs.getName()));

      return list;
    }

    /**
     * If you are trying to use this method inside a fragment, the base fragment already has
     * a copy of it. Use that instead.
     */
    @Deprecated public static void hideKeyboard(Activity activity) {
      View view = activity.getCurrentFocus();
      if (view != null) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
            hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }

    public static void clearApplicationData(Context context) {
      File cache = context.getCacheDir();
      File appDir = new File(cache.getParent());
      if (appDir.exists()) {
        String[] children = appDir.list();
        for (String s : children) {
          if (!s.equals("lib")) {
            deleteDir(new File(appDir, s));
          }
        }
      }
    }

    public static boolean deleteDir(File dir) {
      if (dir != null && dir.isDirectory()) {
        String[] children = dir.list();
        if (children != null) {
          for (String child : children) {
            boolean success = deleteDir(new File(dir, child));
            if (!success) {
              return false;
            }
          }
        }
      }

      return dir != null && dir.delete();
    }

    /**
     * from v7
     *
     * Use RootManager or other entity created for this effect in the engine
     */
    @Deprecated public static boolean hasRoot() {
      boolean retval;
      Process suProcess;

      try {
        suProcess = Runtime.getRuntime()
            .exec("su");

        DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
        DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

        // Getting the id of the current user to check if this is root
        os.writeBytes("id\n");
        os.flush();

        String currUid = osRes.readLine();
        boolean exitSu;
        if (null == currUid) {
          retval = false;
          exitSu = false;
          Logger.d("ROOT", "Can't get root access or denied by user");
        } else if (currUid.contains("uid=0")) {
          retval = true;
          exitSu = true;
          Logger.d("ROOT", "Root access granted");
        } else {
          retval = false;
          exitSu = true;
          Logger.d("ROOT", "Root access rejected: " + currUid);
        }

        if (exitSu) {
          os.writeBytes("exit\n");
          os.flush();
        }
      } catch (Exception e) {
        // Can't get root !
        // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

        retval = false;
        Logger.d("ROOT", "Root access rejected [" + e.getClass()
            .getName() + "] : " + e.getMessage());
      }

      return retval;
    }
  }

  public static final class ThreadU {

    private static final String TAG = ThreadU.class.getName();

    public static void runOnIoThread(Runnable runnable) {
      Observable.just(null)
          .observeOn(Schedulers.io())
          .subscribe(o -> runnable.run(), e -> {
            Logger.e(TAG, e);
            throw new RuntimeException(e);
          });
    }

    public static void runOnUiThread(Runnable runnable) {
      if (ThreadU.isUiThread()) {
        runnable.run();
      } else {
        Observable.just(null)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(o -> runnable.run(), e -> {
              e.printStackTrace();
            });
      }
    }

    @NonNull public static String getStack() {
      StringBuilder stringBuilder = new StringBuilder();
      for (StackTraceElement stackTraceElement : Thread.currentThread()
          .getStackTrace()) {
        stringBuilder.append(stackTraceElement);
        stringBuilder.append("\n");
      }
      return stringBuilder.toString();
    }

    public static boolean isUiThread() {
      return Looper.getMainLooper()
          .getThread() == Thread.currentThread();
    }
  }

  public static class HtmlU {

    /**
     * Find a work around for this. Could be a dangerous operation, converting text from HTML.
     *
     * @return original text converted to HTML in a CharSequence
     */
    public static CharSequence parse(String text) {
      // Fix for AN-348: replace the & with &amp; (that's was causing the pushback buffer full) (from Aptoide V7)
      return Html.fromHtml(text.replace("\n", "<br/>")
          .replace("&", "&amp;"));
    }
  }

  public static class ResourseU {

    public static int getInt(@IntegerRes int resId, Resources resources) {
      return resources.getInteger(resId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId, Resources resources) {
      return resources.getDrawable(drawableId);
    }

    public static String getString(@StringRes int stringRes, Resources resources) {
      return StringU.getResString(stringRes, resources);
    }
  }

  public static class DateTimeU extends DateUtils {

    private static final long millisInADay = 1000 * 60 * 60 * 24;
    private static String mTimestampLabelYesterday;
    private static String mTimestampLabelToday;
    private static String mTimestampLabelJustNow;
    private static String mTimestampLabelMinutesAgo;
    private static String mTimestampLabelHoursAgo;
    private static String mTimestampLabelHourAgo;
    private static String mTimestampLabelDaysAgo;
    private static String mTimestampLabelWeekAgo;
    private static String mTimestampLabelWeeksAgo;
    private static String mTimestampLabelMonthAgo;
    private static String mTimestampLabelMonthsAgo;
    private static String mTimestampLabelYearAgo;
    private static String mTimestampLabelYearsAgo;
    private static DateTimeU instance;
    private static String[] weekdays = new DateFormatSymbols().getWeekdays(); // get day names

    private final Context context;

    private DateTimeU(Context context) {
      this.context = context;
    }

    /**
     * Singleton constructor, needed to get access to the application context & strings for i18n
     *
     * @param context Context
     *
     * @return DateTimeUtils singleton instance
     *
     * @throws Exception
     */
    public static DateTimeU getInstance(Context context) {
      if (instance == null) {
        instance = new DateTimeU(context);
        mTimestampLabelYesterday = ResourseU.getString(R.string.WidgetProvider_timestamp_yesterday,
            context.getResources());
        mTimestampLabelToday =
            ResourseU.getString(R.string.WidgetProvider_timestamp_today, context.getResources());
        mTimestampLabelJustNow =
            ResourseU.getString(R.string.WidgetProvider_timestamp_just_now, context.getResources());
        mTimestampLabelMinutesAgo =
            ResourseU.getString(R.string.WidgetProvider_timestamp_minutes_ago,
                context.getResources());
        mTimestampLabelHoursAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_hours_ago,
            context.getResources());
        mTimestampLabelHourAgo =
            ResourseU.getString(R.string.WidgetProvider_timestamp_hour_ago, context.getResources());
        mTimestampLabelDaysAgo =
            ResourseU.getString(R.string.WidgetProvider_timestamp_days_ago, context.getResources());
        mTimestampLabelWeekAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_week_ago2,
            context.getResources());
        mTimestampLabelWeeksAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_weeks_ago,
            context.getResources());
        mTimestampLabelMonthAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_month_ago,
            context.getResources());
        mTimestampLabelMonthsAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_months_ago,
            context.getResources());
        mTimestampLabelYearAgo =
            ResourseU.getString(R.string.WidgetProvider_timestamp_year_ago, context.getResources());
        mTimestampLabelYearsAgo = ResourseU.getString(R.string.WidgetProvider_timestamp_years_ago,
            context.getResources());
      }
      return instance;
    }

    /**
     * Checks if the given date is yesterday.
     *
     * @param date - Date to check.
     *
     * @return TRUE if the date is yesterday, FALSE otherwise.
     */
    private static boolean isYesterday(long date) {

      final Calendar currentDate = Calendar.getInstance();
      currentDate.setTimeInMillis(date);

      final Calendar yesterdayDate = Calendar.getInstance();
      yesterdayDate.add(Calendar.DATE, -1);

      return yesterdayDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
          && yesterdayDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
    }

    public String getTimeDiffAll(Context context, long time, Resources resources) {

      long diffTime = new Date().getTime() - time;

      if (isYesterday(time) || isToday(time)) {
        getTimeDiffString(time, context, resources);
      } else {
        if (diffTime < DateUtils.WEEK_IN_MILLIS) {
          int diffDays = Double.valueOf(Math.ceil(diffTime / millisInADay))
              .intValue();
          return diffDays == 1 ? mTimestampLabelYesterday
              : AptoideUtils.StringU.getFormattedString(R.string.WidgetProvider_timestamp_days_ago,
                  resources, diffDays);
        } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4) {
          int diffDays = Double.valueOf(Math.ceil(diffTime / WEEK_IN_MILLIS))
              .intValue();
          return diffDays == 1 ? mTimestampLabelWeekAgo
              : AptoideUtils.StringU.getFormattedString(R.string.WidgetProvider_timestamp_weeks_ago,
                  resources, diffDays);
        } else if (diffTime < DateUtils.WEEK_IN_MILLIS * 4 * 12) {
          int diffDays = Double.valueOf(Math.ceil(diffTime / (WEEK_IN_MILLIS * 4)))
              .intValue();
          return diffDays == 1 ? mTimestampLabelMonthAgo : AptoideUtils.StringU.getFormattedString(
              R.string.WidgetProvider_timestamp_months_ago, resources, diffDays);
        } else {
          int diffDays = Double.valueOf(Math.ceil(diffTime / (WEEK_IN_MILLIS * 4 * 12)))
              .intValue();
          return diffDays == 1 ? mTimestampLabelYearAgo
              : AptoideUtils.StringU.getFormattedString(R.string.WidgetProvider_timestamp_years_ago,
                  resources, diffDays);
        }
      }

      return getTimeDiffString(time, context, resources);
    }

    /**
     * Displays a user-friendly date difference string
     *
     * @param timedate Timestamp to format as date difference from now
     *
     * @return Friendly-formatted date diff string
     */
    public String getTimeDiffString(Context context, long timedate, Resources resources) {
      Calendar startDateTime = Calendar.getInstance();
      Calendar endDateTime = Calendar.getInstance();
      endDateTime.setTimeInMillis(timedate);
      long milliseconds1 = startDateTime.getTimeInMillis();
      long milliseconds2 = endDateTime.getTimeInMillis();
      long diff = milliseconds1 - milliseconds2;

      long hours = diff / (60 * 60 * 1000);
      long minutes = diff / (60 * 1000);
      minutes = minutes - 60 * hours;
      long seconds = diff / (1000);

      boolean isToday = DateTimeU.isToday(timedate);
      boolean isYesterday = DateTimeU.isYesterday(timedate);

      if (hours > 0 && hours < 12) {
        return hours == 1 ? AptoideUtils.StringU.getFormattedString(
            R.string.WidgetProvider_timestamp_hour_ago, resources, hours)
            : AptoideUtils.StringU.getFormattedString(R.string.WidgetProvider_timestamp_hours_ago,
                resources, hours);
      } else if (hours <= 0) {
        if (minutes > 0) {
          return AptoideUtils.StringU.getFormattedString(
              R.string.WidgetProvider_timestamp_minutes_ago, resources, minutes);
        } else {
          return mTimestampLabelJustNow;
        }
      } else if (isToday) {
        return mTimestampLabelToday;
      } else if (isYesterday) {
        return mTimestampLabelYesterday;
      } else if (startDateTime.getTimeInMillis() - timedate < millisInADay * 6) {
        return weekdays[endDateTime.get(Calendar.DAY_OF_WEEK)];
      } else {
        return formatDateTime(context, timedate, DateUtils.FORMAT_NUMERIC_DATE);
      }
    }

    public String getTimeDiffString(long timedate, Context context, Resources resources) {
      return getTimeDiffString(context, timedate, resources);
    }
  }

  /**
   * Created with IntelliJ IDEA. User: rmateus Date: 03-12-2013 Time: 12:58 To change this template
   * use File | Settings | File Templates.
   */
  public static class IconSizeU {

    public static final int DEFAULT_SCREEN_DENSITY = -1;
    public static final HashMap<Integer, String> mStoreIconSizes;
    public static final int ICONS_SIZE_TYPE = 0;
    public static final HashMap<Integer, String> mIconSizes;
    public static final int STORE_ICONS_SIZE_TYPE = 1;
    private static final String TAG = IconSizeU.class.getName();
    static final private int baseLine = 96;
    static final private int baseLineAvatar = 150;
    static final private int baseLineXNotification = 320;
    static final private int baseLineYNotification = 180;
    private static final String AVATAR_STRING = "_avatar";
    private static final Pattern urlWithDimensionPattern =
        Pattern.compile("_{1}[1-9]{3}(x|X){1}[1-9]{3}.{1}.{3,4}\\b");
    private static int baseLineScreenshotLand = 256;
    private static int baseLineScreenshotPort = 96;

    static {
      mStoreIconSizes = new HashMap<>();
      mStoreIconSizes.put(DisplayMetrics.DENSITY_XXHIGH, "450x450");
      mStoreIconSizes.put(DisplayMetrics.DENSITY_XHIGH, "300x300");
      mStoreIconSizes.put(DisplayMetrics.DENSITY_HIGH, "225x225");
      mStoreIconSizes.put(DisplayMetrics.DENSITY_MEDIUM, "150x150");
      mStoreIconSizes.put(DisplayMetrics.DENSITY_LOW, "113x113");
    }

    static {
      mIconSizes = new HashMap<>();
      mIconSizes.put(DisplayMetrics.DENSITY_XXXHIGH, "384x384");
      mIconSizes.put(DisplayMetrics.DENSITY_XXHIGH, "288x288");
      mIconSizes.put(DisplayMetrics.DENSITY_XHIGH, "192x192");
      mIconSizes.put(DisplayMetrics.DENSITY_HIGH, "144x144");
      mIconSizes.put(DisplayMetrics.DENSITY_MEDIUM, "127x127");
      mIconSizes.put(DisplayMetrics.DENSITY_LOW, "96x96");
    }

    public static String screenshotToThumb(String imageUrl, String orientation,
        WindowManager windowManager, Resources resources) {

      String screen = null;

      try {

        if (imageUrl.contains("_screen")) {
          screen = parseScreenshotUrl(imageUrl, orientation, windowManager, resources);
        } else {

          String[] splitString = imageUrl.split("/");
          StringBuilder db = new StringBuilder();
          for (int i = 0; i != splitString.length - 1; i++) {
            db.append(splitString[i]);
            db.append("/");
          }

          db.append("thumbs/mobile/");
          db.append(splitString[splitString.length - 1]);
          screen = db.toString();
        }
      } catch (Exception e) {
        Logger.e(TAG, e);
        throw e;
      }

      return screen;
    }

    private static String parseScreenshotUrl(String screenshotUrl, String orientation,
        WindowManager windowManager, Resources resources) {
      String sizeString = generateSizeStringScreenshotsdd(orientation, windowManager, resources);

      String[] splitUrl = splitUrlExtension(screenshotUrl);
      return splitUrl[0] + "_" + sizeString + "." + splitUrl[1];
    }

    private static String generateSizeStringScreenshotsdd(String orient,
        WindowManager windowManager, Resources resources) {
      float densityMultiplier = densityMultiplier(resources);

      int size;
      if (orient != null && orient.equals("portrait")) {
        size = baseLineScreenshotPort * ((int) densityMultiplier);
      } else {
        size = baseLineScreenshotLand * ((int) densityMultiplier);
      }

      return size + "x" + ScreenU.getDensityDpi(windowManager);
    }

    private static String[] splitUrlExtension(String url) {
      return url.split(RegexU.SPLIT_URL_EXTENSION);
    }

    private static Float densityMultiplier(Resources resources) {

      float densityMultiplier = resources.getDisplayMetrics().density;

      if (densityMultiplier <= 0.75f) {
        densityMultiplier = 0.75f;
      } else if (densityMultiplier <= 1) {
        densityMultiplier = 1f;
      } else if (densityMultiplier <= 1.333f) {
        densityMultiplier = 1.3312500f;
      } else if (densityMultiplier <= 1.5f) {
        densityMultiplier = 1.5f;
      } else if (densityMultiplier <= 2f) {
        densityMultiplier = 2f;
      } else if (densityMultiplier <= 3f) {
        densityMultiplier = 3f;
      } else {
        densityMultiplier = 4f;
      }
      return densityMultiplier;
    }

    // method deprecated since no usage was found.
    @Deprecated public static String generateStringNotification(String url, Resources resources) {
      if (url == null) {
        return "";
      }
      float densityMultiplier = densityMultiplier(resources);

      int sizeX = (int) (baseLineXNotification * densityMultiplier);
      int sizeY = (int) (baseLineYNotification * densityMultiplier);

      //Logger.d("Aptoide-IconSize", "Size is " + size);

      //return sizeX + "x" + sizeY;
      String[] splittedUrl = splitUrlExtension(url);
      url = splittedUrl[0] + "_" + sizeX + "x" + sizeY + "." + splittedUrl[1];

      return url;
    }

    public static String generateSizeStoreString(String url, Resources resources,
        WindowManager windowManager) {

      if (url == null) {
        return "";
      }

      String iconRes = mStoreIconSizes.get(resources.getDisplayMetrics().densityDpi);
      iconRes = (TextUtils.isEmpty(iconRes) ? getDefaultSize(STORE_ICONS_SIZE_TYPE, windowManager)
          : iconRes);

      if (TextUtils.isEmpty(iconRes)) {
        return url;
      } else {
        String[] splittedUrl = splitUrlExtension(url);
        return splittedUrl[0] + "_" + iconRes + "." + splittedUrl[1];
      }
    }

    private static String getDefaultSize(int varType, WindowManager windowManager) {

      switch (varType) {
        case STORE_ICONS_SIZE_TYPE:
          if (ScreenU.getDensityDpi(windowManager) < DisplayMetrics.DENSITY_HIGH) {
            return mStoreIconSizes.get(DisplayMetrics.DENSITY_LOW);
          } else {
            return mStoreIconSizes.get(DisplayMetrics.DENSITY_XXHIGH);
          }
        case ICONS_SIZE_TYPE:
          if (ScreenU.getDensityDpi(windowManager) < DisplayMetrics.DENSITY_HIGH) {
            return mIconSizes.get(DisplayMetrics.DENSITY_LOW);
          } else {
            return mIconSizes.get(DisplayMetrics.DENSITY_XXXHIGH);
          }
      }
      return null;
    }

    public static String generateStringAvatar(String url, Resources resources,
        WindowManager windowManager) {
      if (url == null) {
        return "";
      }
      float densityMultiplier = densityMultiplier(resources);

      int size = Math.round(baseLineAvatar * densityMultiplier);

      //Logger.d("Aptoide-IconSize", "Size is " + size);

      //return size + "x" + size;

      String[] splittedUrl = splitUrlExtension(url);
      return splittedUrl[0] + "_" + getUserAvatarIconSize(windowManager) + "." + splittedUrl[1];
    }

    private static String getUserAvatarIconSize(WindowManager windowManager) {
      if (ScreenU.getDensityDpi(windowManager) <= DisplayMetrics.DENSITY_HIGH) {
        return "50x50";
      } else {
        return "150x150";
      }
    }

    public static String getNewImageUrl(String imageUrl, Resources resources,
        WindowManager windowManager) {

      if (TextUtils.isEmpty(imageUrl)) {
        return imageUrl;
      } else if (imageUrl.contains("portrait")) {
        return parseScreenshots(imageUrl, windowManager);
      } else if (imageUrl.contains("_icon")) {
        return parseIcon(imageUrl, resources, windowManager);
      }
      return imageUrl;
    }

    private static String parseScreenshots(String orient, WindowManager windowManager) {
      if (orient == null) {
        return "";
      }
      boolean isPortrait = orient != null && orient.equals("portrait");
      int dpi = ScreenU.getDensityDpi(windowManager);

      String[] splittedUrl = splitUrlExtension(orient);
      return splittedUrl[0] + "_" + getThumbnailSize(dpi, isPortrait) + "." + splittedUrl[1];
    }

    /**
     * On v7 webservices there is no attribute of HD icon. <br />Instead, the logic is that if the
     * filename ends with <b>_icon</b> it is an HD icon.
     *
     * @param iconUrl The String with the URL of the icon
     *
     * @return A String with
     */
    private static String parseIcon(String iconUrl, Resources resources,
        WindowManager windowManager) {

      if (iconUrl == null) {
        return "";
      }
      try {
        if (iconUrl.contains("_icon")) {
          String sizeString = IconSizeU.generateSizeString(resources, windowManager);
          if (sizeString != null && !sizeString.isEmpty()) {
            String[] splittedUrl = splitUrlExtension(iconUrl);
            iconUrl = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
          }
        }
      } catch (Exception e) {
        Logger.e(TAG, e);
        throw e;
      }
      return iconUrl;
    }

    private static String getThumbnailSize(int density, boolean isPortrait) {
      if (!isPortrait) {
        if (density >= 640) {
          return "1024x640";
        } else if (density >= 480) {
          return "768x480";
        } else if (density >= 320) {
          return "512x320";
        } else if (density >= 240) {
          return "384x240";
        } else if (density >= 213) {
          return "340x213";
        } else if (density >= 160) {
          return "256x160";
        } else {
          return "192x120";
        }
      } else {
        if (density >= 640) {
          return "384x640";
        } else if (density >= 480) {
          return "288x480";
        } else if (density >= 320) {
          return "192x320";
        } else if (density >= 240) {
          return "144x240";
        } else if (density >= 213) {
          return "127x213";
        } else if (density >= 160) {
          return "96x160";
        } else {
          return "72x120";
        }
      }
    }

    private static String generateSizeString(Resources resources, WindowManager windowManager) {
      String iconRes = mIconSizes.get(resources.getDisplayMetrics().densityDpi);
      return iconRes != null ? iconRes : getDefaultSize(ICONS_SIZE_TYPE, windowManager);
    }

    /**
     * Cleans the image URL out of "_widthXheight"
     */
    public static String cleanImageUrl(String originalUrl) {
      int lastUnderScore = originalUrl.lastIndexOf('_');
      if (lastUnderScore == -1) {
        return originalUrl;
      }

      String lastPart = originalUrl.substring(lastUnderScore);
      if (urlWithDimensionPattern.matcher(lastPart)
          .matches()) {
        int lastDot = originalUrl.lastIndexOf('.');
        return originalUrl.substring(0, lastUnderScore) + originalUrl.substring(lastDot);
      }

      return originalUrl;
    }
  }

  // deprecated since no usage was found.
  @Deprecated public static class Benchmarking {

    private static final String TAG = Benchmarking.class.getSimpleName();

    private String methodName;
    private long startTime;

    public static Benchmarking start(String methodName) {
      Benchmarking benchmarking = new Benchmarking();
      benchmarking.methodName = methodName;
      benchmarking.startTime = System.currentTimeMillis();
      return benchmarking;
    }

    public void end() {
      long endTime = System.currentTimeMillis();
      Logger.d(TAG, "Thread: "
          + Thread.currentThread()
          .getId()
          + " Method:"
          + methodName
          + " - Total execution time: "
          + (endTime - startTime)
          + "ms");
    }
  }

  // deprecated since no usage was found.
  @Deprecated public static class ObservableU {

    /**
     * code from <a href="http://blog.danlew.net/2015/03/02/dont-break-the-chain/">http://blog.danlew.net/2015/03/02/dont-break-the-chain/</a>
     *
     * @param <T> Observable of T
     *
     * @return original Observable subscribed in an io thread and observed in the main thread
     */
    public static <T> Observable.Transformer<T, T> applySchedulers() {
      return observable -> observable.subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }

    // consider moving the retry code from dataprovider module to here
  }

  public static final class SocialLinksU {

    public static @Nullable String getFacebookPageURL(int version, @NonNull String facebookUrl) {
      String toReturn;
      if (version >= 3002850) { //newer versions of fb app
        toReturn = "fb://facewebmodal/f?href=" + facebookUrl;
      } else {
        toReturn = facebookUrl;
      }
      return toReturn;
    }
  }

  // deprecated since no usage was found.
  @Deprecated public static final class LocaleU {

    public static final Locale DEFAULT = Locale.getDefault();
  }
}
