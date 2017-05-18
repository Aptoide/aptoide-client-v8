package cm.aptoide.pt.v8engine.util;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.v8engine.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by neuro on 30-12-2016.
 */

public class ApkFy {

  private final Context context;
  private final Intent intent;

  public ApkFy(Context context, Intent intent) {
    this.context = context;
    this.intent = intent;
  }

  public void run() {
    if (SecurePreferences.shouldRunApkFy()) {
      Long appId = extractAppId(context);
      if (appId != null) {
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT, true);
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, appId);
        intent.putExtra(DeepLinkIntentReceiver.DeepLinksKeys.SHOULD_INSTALL,
            AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP);
      }
      SecurePreferences.setApkFyRun();
    }
  }

  private Long extractAppId(Context context) {

    String appId = null;
    try {
      final String sourceDir = context.getPackageManager()
          .getPackageInfo(V8Engine.getConfiguration()
              .getAppId(), 0).applicationInfo.sourceDir;
      final ZipFile myZipFile = new ZipFile(sourceDir);
      final ZipEntry entry = myZipFile.getEntry("META-INF/aob");
      if (entry != null) {
        final InputStream is = myZipFile.getInputStream(entry);

        Properties properties = new Properties();
        properties.load(is);
        if (properties.containsKey("downloadId")) {
          appId = properties.getProperty("downloadId");
        }

        return appId != null ? Long.parseLong(appId) : null;
      }
    } catch (Exception e) {
      if (appId != null) {
        CrashReport.getInstance()
            .log("APKFY_APP_ID", appId);
      }
      Logger.d(TAG, e.getMessage());
      CrashReport.getInstance()
          .log(e);
    }
    return null;
  }
}
