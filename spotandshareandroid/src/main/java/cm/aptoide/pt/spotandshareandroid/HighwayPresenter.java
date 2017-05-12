package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

import android.os.Build;
import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import java.util.ArrayList;
import rx.Subscription;

/**
 * This class will store the logic of the HighwayActivity
 */
public class HighwayPresenter implements Presenter {

  private final HighwayView view;
  private final DeactivateHotspotTask deactivateHotspotTask;
  private final ConnectionManager connectionManager;
  private final PermissionManager permissionManager;
  private GroupNameProvider groupNameProvider;
  private SpotAndShareAnalyticsInterface analytics;
  private GroupManager groupManager;
  private boolean permissionRequested;
  private Subscription subscription;
  private String autoShareAppName;
  private String autoShareFilepath;

  public HighwayPresenter(HighwayView view, GroupNameProvider groupNameProvider,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface analytics, GroupManager groupManager,
      PermissionManager permissionManager) {
    this.view = view;
    this.groupNameProvider = groupNameProvider;
    this.deactivateHotspotTask = deactivateHotspotTask;
    this.connectionManager = connectionManager;
    this.analytics = analytics;
    this.groupManager = groupManager;
    this.permissionManager = permissionManager;
  }

  public HighwayPresenter(HighwayView view, GroupNameProvider groupNameProvider,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface analytics, GroupManager groupManager,
      PermissionManager permissionManager, String autoShareAppName, String autoShareFilepath) {
    this(view, groupNameProvider, deactivateHotspotTask, connectionManager, analytics, groupManager,
        permissionManager);
    this.autoShareAppName = autoShareAppName;
    this.autoShareFilepath = autoShareFilepath;
  }

  @Override public void onCreate() {

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
  }

  @Override public void onResume() {
    if (!permissionManager.checkPermissions() && !permissionRequested) {
      permissionManager.requestPermissions();
      permissionRequested = true;
    }
  }

  @Override public void onPause() {

  }

  @Override public void onDestroy() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
    deactivateHotspotTask.cancel(false);
    connectionManager.stop();
    groupManager.stop();
    permissionManager.removeListener();
    autoShareFilepath = null;
  }

  @Override public void onStop() {

  }

  @Override public void onStart() {

  }

  private void deactivateHotspot() {
    deactivateHotspotTask.setListener(new SimpleListener() {
      @Override public void onEvent() {
        connectionManager.start(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            if (enabled) {

              if (autoShareFilepath != null) {
                joinShareFromAppView(autoShareAppName, autoShareFilepath);
                autoShareAppName = null;
                autoShareFilepath = null;
              } else {
                connectionManager.cleanNetworks();
                view.showConnections();
                view.setUpListeners();
                view.enableButtons(true);
              }
            }
          }
        });
        connectionManager.enableWifi(true);
      }
    });
    deactivateHotspotTask.execute();
  }

  public void onActivityResult(Group group) {
    groupManager.retryToJoinGroup(group);
  }

  public void clickJoinGroup(Group group) {
    view.enableButtons(false);
    groupManager.joinGroup(group, new GroupManager.JoinGroupListener() {
      @Override public void onSuccess(final String fullGroupName) {
        //analytics - track event
        connectionManager.evaluateWifi(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            if (enabled) {
              view.hideButtonsProgressBar();
              System.out.println("Inside presenter on Success for join group");
              analytics.joinGroupSuccess();
              view.enableButtons(true);
              String ipAddress = connectionManager.getIPAddress();
              view.openChatClient(ipAddress, fullGroupName, null);
            } else {
              view.hideButtonsProgressBar();
              view.enableButtons(true);
              view.hideSearchGroupsTextview(false);
              view.showJoinGroupResult(ConnectionManager.ERROR_UNKNOWN);
            }
          }
        });
      }

      @Override public void onError(int result) {
        view.showJoinGroupResult(result);
        view.hideButtonsProgressBar();
        view.hideSearchGroupsTextview(false);
        view.enableButtons(true);
      }
    });
  }

  public void clickCreateGroup() {
    view.enableButtons(false);
    subscription = groupNameProvider.getName()
        .subscribe(deviceName -> {
          groupManager.createGroup(deviceName, new GroupManager.CreateGroupListener() {
            @Override public void onSuccess() {
              view.hideButtonsProgressBar();
              view.enableButtons(true);
              analytics.createGroupSuccess();
              view.openChatHotspot(null, deviceName);//null is due to the outsidesharemanager.
            }

            @Override public void onError(int result) {
              view.showCreateGroupResult(result);
              view.hideButtonsProgressBar();
              view.enableButtons(true);
              view.hideSearchGroupsTextview(false);
            }
          });
        });
  }

  public void scanNetworks() {
    connectionManager.searchForAPTXVNetworks(new ConnectionManager.InactivityListener() {
      @Override public void onInactivity(boolean inactive) {
        view.showInactivityToast();
      }
    }, new ConnectionManager.ClientsConnectedListener() {
      @Override public void onNewClientsConnected(ArrayList<Group> clients) {
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

  public void recoverNetworkState() {
    connectionManager.recoverNetworkState();
    view.showRecoveringWifiStateToast();
  }

  public void forgetAPTXVNetwork() {
    connectionManager.cleanNetworks();
  }

  public void joinShareFromAppView(String appName, String appFilepath) {
    //subscription = groupNameProvider.getName().subscribe(deviceName -> {
    groupManager.createGroup(appName, new GroupManager.CreateGroupListener() {
      @Override public void onSuccess() {
        analytics.createGroupSuccess();
        view.openChatFromAppViewShare(appName, appFilepath);
      }

      @Override public void onError(int result) {
        view.showCreateGroupResult(result);
        view.hideButtonsProgressBar();
        view.enableButtons(true);
        view.hideSearchGroupsTextview(false);
      }
    });
    //});
  }
}