package cm.aptoide.pt.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by neuro on 30-12-2016.
 */

public class ApkFyManager {

  private static final String TAG = "ApkFy";

  private final Context context;
  private final Intent intent;
  private final SharedPreferences securePreferences;

  public ApkFyManager(Context context, Intent intent, SharedPreferences securePreferences) {
    this.context = context;
    this.intent = intent;
    this.securePreferences = securePreferences;
  }

  public void run() {
    if (SecurePreferences.shouldRunApkFy(securePreferences)) {
      ApkfyParameters params = extractApkfyParameters(context);
      if (params.getAppId() != null) {
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, params.getAppId());
        if (params.getOemId() != null && !params.getOemId()
            .isEmpty()) {
          intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.OEM_ID_KEY, params.getOemId());
        }
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APK_FY, true);
      }
      SecurePreferences.setApkFyRun(securePreferences);
    }
  }

  private ApkfyParameters extractApkfyParameters(Context context) {
    Long appId = null;
    String oemId = null;

    String stringAppId = null;
    try {
      final String sourceDir = context.getPackageManager()
          .getPackageInfo(BuildConfig.APPLICATION_ID, 0).applicationInfo.sourceDir;
      final ZipFile myZipFile = new ZipFile(sourceDir);
      final ZipEntry entry = myZipFile.getEntry("META-INF/aob");
      if (entry != null) {
        final InputStream is = myZipFile.getInputStream(entry);

        Properties properties = new Properties();
        properties.load(is);
        if (properties.containsKey("downloadId")) {
          stringAppId = properties.getProperty("downloadId");
        }
        if (properties.containsKey("oemid")) {
          oemId = properties.getProperty("oemid");
        }
        appId = stringAppId != null ? Long.parseLong(stringAppId) : null;
      }
    } catch (Exception e) {
      if (stringAppId != null) {
        CrashReport.getInstance()
            .log("APKFY_APP_ID", stringAppId);
      }
      Logger.getInstance()
          .d(TAG, e.getMessage());
      CrashReport.getInstance()
          .log(e);
    }
    return new ApkfyParameters(appId, oemId);
  }
}
