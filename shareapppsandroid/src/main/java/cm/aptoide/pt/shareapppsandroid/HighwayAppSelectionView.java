package cm.aptoide.pt.shareapppsandroid;

import java.util.List;

/**
 * Created by filipegoncalves on 07-02-2017.
 */
public interface HighwayAppSelectionView {

  void setUpSendListener();

  //    void removeSendListener();

  void showNoAppsSelectedToast();

  void sendMultipleFiles(List<App> selectedApps);

  void enableGridView(boolean enable);

  void generateAdapter(boolean isHotspot, List<AppViewModel> itemList);

  void setAppSelectionListener(AppSelectionListener listener);

  void removeAppSelectionListener();

  void notifyChanges();

  interface AppSelectionListener {

    void onAppSelected(AppViewModel item);
  }
}
