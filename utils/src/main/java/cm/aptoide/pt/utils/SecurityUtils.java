/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Base64;
import cm.aptoide.pt.logger.Logger;
import java.security.MessageDigest;

/**
 * Created by sithengineer on 01/06/16.
 * <p>
 * Code extracted from https://www.airpair.com/android/posts/adding-tampering-detection-to-your-android-app
 */
public final class SecurityUtils {

  public static final int VALID_APP_SIGNATURE = 0;
  public static final int INVALID_APP_SIGNATURE = 1;
  public static final String APTOIDE_STORE_APP_ID = "cm.aptoide.pt";
  public static final String PLAY_STORE_APP_ID = "com.android.vending";
  public static final String AMAZON_STORE_APP_ID = "com.amazon.mShop.android";
  public static final String XIAOMI_STORE_APP_ID = "com.xiaomi.market";
  private static final String TAG = SecurityUtils.class.getName();
  // point a string obfuscator tool - like DexGuard has - to here
  private static final String APP_SIGNATURE = "mKfMdjy9CFoKhOJqec3POh4yPFI=";

  public static int checkAppSignature(Context context) {

    try {

      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

      for (Signature signature : packageInfo.signatures) {

        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);

        Logger.d(TAG,
            String.format("Include this string as a value for SIGNATURE: %s", currentSignature));

        //compare signatures

        if (TextUtils.equals(APP_SIGNATURE, currentSignature)) {
          return VALID_APP_SIGNATURE;
        }
      }
    } catch (Exception e) {
      //assumes an issue in checking signature., but we let the caller decide on what to do.
      Logger.w(TAG, "checkAppSignature(Context)", e);
    }

    return INVALID_APP_SIGNATURE;
  }

  public static boolean checkEmulator() {
    try {
      boolean goldfish = getSystemProperty("ro.hardware").contains("goldfish");
      boolean emu = getSystemProperty("ro.kernel.qemu").length() > 0;
      boolean sdk = getSystemProperty("ro.product.model").equals("sdk");
      if (emu || goldfish || sdk) {
        return true;
      }
    } catch (Exception e) {

    }
    return false;
  }

  private static String getSystemProperty(String name) throws Exception {
    Class systemPropertyClazz = Class.forName("android.os.SystemProperties");
    return (String) systemPropertyClazz.getMethod("get", new Class[] { String.class })
        .invoke(systemPropertyClazz, name);
  }

  public static boolean checkDebuggable(Context context) {
    return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
  }

  public static boolean verifyInstallerForStore(final Context context, String packageName,
      @Store String store) {
    final String installer = getInstallerPackageName(context, packageName);
    return installer != null && installer.startsWith(store);
  }

  public static String getInstallerPackageName(final Context context, String packageName) {
    return context.getPackageManager()
        .getInstallerPackageName(packageName);
  }

  @StringDef({ APTOIDE_STORE_APP_ID, PLAY_STORE_APP_ID, AMAZON_STORE_APP_ID, XIAOMI_STORE_APP_ID })
  public @interface Store {

  }
}
