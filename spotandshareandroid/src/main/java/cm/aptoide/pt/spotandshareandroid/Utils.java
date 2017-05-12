package cm.aptoide.pt.spotandshareandroid;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by jandrade on 21-08-2015.
 */
public class Utils {
  public static String filters(Context context) {

    int minSdk = AptoideUtils.HWSpecifications.getSdkVer();
    String minScreen =
        Filters.Screen.values()[AptoideUtils.HWSpecifications.getScreenSize(context)].name()
            .toLowerCase(Locale.ENGLISH);
    String minGlEs = AptoideUtils.HWSpecifications.getGlEsVer(context);

    final int density = AptoideUtils.HWSpecifications.getDensityDpi(context);

    String cpuAbi = AptoideUtils.HWSpecifications.getCpuAbi();

    if (AptoideUtils.HWSpecifications.getCpuAbi2()
        .length() > 0) {
      cpuAbi += "," + AptoideUtils.HWSpecifications.getCpuAbi2();
    }
    int myversionCode = 0;
    PackageManager manager = context.getPackageManager();
    try {
      myversionCode = manager.getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    String filters = (Build.DEVICE.equals("alien_jolla_bionic") ? "apkdwn=myapp&" : "")
        + "maxSdk="
        + minSdk
        + "&maxScreen="
        + minScreen
        + "&maxGles="
        + minGlEs
        + "&myCPU="
        + cpuAbi
        + "&myDensity="
        + density
        + "&myApt="
        + myversionCode;

    return Base64.encodeToString(filters.getBytes(), 0)
        .replace("=", "")
        .replace("/", "*")
        .replace("+", "_")
        .replace("\n", "");
  }

  public static String generateRandomAlphanumericString(int lengthWanted) {
    char[] array = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    StringBuilder sb = new StringBuilder();
    Random r = new Random();
    for (int i = 0; i < lengthWanted; i++) {
      char c = array[r.nextInt(array.length)];
      sb.append(c);
    }
    return sb.toString();
  }

  public static long getFolderSize(File dir) {
    long size = 0;
    for (File file : dir.listFiles()) {
      if (file.isFile()) {
        System.out.println(file.getName() + " " + file.length());
        size += file.length();
      } else {
        size += getFolderSize(file);
      }
    }
    return size;
  }

  private static class AptoideUtils {
    public static class HWSpecifications {

      public static final String TERMINAL_INFO =
          getModel() + "(" + getProduct() + ")" + ";v" + getRelease() + ";" + System.getProperty(
              "os.arch");
      private static String cpuAbi2;

      public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      }

      /**
       * @return the screenSize
       */
      static public int getScreenSize(Context context) {
        return context.getResources()
            .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
      }

      static public int getNumericScreenSize(Context context) {
        int size = context.getResources()
            .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return (size + 1) * 100;
      }

      /**
       * @return the esglVer
       */
      static public String getGlEsVer(Context context) {
        return ((ActivityManager) context.getSystemService(
            Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo()
            .getGlEsVersion();
      }

      public static int getDensityDpi(Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        manager.getDefaultDisplay()
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
        } else {
          dpi = 480;
        }

        return dpi;
      }

      public static String getCpuAbi() {
        return Build.CPU_ABI;
      }

      public static String getCpuAbi2() {

        if (getSdkVer() >= 8 && !Build.CPU_ABI2.equals(Build.UNKNOWN)) {
          return Build.CPU_ABI2;
        } else {
          return "";
        }
      }

      /**
       * @return the sdkVer
       */

      static public int getSdkVer() {
        return Build.VERSION.SDK_INT;
      }

      public static String getProduct() {
        return Build.PRODUCT.replace(";", " ");
      }

      public static String getModel() {
        return Build.MODEL.replaceAll(";", " ");
      }

      public static String getRelease() {
        return Build.VERSION.RELEASE.replaceAll(";", " ");
      }
    }
  }

  private static class Filters {
    public enum Screen {
      notfound, small, normal, large, xlarge;

      public static Screen lookup(String screen) {
        try {
          return valueOf(screen);
        } catch (Exception e) {
          return notfound;
        }
      }

    }

    public enum Age {
      All, Mature;

      public static Age lookup(String age) {
        try {
          return valueOf(age);
        } catch (Exception e) {
          return All;
        }
      }
    }
  }

  public static class Socket {

    public static SocketBinder newDefaultSocketBinder() {
      return new SocketBinder() {
        @Override public void bind(java.net.Socket socket) {
          if (DataHolder.getInstance().network != null) {
            try {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DataHolder.getInstance().network.bindSocket(socket);
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      };
    }
  }
}
