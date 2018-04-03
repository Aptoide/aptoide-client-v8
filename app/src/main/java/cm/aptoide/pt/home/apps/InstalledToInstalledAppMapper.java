package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.database.realm.Installed;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 3/14/18.
 */

class InstalledToInstalledAppMapper {

  public List<App> getInstalledApps(List<Installed> installeds) {
    List<App> installedAppsList = new ArrayList<>();

    for (int i = 0; i < installeds.size(); i++) {
      Installed installed = installeds.get(i);
      installedAppsList.add(new InstalledApp(installed.getName(), installed.getPackageName(),
          installed.getVersionName(), installed.getIcon()));
    }
    return installedAppsList;
  }
}
