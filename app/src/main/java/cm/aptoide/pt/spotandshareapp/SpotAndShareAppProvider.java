package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by filipe on 19-06-2017.
 */

public class SpotAndShareAppProvider {
  public static final String NO_OBBS = "no_obbs";
  private Context context;
  private PackageManager packageManager;

  public SpotAndShareAppProvider(Context context, PackageManager packageManager) {
    this.context = context;
    this.packageManager = packageManager;
  }

  public List<AppModel> getInstalledApps() {
    List<AppModel> installedApps = new ArrayList<>();
    List<ApplicationInfo> packages =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

    for (ApplicationInfo applicationInfo : packages) {

      if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
          && applicationInfo.packageName != null) {

        AppModel app = new AppModel(applicationInfo.loadLabel(packageManager)
            .toString(), applicationInfo.packageName, applicationInfo.sourceDir,
            getObbFilePath(applicationInfo.packageName), applicationInfo.loadIcon(packageManager),
            new DrawableBitmapMapper(context));
        if (!installedApps.contains(app)) {
          installedApps.add(app);
        }
      }
    }

    Collections.sort(installedApps, (o1, o2) -> o1.getAppName()
        .toLowerCase()
        .compareTo(o2.getAppName()
            .toLowerCase()));

    return installedApps;
  }

  public String getObbFilePath(String packageName) {
    String obbsFilePath = NO_OBBS;
    String obbPath = Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/";
    File obbFolder = new File(obbPath);
    File[] list = obbFolder.listFiles();
    if (list != null) {
      if (list.length > 0) {
        for (int i = 0; i < list.length; i++) {
          if (list[i].getName()
              .equals(packageName)) {
            obbsFilePath = list[i].getAbsolutePath();
          }
        }
      }
    }
    return obbsFilePath;
  }
}
