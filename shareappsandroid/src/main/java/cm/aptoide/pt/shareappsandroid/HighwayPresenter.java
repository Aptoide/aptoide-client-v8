package cm.aptoide.pt.shareappsandroid;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

import android.net.Uri;
import android.os.Build;
import java.util.ArrayList;

/**
 * This class will store the logic of the HighwayActivity
 */
public class HighwayPresenter implements Presenter {

  private final HighwayView view;
  private final DeactivateHotspotTask deactivateHotspotTask;
  private final ConnectionManager connectionManager;
  private String deviceName;
  private boolean mobileData;
  private boolean mobileDataDialog;
  private boolean outsideShare;
  private boolean joinGroupFlag;//to allow only 1 press of the radar elements
  private AnalyticsManager analyticsManager;
  private GroupManager groupManager;
  private OutsideShareManager outsideShareManager;
  private boolean isOutsideShare;

  public HighwayPresenter(HighwayView view, String deviceName,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      AnalyticsManager analyticsManager, GroupManager groupManager) {
    this.view = view;
    this.deviceName = deviceName;
    this.deactivateHotspotTask = deactivateHotspotTask;
    this.connectionManager = connectionManager;
    this.analyticsManager = analyticsManager;
    this.groupManager = groupManager;
  }

  @Override public void onCreate() {

    deviceName = Utils.getDeviceName();
    mobileDataDialog = false;
    outsideShare = false;
    joinGroupFlag = false;

    deactivateHotspotTask.setListener(new SimpleListener() {
      @Override public void onEvent() {
        view.showConnections();//fillTheRadar
        view.setUpListeners();
        connectionManager.start(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            if (enabled) {
              connectionManager.cleanNetworks();
              view.enableButtons(true);
            }
          }
        });
      }
    });
    deactivateHotspotTask.execute();
  }

  @Override public void onResume() {
//    connectionManager.resume();
  }

  @Override public void onPause() {
    //call groupmanager.pause etc.
    //nothing to put.

  }

  @Override public void onDestroy() {
    deactivateHotspotTask.cancel(false);
    connectionManager.stop();
    groupManager.stop();
  }

  //should be on the presenter interface?
  public void onNewIntent() {

  }

  public void onActivityResult(Group group) {
    groupManager.retryToJoinGroup(group);
  }

  public void clickJoinGroup(Group group) {
    view.enableButtons(false);
    groupManager.joinGroup(group, new GroupManager.GroupListener() {
      @Override public void onSuccess() {
        //analyticsManager - track event
        connectionManager.evaluateWifi(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            view.hideButtonsProgressBar();
            System.out.println("Inside presenter on Success for join group");
            analyticsManager.joinGroupSuccess();
            view.enableButtons(true);
            String ipAddress = connectionManager.getIPAddress();
            if (outsideShareManager != null) {
              ArrayList<String> pathsFromOutside = outsideShareManager.getPathsFromOutsideShare();
              view.openChatClient(ipAddress, deviceName, pathsFromOutside);
            } else {
              view.openChatClient(ipAddress, deviceName, null);
            }
          }
        });
      }

      @Override public void onError(int result) {
        view.showJoinGroupResult(result);
        view.hideButtonsProgressBar();
        view.enableButtons(true);
      }
    });
  }

  public void clickCreateGroup() {
    String randomAlphaNum = connectionManager.generateRandomAlphanumericString(5);
    view.enableButtons(false);
    groupManager.createGroup(randomAlphaNum, deviceName, new GroupManager.GroupListener() {
      @Override public void onSuccess() {
        view.hideButtonsProgressBar();
        view.enableButtons(true);
        analyticsManager.createGroupSuccess();
        if (outsideShareManager != null) {
          ArrayList<String> pathsFromOutside = outsideShareManager.getPathsFromOutsideShare();
          view.openChatHotspot(pathsFromOutside, deviceName);
        } else {
          view.openChatHotspot(null, deviceName);
        }
      }

      @Override public void onError(int result) {
        view.showCreateGroupResult(result);
      }
    });
  }

  public void scanNetworks() {
    view.showEnablingWifiToast();
    connectionManager.searchForAPTXNetworks(new ConnectionManager.InactivityListener() {
      @Override public void onInactivity(boolean inactive) {
        view.showInactivityToast();
      }
    }, new ConnectionManager.ClientsConnectedListener() {
      @Override public void onNewClientsConnected(ArrayList<String> clients) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
          view.refreshRadarLowerVersions(clients);
        } else {
          view.refreshRadar(clients);
        }
      }
    });
  }

  public void tagAnalyticsUnsuccessJoin() {
    analyticsManager.joinGroupUnsuccess();
  }

  public void tagAnalyticsUnsuccessCreate() {
    analyticsManager.createGroupUnsuccess();
  }

  public void getAppFilePathFromOutside(Uri uri) {
    isOutsideShare = true;
    generateLocalyticsSettings();
    outsideShareManager.getApp(uri);
  }

  public void generateLocalyticsSettings() {
    analyticsManager.generateLocalyticsSettings();
  }

  public void getMultipleAppFilePathsFromOutside(ArrayList<Uri> list) {
    isOutsideShare = true;
    generateLocalyticsSettings();
    outsideShareManager.getMultipleApps(list);
  }

  public void setOutsideShareManager(OutsideShareManager outsideShareManager) {
    this.outsideShareManager = outsideShareManager;
  }

  public void recoverNetworkState(){
    connectionManager.recoverNetworkState();
    view.showRecoveringWifiStateToast();
  }

  public void forgetAPTXNetwork(){
    connectionManager.cleanNetworks();
  }
}