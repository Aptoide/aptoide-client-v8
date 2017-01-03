package cm.aptoide.pt.v8engine.util;

import android.app.Activity;
import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;
import com.crashlytics.android.Crashlytics;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipFile;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by neuro on 30-12-2016.
 */

public class ApkFy {

  private Long extractAppId(Context context) {

    String appId = null;
    try {
      final String sourceDir = context.getPackageManager()
          .getPackageInfo(V8Engine.getConfiguration().getAppId(), 0).applicationInfo.sourceDir;
      final ZipFile myZipFile = new ZipFile(sourceDir);
      final InputStream is = myZipFile.getInputStream(myZipFile.getEntry("META-INF/aob"));

      Properties properties = new Properties();
      properties.load(is);
      if (properties.containsKey("downloadId")) {
        appId = properties.getProperty("downloadId");
      }

      return appId != null ? Long.parseLong(appId) : null;
    } catch (Exception e) {
      if (appId != null) {
        Crashlytics.setString("APKFY_APP_ID", appId);
      }
      Logger.d(TAG, e.getMessage());
      Crashlytics.logException(e);
    }
    return null;
  }

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
}
