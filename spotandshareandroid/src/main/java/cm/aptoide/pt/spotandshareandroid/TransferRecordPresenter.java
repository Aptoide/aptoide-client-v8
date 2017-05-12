package cm.aptoide.pt.spotandshareandroid;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshareandroid.analytics.SpotAndShareAnalyticsInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public class TransferRecordPresenter implements Presenter {

  private final ConnectionManager connectionManager;
  private final Disconnecter disconnecter;
  private HighwayTransferRecordView view;
  private List<HighwayTransferRecordItem> listOfApps;
  private List<Host> connectedClients;
  private ApplicationReceiver applicationReceiver;
  private ApplicationSender applicationSender;
  private TransferRecordManager transferRecordManager;
  private boolean isHotspot;
  private SpotAndShareAnalyticsInterface analytics;

  public TransferRecordPresenter(HighwayTransferRecordView view,
      ApplicationReceiver applicationReceiver, ApplicationSender applicationSender,
      TransferRecordManager transferRecordManager, boolean isHotspot, Disconnecter disconnecter,
      ConnectionManager connectionManager, SpotAndShareAnalyticsInterface anaylitics) {
    this.view = view;
    this.applicationReceiver = applicationReceiver;
    this.applicationSender = applicationSender;
    this.transferRecordManager = transferRecordManager;
    this.isHotspot = isHotspot;
    this.disconnecter = disconnecter;
    this.connectionManager = connectionManager;
    listOfApps = new ArrayList<>();
    this.analytics = anaylitics;
  }

  @Override public void onCreate() {
    view.setUpSendButtonListener();
    view.setUpClearHistoryListener();
    view.generateAdapter(listOfApps);
    applicationReceiver.startListening(new ApplicationReceiver.ReceiveAppListener() {
      @Override public void onStartedReceiving(String appName) {
        //show notification
        //                view.showNotification();
        //future
      }

      @Override public void onReceivedApp(String appName, String filePath, boolean needReSend) {
        HighwayTransferRecordItem item = transferRecordManager.readApkArchive(appName, filePath);
        if (!listOfApps.contains(item)) {
          listOfApps.add(item);
        }
        analytics.receiveApkSuccess();
        view.showNewCard(item);

        if (!view.getTransparencyClearHistory()) {
          view.setTransparencyClearHistory(false);
        }
      }

      @Override public void onErrorReceiving() {
        //handling error
        view.showGeneralErrorToast();
        if (isHotspot) {
          view.setInitialApConfig();
        }
        recoverNetworkState();
        cleanAPTXVNetworks();
        analytics.receiveApkFailed();
        view.dismiss();
      }

      @Override public void onServerLeft() {
        view.showServerLeftMessage();
        recoverNetworkState();
        cleanAPTXVNetworks();
        view.dismiss();
      }
    });
    setTransferRecordListener();

    applicationSender.setSendListener(new ApplicationSender.SendListener() {
      @Override public void onAppStartingToSend(String appName, String packageName, boolean isSent,
          boolean needReSend, int positionToReSend) {
        if (positionToReSend == 100000) {
          HighwayTransferRecordItem tmp =
              transferRecordManager.startedSending(appName, packageName, needReSend, isSent);
          if (!listOfApps.contains(tmp)) {
            listOfApps.add(tmp);
            view.showNewCard(tmp);
          }
        } else {

          view.updateItemStatus(positionToReSend, isSent, needReSend);
        }
      }

      @Override
      public void onAppSent(String appName, boolean needReSend, boolean isSent, boolean received,
          int positionToReSend) {
        analytics.sendApkSuccess();
        if (listOfApps.size() > 0) {
          if (positionToReSend == 100000) {
            for (int i = listOfApps.size() - 1; i >= 0; i--) {
              if (listOfApps.get(i)
                  .getAppName()
                  .equals(appName) && !received && !listOfApps.get(i)
                  .isSent()) {
                listOfApps.get(i)
                    .setNeedReSend(needReSend);
                listOfApps.get(i)
                    .setSent(isSent);
                view.updateItemStatus(i, isSent, needReSend);
                if (!view.getTransparencyClearHistory()) {
                  view.setTransparencyClearHistory(false);
                }
              }
            }
          } else {
            listOfApps.get(positionToReSend)
                .setNeedReSend(needReSend);
            listOfApps.get(positionToReSend)
                .setSent(isSent);

            if (!view.getTransparencyClearHistory()) {
              view.setTransparencyClearHistory(false);
            }
          }
        }
      }

      @Override public void onErrorSendingApp() {
        analytics.sendApkFailed();
        view.showGeneralErrorToast();
      }
    });

    applicationSender.setHostsListener(new ApplicationSender.HostsListener() {
      @Override public void onNoClients() {
        view.setTransparencySend(true);
        view.setTextViewMessage(false);
      }

      @Override public void onAvailableClients() {
        System.out.println("inside onAvailableClients");
        if (!view.getTransparencySend()) {
          System.out.println("ordered to change transparency");
          view.setTransparencySend(false);
          view.setTextViewMessage(true);
        }
      }

      @Override public void onAutoShare(String filepath) {
        App app = transferRecordManager.readApkArchive(filepath);
        List<App> appList = new ArrayList<App>();
        appList.add(app);
        applicationSender.sendApp(appList);
        if (!view.getTransparencySend()) {
          view.setTransparencySend(false);
          view.setTextViewMessage(true);
        }
      }
    });
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }

  @Override public void onDestroy() {
    applicationReceiver.stop();
    applicationSender.stop();
    transferRecordManager.stop();
    if (disconnecter != null) {
      disconnecter.stop();
    }
    if (listOfApps != null) {
      listOfApps.clear();
      view.clearAdapter();
      view.notifyChanged();
    }
  }

  @Override public void onStop() {

  }

  @Override public void onStart() {

  }

  public void recoverNetworkState() {
    if (connectionManager != null) {
      connectionManager.recoverNetworkState();
      view.showRecoveringWifiStateToast();
    }
  }

  public void cleanAPTXVNetworks() {
    if (connectionManager != null) {
      connectionManager.cleanNetworks();
    }
  }

  private void setTransferRecordListener() {
    view.setAdapterListeners(new HighwayTransferRecordView.TransferRecordListener() {
      @Override public void onInstallApp(HighwayTransferRecordItem item) {

        String appName = item.getAppName();
        String filePath = item.getFilePath();
        String packageName = item.getPackageName();
        if (filePath.equals("Could not read the original filepath")) {
          view.showInstallErrorDialog(appName);
        } else {
          view.showDialogToInstall(appName, filePath, packageName);
        }
      }

      @Override public void onDeleteApp(HighwayTransferRecordItem item) {

        view.showDialogToDelete(item);
      }

      @Override public void onReSendApp(HighwayTransferRecordItem item,
          int position) {//// TODO: extrair o item para um App e , extrair o send files do chat activity p aplicationSEnder

        App app = transferRecordManager.convertTransferRecordItemToApp(item);

        List<App> list = new ArrayList<App>();
        list.add(app);

        applicationSender.reSendApp(list, position);
      }
    });
  }

  public void receivedAnApp(boolean received, boolean needReSend, String tmpfilePath) {
    //        transferRecordManager.receivedApp(); //TODO i have been doing this from the activity towards the presenter.
    //if using the broadcasts, then it will be different.

  }

  public void clickedOnSendButton() {
    view.openAppSelectionView();
  }

  public void clickedOnClearHistoryButton() {

    if (listOfApps == null || listOfApps.isEmpty()) {
      view.showNoRecordsToDeleteToast();
    } else {
      view.showDeleteHistoryDialog();
    }
  }

  public void deleteAllApps() {
    transferRecordManager.deleteAllApps(new TransferRecordManager.DeleteAppsListener() {
      @Override public void onDeleteAllApps(List<HighwayTransferRecordItem> toRemoveList) {
        view.refreshAdapter(toRemoveList);
        view.hideReceivedAppMenu();
      }
    }, listOfApps);
  }

  public void deleteAppFile(HighwayTransferRecordItem item) {
    String filePath = item.getFilePath();
    transferRecordManager.deleteAppFile(filePath);
    item.setDeleted(true);
    view.notifyChanged();
  }

  public void installApp(String filePath, String packageName) {
    transferRecordManager.installApp(filePath, packageName);
  }

  public void listenToDisconnect() {
    disconnecter.listenToDisconnect(new Disconnecter.DisconnectListener() {
      @Override public void onServerDisconnected() {
        recoverNetworkState();
        view.dismiss();
      }

      @Override public void onClientDisconnected() {
        recoverNetworkState();
        cleanAPTXVNetworks();
        view.dismiss();
      }
    });
  }
}
