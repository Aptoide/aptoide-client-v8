/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by neuro on 26-04-2016.
 */
public class AccountManagerUtils {

  public static String getDeviceId(Context context) {
    return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  public static int getSdkVer() {
    return Build.VERSION.SDK_INT;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP) @SuppressWarnings("deprecation")
  public static String getAbis() {
    final String[] abis = getSdkVer() >= 21 ? Build.SUPPORTED_ABIS : new String[] {
        Build.CPU_ABI, Build.CPU_ABI2
    };
    final StringBuilder builder = new StringBuilder();
    for (int i = 0; i < abis.length; i++) {
      builder.append(abis[i]);
      if (i < abis.length - 1) {
        builder.append(",");
      }
    }
    return builder.toString();
  }

  public static int getNumericScreenSize(Context context) {
    int size = context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK;
    return (size + 1) * 100;
  }

  public static String getGlEsVer(Context context) {
    return ((ActivityManager) context.getSystemService(
        Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().getGlEsVersion();
  }

  public static int getScreenSize(Context context) {
    return context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK;
  }
}
