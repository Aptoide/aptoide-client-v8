package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;
import java.util.List;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public interface AppsFragmentView extends View {

  void showUpdatesList(List<UpdateApp> list);

  void showInstalledApps(List<InstalledApp> installedApps);
}
