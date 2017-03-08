package cm.aptoide.pt.shareappsandroid;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

import android.net.Uri;
import android.os.Build;
import cm.aptoide.pt.shareappsandroid.analytics.SpotAndShareAnalyticsInterface;
import java.util.ArrayList;

/**
 * This class will store the logic of the HighwayActivity
 */
public class HighwayPresenter implements Presenter {

  private final HighwayView view;
  private final DeactivateHotspotTask deactivateHotspotTask;
  private final ConnectionManager connectionManager;
  private final PermissionManager permissionManager;
  private String deviceName;
  private boolean mobileData;
  private boolean mobileDataDialog;
  private boolean outsideShare;
  private boolean joinGroupFlag;//to allow only 1 press of the radar elements
  private SpotAndShareAnalyticsInterface analytics;
  private GroupManager groupManager;
  private OutsideShareManager outsideShareManager;
  private boolean isOutsideShare;
  private boolean permissionRequested;

  private SpotAndShareAnalyticsInterface spotAndShareAnalyticsInterface;

  public HighwayPresenter(HighwayView view, String deviceName,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface analytics, GroupManager groupManager,
      PermissionManager permissionManager) {
    this.view = view;
    this.deviceName = deviceName;
    this.deactivateHotspotTask = deactivateHotspotTask;
    this.connectionManager = connectionManager;
    this.analytics = analytics;
    this.groupManager = groupManager;
    this.permissionManager = permissionManager;
    this.spotAndShareAnalyticsInterface = spotAndShareAnalyticsInterface;
  }

  @Override public void onCreate() {

    deviceName = Utils.getDeviceName();
    mobileDataDialog = false;
    outsideShare = false;
    joinGroupFlag = false;

    permissionManager.registerListener(new PermissionListener() {
      @Override public void onPermissionGranted() {

        deactivateHotspot();
        permissionRequested = false;
      }

      @Override public void onPermissionDenied() {
        view.dismiss();
        permissionRequested = false;
      }
    });

    if (permissionManager.checkPermissions()) {
      deactivateHotspot();
    }
    //else if(!permissionManager.checkPermissions() ){//already requested the permissions but didn't accept all.
    //  System.out.println("requesting permissions again, because he didn't accept all. ");
    //  permissionManager.requestPermissions();
    //}

  }

  private void deactivateHotspot() {
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
    if (!permissionManager.checkPermissions() && !permissionRequested) {
      permissionManager.requestPermissions();
      permissionRequested = true;
    }
  }

  @Override public void onPause() {
    //call groupmanager.pause etc.
    //nothing to put.

  }

  @Override public void onDestroy() {
    deactivateHotspotTask.cancel(false);
    connectionManager.stop();
    groupManager.stop();
    permissionManager.removeListener();
  }

  @Override public void onStop() {

  }

  @Override public void onStart() {
    //if(!permissionManager.checkPermissions()){
    //  permissionManager.requestPermissions();
    //}
  }

  public void onActivityResult(Group group) {
    groupManager.retryToJoinGroup(group);
  }

  public void clickJoinGroup(Group group) {
    view.enableButtons(false);
    groupManager.joinGroup(group, new GroupManager.GroupListener() {
      @Override public void onSuccess() {
        //analytics - track event
        connectionManager.evaluateWifi(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            if (enabled) {
              view.hideButtonsProgressBar();
              System.out.println("Inside presenter on Success for join group");
              analytics.joinGroupSuccess();
              view.enableButtons(true);
              String ipAddress = connectionManager.getIPAddress();
              if (outsideShareManager != null) {
                ArrayList<String> pathsFromOutside = outsideShareManager.getPathsFromOutsideShare();
                view.openChatClient(ipAddress, deviceName, pathsFromOutside);
              } else {
                view.openChatClient(ipAddress, deviceName, null);
              }
            } else {
              view.hideButtonsProgressBar();
              view.enableButtons(true);
              view.showJoinGroupResult(ConnectionManager.ERROR_UNKNOWN);
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
        analytics.createGroupSuccess();
        if (outsideShareManager != null) {
          ArrayList<String> pathsFromOutside = outsideShareManager.getPathsFromOutsideShare();
          view.openChatHotspot(pathsFromOutside, deviceName);
        } else {
          view.openChatHotspot(null, deviceName);
        }
      }

      @Override public void onError(int result) {
        view.showCreateGroupResult(result);
        view.hideButtonsProgressBar();
        view.enableButtons(true);
      }
    });
  }

  public void scanNetworks() {
    //view.showEnablingWifiToast();
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
    analytics.joinGroupFailed();
  }

  public void tagAnalyticsUnsuccessCreate() {
    analytics.createGroupFailed();
  }

  public void getAppFilePathFromOutside(Uri uri) {
    isOutsideShare = true;
    //generateLocalyticsSettings();
    outsideShareManager.getApp(uri);
  }
  //
  //public void generateLocalyticsSettings() {
  //  analytics.generateLocalyticsSettings();
  //}

  public void getMultipleAppFilePathsFromOutside(ArrayList<Uri> list) {
    isOutsideShare = true;
    //generateLocalyticsSettings();
    outsideShareManager.getMultipleApps(list);
  }

  public void setOutsideShareManager(OutsideShareManager outsideShareManager) {
    this.outsideShareManager = outsideShareManager;
  }

  public void recoverNetworkState() {
    connectionManager.recoverNetworkState();
    view.showRecoveringWifiStateToast();
  }

  public void forgetAPTXNetwork() {
    connectionManager.cleanNetworks();
  }
}