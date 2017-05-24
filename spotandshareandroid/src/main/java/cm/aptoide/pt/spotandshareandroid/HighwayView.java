package cm.aptoide.pt.spotandshareandroid;

import java.util.ArrayList;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public interface HighwayView {

  void showConnections();

  void enableButtons(boolean enable);

  void setupViews();

  void showJoinGroupResult(int result);

  void showCreateGroupResult(int result);

  void showInactivityToast();

  void hideButtonsProgressBar();

  void openChatClient(String ipAddress, String deviceName, ArrayList<String> pathsFromOutside);

  void openChatHotspot(ArrayList<String> pathsFromOutside, String deviceName);

  void refreshRadar(ArrayList<Group> clients);

  void showRecoveringWifiStateToast();

  void dismiss();

  void hideSearchGroupsTextview(boolean hide);

  void openChatFromAppViewShare(String deviceName, String appFilepath);

  void paintSelectedGroup(Group group);

  void deselectHotspot(Group group);

  void openShareAptoide(String Ssid);

  void showShareAptoideApk();

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
