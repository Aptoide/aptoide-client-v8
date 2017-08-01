package cm.aptoide.pt.spotandshare.presenter;

import cm.aptoide.pt.spotandshare.transference.TransferRecordItem;
import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 */

public interface TransferRecordView {

  void setUpSendButtonListener();

  void setUpClearHistoryListener();

  void showNewCard(TransferRecordItem item);

  void updateItemStatus(int positionToUpdate, boolean isSent,
      boolean needReSend);//could change to the packageName

  void openAppSelectionView();

  void showNoRecordsToDeleteToast();

  void showDeleteHistoryDialog();

  void refreshAdapter(List<TransferRecordItem> toRemoveList);

  void hideReceivedAppMenu();

  void showInstallErrorDialog(String appName);

  void showDialogToInstall(String appName, String filePath, String packageName);

  void showDialogToDelete(TransferRecordItem item);

  void setAdapterListeners(TransferRecordListener listener);

  void notifyChanged();

  void generateAdapter(List<TransferRecordItem> list);

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
    void onInstallApp(TransferRecordItem item);

    void onDeleteApp(TransferRecordItem item);

    void onReSendApp(TransferRecordItem item, int position);
  }
}
