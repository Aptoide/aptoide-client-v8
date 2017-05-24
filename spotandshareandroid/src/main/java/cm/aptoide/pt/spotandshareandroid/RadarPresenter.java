package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import java.util.ArrayList;
import rx.Subscription;

public class RadarPresenter implements Presenter {

  private final RadarView radarView;
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
  private Group chosenHotspot;
  private boolean showShareAptoideApk;

  public RadarPresenter(RadarView radarView, GroupNameProvider groupNameProvider,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface analytics, GroupManager groupManager,
      PermissionManager permissionManager, boolean showShareAptoideApk) {
    this.radarView = radarView;
    this.groupNameProvider = groupNameProvider;
    this.deactivateHotspotTask = deactivateHotspotTask;
    this.connectionManager = connectionManager;
    this.analytics = analytics;
    this.groupManager = groupManager;
    this.permissionManager = permissionManager;
    this.showShareAptoideApk = showShareAptoideApk;
  }

  public RadarPresenter(RadarView radarView, GroupNameProvider groupNameProvider,
      DeactivateHotspotTask deactivateHotspotTask, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface analytics, GroupManager groupManager,
      PermissionManager permissionManager, String autoShareAppName, String autoShareFilepath,
      boolean showShareAptoideApk) {
    this(radarView, groupNameProvider, deactivateHotspotTask, connectionManager, analytics,
        groupManager,
        permissionManager, showShareAptoideApk);
    this.autoShareAppName = autoShareAppName;
    this.autoShareFilepath = autoShareFilepath;
  }

  @Override public void onCreate() {

    if (showShareAptoideApk) {
      radarView.showShareAptoideApk();
    }

    permissionManager.registerListener(new PermissionListener() {
      @Override public void onPermissionGranted() {

        deactivateHotspot();
        permissionRequested = false;
      }

      @Override public void onPermissionDenied() {
        radarView.dismiss();
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
                radarView.showConnections();
                radarView.setupViews();
                radarView.enableButtons(true);
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
    radarView.enableButtons(false);
    groupManager.joinGroup(group, new GroupManager.JoinGroupListener() {
      @Override public void onSuccess(final String fullGroupName) {
        //analytics - track event
        connectionManager.evaluateWifi(new ConnectionManager.WifiStateListener() {
          @Override public void onStateChanged(boolean enabled) {
            if (enabled) {
              radarView.hideButtonsProgressBar();
              analytics.joinGroupSuccess();
              radarView.enableButtons(true);
              String ipAddress = connectionManager.getIPAddress();
              radarView.openChatClient(ipAddress, fullGroupName, null);
            } else {
              radarView.hideButtonsProgressBar();
              radarView.enableButtons(true);
              radarView.hideSearchGroupsTextview(false);
              radarView.showJoinGroupResult(ConnectionManager.ERROR_UNKNOWN);
              if (chosenHotspot != null) {
                radarView.deselectHotspot(chosenHotspot);
              }
            }
          }
        });
      }

      @Override public void onError(int result) {
        radarView.showJoinGroupResult(result);
        radarView.hideButtonsProgressBar();
        radarView.hideSearchGroupsTextview(false);
        radarView.enableButtons(true);
      }
    });
  }

  public void clickCreateGroup() {
    radarView.enableButtons(false);
    subscription = groupNameProvider.getName()
        .subscribe(deviceName -> {
          groupManager.createGroup(deviceName, new GroupManager.CreateGroupListener() {
            @Override public void onSuccess() {
              radarView.hideButtonsProgressBar();
              radarView.enableButtons(true);
              analytics.createGroupSuccess();
              radarView.openChatHotspot(null, deviceName);//null is due to the outsidesharemanager.
            }

            @Override public void onError(int result) {
              radarView.showCreateGroupResult(result);
              radarView.hideButtonsProgressBar();
              radarView.enableButtons(true);
              radarView.hideSearchGroupsTextview(false);
            }
          });
        });
  }

  public void scanNetworks() {
    connectionManager.searchForAPTXVNetworks(new ConnectionManager.InactivityListener() {
      @Override public void onInactivity(boolean inactive) {
        radarView.showInactivityToast();
      }
    }, new ConnectionManager.ClientsConnectedListener() {
      @Override public void onNewClientsConnected(ArrayList<Group> clients) {
        radarView.refreshRadar(clients);
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
    radarView.showRecoveringWifiStateToast();
  }

  public void forgetAPTXVNetwork() {
    connectionManager.cleanNetworks();
  }

  public void joinShareFromAppView(String appName, String appFilepath) {
    //subscription = groupNameProvider.getName().subscribe(deviceName -> {
    groupManager.createGroup(appName, new GroupManager.CreateGroupListener() {
      @Override public void onSuccess() {
        analytics.createGroupSuccess();
        radarView.openChatFromAppViewShare(appName, appFilepath);
      }

      @Override public void onError(int result) {
        radarView.showCreateGroupResult(result);
        radarView.hideButtonsProgressBar();
        radarView.enableButtons(true);
        radarView.hideSearchGroupsTextview(false);
      }
    });
    //});
  }

  public void clickedOnGroup(Group group) {

    if (group != null && group.equals(this.chosenHotspot)) {
      radarView.deselectHotspot(group);
      this.chosenHotspot = null;
    } else {
      if (chosenHotspot != null) {
        radarView.deselectHotspot(this.chosenHotspot);
        this.chosenHotspot = null;
      }
      this.chosenHotspot = group;
      radarView.paintSelectedGroup(group);
      radarView.hideSearchGroupsTextview(true);
      clickJoinGroup(group);
    }
  }

  public void clickShareAptoide() {
    subscription = groupNameProvider.getName()
        .subscribe(deviceName -> radarView.openShareAptoide(deviceName),
            Throwable::printStackTrace);
  }
}
