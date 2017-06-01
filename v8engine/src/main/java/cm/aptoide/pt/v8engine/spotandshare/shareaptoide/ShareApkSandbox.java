package cm.aptoide.pt.v8engine.spotandshare.shareaptoide;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import cm.aptoide.pt.nanohttpd.servers.modular.modules.ShareApkServer;
import cm.aptoide.pt.spotandshareandroid.R;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by neuro on 09-05-2017.
 */
public class ShareApkSandbox extends NanoHTTPD {

  private final ShareApkServer shareApkServer;

  public ShareApkSandbox(Context context) {
    super(38080);

    shareApkServer = new ShareApkServer(new File(getPathAndroid(context)), getFileName(context),
        createTokens(context));
  }

  private static HashMap<String, String> createTokens(Context context) {
    HashMap<String, String> tokensMap = new HashMap<>();

    tokensMap.put("{{spotandshare_title_share_apk_welcome_page}}",
        context.getString(R.string.spotandshare_title_share_apk_welcome_page));
    tokensMap.put("{{spotandshare_message_share_apk_welcome_download_aptoide}}",
        context.getString(R.string.spotandshare_message_share_apk_welcome_download_aptoide));
    tokensMap.put("{{spotandshare_message_share_apk_welcome_install_when_completed}}",
        context.getString(R.string.spotandshare_message_share_apk_welcome_install_when_completed));
    tokensMap.put("{{spotandshare_message_share_apk_welcome_ask_your_friend}}",
        context.getString(R.string.spotandshare_message_share_apk_welcome_ask_your_friend));

    return tokensMap;
  }

  private static String getPathAndroid(Context context) {
    String sourceDir = null;
    try {
      sourceDir = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0).applicationInfo.sourceDir;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return sourceDir;
  }

  private static String getFileName(Context context) {
    String versionName = null;
    String appName = null;
    try {
      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      versionName = packageInfo.versionName;
      appName = getApplicationName(context);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return appName + "-v" + versionName + ".apk";
  }

  private static String getApplicationName(Context context) {
    ApplicationInfo applicationInfo = context.getApplicationInfo();
    int stringId = applicationInfo.labelRes;
    return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString()
        : context.getString(stringId);
  }

  @Override public Response serve(IHTTPSession session) {
    return shareApkServer.serve(session);
  }

  public void start() throws IOException {
    start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
  }
}
