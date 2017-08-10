package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.v8engine.presenter.View;
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

  Observable<Void> clickedRefresh();

  void openTransferRecord();

  AppModel getSelectedApp();
}
