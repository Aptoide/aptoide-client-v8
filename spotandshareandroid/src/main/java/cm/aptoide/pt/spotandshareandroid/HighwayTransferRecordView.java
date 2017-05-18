package cm.aptoide.pt.spotandshareandroid;

import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public interface HighwayTransferRecordView {

  void setUpSendButtonListener();

  void setUpClearHistoryListener();

  void showNewCard(HighwayTransferRecordItem item);

  void updateItemStatus(int positionToUpdate, boolean isSent,
      boolean needReSend);//could change to the packageName

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

  void showGeneralErrorToast();

  void showRecoveringWifiStateToast();

  void dismiss();

  void showServerLeftMessage();

  void clearAdapter();

  boolean getTransparencySend();

  void setTransparencySend(boolean transparency);

  boolean getTransparencyClearHistory();

  void setTransparencyClearHistory(boolean transparency);

  void setTextViewMessage(boolean availableClients);

  void setInitialApConfig();

  interface TransferRecordListener {
    void onInstallApp(HighwayTransferRecordItem item);

    void onDeleteApp(HighwayTransferRecordItem item);

    void onReSendApp(HighwayTransferRecordItem item, int position);
  }
}
