package cm.aptoide.pt.shareappsandroid;

import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public interface HighwayTransferRecordView {

  void setUpSendButtonListener();

  void setUpClearHistoryListener();

  void handleReceivedApp(boolean received, boolean needReSend, String tmpFilePath);

  void showNewCard(HighwayTransferRecordItem item);

  void updateItemStatus(int positionToUpdate, boolean isSent,
      boolean needReSend);//could change to the packageName

  void showNoConnectedClientsToast();

  void openAppSelectionView();

  void showNoRecordsToDeleteToast();

  void showDeleteHistoryDialog();

  void refreshAdapter(List<HighwayTransferRecordItem> toRemoveList);

  void hideReceivedAppMenu();

  void showInstallErrorDialog(String appName);

  void showDialogToInstall(String appName, String filePath, String packageName);

  void showDialogToDelete(HighwayTransferRecordItem item);

  void setAdapterListeners(TransferRecordListener listener);

  void notifyChanged();

  void generateAdapter(List<HighwayTransferRecordItem> list);

  void showGeneralErrorToast(boolean isHotspot);

  void showRecoveringWifiStateToast();

  void dismiss();

  void showServerLeftMessage();

  void clearAdapter();

  interface TransferRecordListener {
    void onInstallApp(HighwayTransferRecordItem item);

    void onDeleteApp(HighwayTransferRecordItem item);

    void onReSendApp(HighwayTransferRecordItem item, int position);
  }
}
