package cm.aptoide.pt.spotandshare.presenter;

import cm.aptoide.pt.spotandshare.app.AppViewModel;
import java.util.List;

/**
 * Created by filipegoncalves on 07-02-2017.
 */
public interface AppSelectionView {

  void setUpSendListener();

  void showNoAppsSelectedToast();

  void enableGridView(boolean enable);

  void generateAdapter(boolean isHotspot, List<AppViewModel> itemList);

  void setAppSelectionListener(AppSelectionListener listener);

  void removeAppSelectionListener();

  void notifyChanges();

  void goBackToTransferRecord();

  interface AppSelectionListener {

    void onAppSelected(AppViewModel item);
  }
}
