package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshareapp.AppModel;
import java.util.List;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndShareAppSelectionView extends View {

  void finish();

  void setupRecyclerView(List<AppModel> installedApps);
}
