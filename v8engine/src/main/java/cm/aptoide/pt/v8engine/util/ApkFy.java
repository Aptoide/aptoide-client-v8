package cm.aptoide.pt.v8engine.util;

import android.app.Activity;
import android.content.Context;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * Created by neuro on 30-12-2016.
 */

public class ApkFy {

  private static final String TAG = ApkFy.class.getSimpleName();

  public void run(Activity activity) {
    if (SecurePreferences.shouldRunApkFy()) {
      Long aLong = extractAppId(activity);
      if (aLong != null) {
        activity.getIntent()
            .putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
        activity.getIntent().putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, aLong);
      }
      SecurePreferences.setApkFyRun();
    }
  }

  private Long extractAppId(Context context) {

    String appId = null;
    try {
      final String sourceDir = context.getPackageManager()
          .getPackageInfo(V8Engine.getConfiguration().getAppId(), 0).applicationInfo.sourceDir;
      final ZipFile myZipFile = new ZipFile(sourceDir);

      if (myZipFile.getEntry("META-INF/aob") != null) {
        final InputStream is = myZipFile.getInputStream(myZipFile.getEntry("META-INF/aob"));

        Properties properties = new Properties();
        properties.load(is);
        if (properties.containsKey("downloadId")) {
          appId = properties.getProperty("downloadId");
        }

        return appId != null ? Long.parseLong(appId) : null;
      }
    } catch (Exception e) {
      if (appId != null) {
        CrashReport.getInstance().log("APKFY_APP_ID", appId);
      }
      Logger.d(TAG, e.getMessage());
      CrashReport.getInstance().log(e);
    }
    return null;
  }
}
