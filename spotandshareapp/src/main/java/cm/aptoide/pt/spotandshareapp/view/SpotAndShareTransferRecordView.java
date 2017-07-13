package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndShareTransferRecordView extends View {
  void finish();

  Observable<TransferAppModel> acceptApp();

  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void updateReceivedAppsList(List<TransferAppModel> transferAppModelList);

  Observable<Void> shareApp();

  void openAppSelectionFragment(boolean shouldCreateGroup);

  Observable<TransferAppModel> installApp();
}
