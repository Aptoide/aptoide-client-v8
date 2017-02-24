package cm.aptoide.pt.shareappsandroid;

import java.util.ArrayList;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public interface HighwayView {

  void showConnections();

  void enableButtons(boolean enable);

  void setUpListeners();

  void showMobileDataDialog();

  void showMobileDataToast();

  void showJoinGroupResult(int result);

  void showCreateGroupResult(int result);

  void showEnablingWifiToast();

  void showInactivityToast();

  void hideButtonsProgressBar();

  void openChatClient(String ipAddress, String deviceName, ArrayList<String> pathsFromOutside);

  void openChatHotspot(ArrayList<String> pathsFromOutside, String deviceName);

  void refreshRadar(ArrayList<String> clients);

  void refreshRadarLowerVersions(ArrayList<String> clients);

  void showRecoveringWifiStateToast();

  void dismiss();

  //    void setCreateGroupListener(View.OnClickListener listener );
  //
  //    void setJoinGroupListener(View.OnClickListener listener);
  //
  //    void showConnectionNotSelectedError();
  //
  //    void setConnectionSelectedListener(SimpleListener listener);
  //
  //    void setUnknownError();
  //
  //    void showConnectionError();
  //
}
