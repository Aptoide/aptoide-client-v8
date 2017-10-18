package cm.aptoide.pt.spotandshareapp.view;

import rx.Observable;

/**
 * Created by filipe on 28-07-2017.
 */

public interface SpotAndShareAppSelectionView extends SpotAndSharePickAppsView {

  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void showTimeoutCreateGroupError();

  void showGeneralCreateGroupError();
}
