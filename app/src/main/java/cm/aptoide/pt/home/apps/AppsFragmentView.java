package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.presenter.View;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public interface AppsFragmentView extends View {

  void showUpdatesList(List<App> list);

  void showInstalledApps(List<App> installedApps);

  void showDownloadsList(List<App> list);
}
