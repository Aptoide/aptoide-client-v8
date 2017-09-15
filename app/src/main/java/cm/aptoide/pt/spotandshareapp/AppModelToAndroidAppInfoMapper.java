package cm.aptoide.pt.spotandshareapp;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import java.io.File;

/**
 * Created by filipe on 24-07-2017.
 */

public class AppModelToAndroidAppInfoMapper {

  private ObbsProvider obbsProvider;

  public AppModelToAndroidAppInfoMapper(ObbsProvider obbsProvider) {
    this.obbsProvider = obbsProvider;
  }

  public AndroidAppInfo convertAppModelToAndroidAppInfo(AppModel appModel) {

    String appName = appModel.getAppName();
    String packageName = appModel.getPackageName();
    File apk = new File(appModel.getFilePath());
    byte[] bitmapdata = appModel.getAppIconAsByteArray();

    AndroidAppInfo androidAppInfo;
    if (!appModel.getObbsFilePath()
        .equals(SpotAndShareAppProvider.NO_OBBS)) {

      File[] obbsList = obbsProvider.getObbsList(appModel.getObbsFilePath());

      androidAppInfo =
          new AndroidAppInfo(appName, packageName, apk, obbsList[0], obbsList[1], bitmapdata, null);
    } else {
      androidAppInfo = new AndroidAppInfo(appName, packageName, apk, null, null, bitmapdata, null);
    }

    return androidAppInfo;
  }
}
