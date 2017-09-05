package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.AppModel;
import rx.Observable;

/**
 * Created by filipe on 07-07-2017.
 */

public interface SpotAndShareWaitingToSendView extends View {
  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void openTransferRecord();

  AppModel getSelectedApp();
}
