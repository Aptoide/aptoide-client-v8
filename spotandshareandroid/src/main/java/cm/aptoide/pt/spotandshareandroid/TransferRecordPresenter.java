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
  private final ApplicationDisconnecter disconnecter;
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
      TransferRecordManager transferRecordManager, boolean isHotspot,
      ApplicationDisconnecter disconnecter, ConnectionManager connectionManager,
      SpotAndShareAnalyticsInterface anaylitics) {
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
        //                String receivedApkFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + tmpFilePath;
        HighwayTransferRecordItem item =
            transferRecordManager.readApkArchive(appName, filePath, needReSend);
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
        view.showGeneralErrorToast(isHotspot);
        recoverNetworkState();
        cleanAPTXNetworks();
        analytics.receiveApkFailed();
        view.dismiss();
      }

      @Override public void onServerLeft() {
        view.showServerLeftMessage();
        recoverNetworkState();
        cleanAPTXNetworks();
        view.dismiss();
      }
    });
    setTransferRecordListener();

    applicationSender.setSendListener(new ApplicationSender.SendListener() {
      @Override public void onAppStartingToSend(String appName, String packageName, boolean isSent,
          boolean needReSend, int positionToReSend) {
        //need more, so that i can go find the apk info /drawable ,etc
        if (positionToReSend == 100000) {
          HighwayTransferRecordItem tmp =
              transferRecordManager.startedSending(appName, packageName, needReSend, isSent);
          if (!listOfApps.contains(tmp)) {
            listOfApps.add(tmp);
            view.showNewCard(tmp);
          }
        } else {
          //          listOfApps.get(positionToReSend).setSent(isSent);
          //          listOfApps.get(positionToReSend).setNeedReSend(needReSend);
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
              if (listOfApps.get(i).getAppName().equals(appName) && !received && !listOfApps.get(i)
                  .isSent()) {
                listOfApps.get(i).setNeedReSend(needReSend);
                listOfApps.get(i).setSent(isSent);
                view.updateItemStatus(i, isSent, needReSend);
                //                            i=-1;//to do only for the last app sent with this name.
                if (!view.getTransparencyClearHistory()) {
                  view.setTransparencyClearHistory(false);
                }
              }
            }
          } else {
            //deal with final try to re-send
            listOfApps.get(positionToReSend).setNeedReSend(needReSend);
            listOfApps.get(positionToReSend).setSent(isSent);

            if (!view.getTransparencyClearHistory()) {
              view.setTransparencyClearHistory(false);
            }
          }
        }
      }

      @Override public void onErrorSendingApp() {
        //handle error
        analytics.sendApkFailed();
        view.showGeneralErrorToast(isHotspot);
      }
    });

    applicationSender.setHostsListener(new ApplicationSender.HostsListener() {
      @Override public void onNoClients() {
        view.setTransparencySend(true);
      }

      @Override public void onAvailableClients() {
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
    //connectionManager.cleanNetworks();
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

  public void cleanAPTXNetworks() {
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
          System.out.println("I will install the app " + appName);
          view.showDialogToInstall(appName, filePath, packageName);
        }
      }

      @Override public void onDeleteApp(HighwayTransferRecordItem item) {

        view.showDialogToDelete(item);
        //                Dialog deleteDialog = createDialogToDelete(v, position);
        //                deleteDialog.show();
      }

      @Override public void onReSendApp(HighwayTransferRecordItem item,
          int position) {//// TODO: extrair o item para um App e , extrair o send files do chat activity p aplicationSEnder

        App app = transferRecordManager.convertTransferRecordItemToApp(item);
        //                String filePathToReSend = item.getFilePath();
        //                String appName = item.getAppName();
        //                String packageName = item.getPackageName();
        //                Drawable imageIcon= item.getIcon();
        //                String origin=item.getFromOutside();
        //                System.out.println("TransferRecordAdapter : here is the filePathToResend :  "+filePathToReSend);
        //                List<App> list= new ArrayList<App>();
        //                App tmpItem = new App(imageIcon, appName, packageName, filePathToReSend, origin);
        //
        //
        //                String obbsFilePath=((HighwayTransferRecordActivity) activity).checkIfHasObb(packageName);
        //                //add obb path
        //                tmpItem.setObbsFilePath(obbsFilePath);

        //NOT TO DELETE : safe way to detect position to resend?
        //                int positionToResend=listOfApps.lastIndexOf(item);
        List<App> list = new ArrayList<App>();
        list.add(app);

        applicationSender.reSendApp(list, position);
        //                ((HighwayTransferRecordActivity)activity).sendFiles(list, position) ;
      }
    });
  }

  public void receivedAnApp(boolean received, boolean needReSend, String tmpfilePath) {
    //        transferRecordManager.receivedApp(); //TODO i have been doing this from the activity towards the presenter.
    //if using the broadcasts, then it will be different.

  }

  public void setListOfConnectedClients(List<Host> connectedClients) {
    this.connectedClients = connectedClients;
  }

  public void clearConnectedClientsList() {
    this.connectedClients.clear();
  }

  public void clickedOnSendButton() {
    if (isHotspot) {
      connectedClients = DataHolder.getInstance()
          .getConnectedClients();//to-do extract to a model with a broadcastreceiver to listen to connect's and disconnect's
      if (connectedClients == null || connectedClients.size() < 2) {
        view.showNoConnectedClientsToast();
      } else {
        view.openAppSelectionView();
      }
    } else {
      view.openAppSelectionView();
    }
  }

  public void clickedOnClearHistoryButton() {

    System.out.println(" Deleting history");

    if (listOfApps == null || listOfApps.isEmpty()) {
      System.out.println("Trying to delete the emtpy list ! ");
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
    disconnecter.listenToDisconnect(new ApplicationDisconnecter.DisconnectListener() {
      @Override public void onServerDisconnected() {
        recoverNetworkState();
        view.dismiss();
      }

      @Override public void onClientDisconnected() {
        recoverNetworkState();
        cleanAPTXNetworks();
        view.dismiss();
      }
    });
  }
}
