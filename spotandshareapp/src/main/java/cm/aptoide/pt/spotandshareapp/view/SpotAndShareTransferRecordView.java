package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndShareTransferRecordView extends View {
  void finish();

  Observable<AndroidAppInfo> acceptApp();

  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();
}
