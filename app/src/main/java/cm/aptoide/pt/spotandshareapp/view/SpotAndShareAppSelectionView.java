package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.AppModel;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 28-07-2017.
 */

public interface SpotAndShareAppSelectionView extends View {

  Observable<Void> backButtonEvent();

  void showExitWarning();

  Observable<Void> exitEvent();

  void navigateBack();

  void onLeaveGroupError();

  void showTimeoutCreateGroupError();

  void showGeneralCreateGroupError();

  void navigateBackWithStateLoss();

  Observable<Void> skipButtonClick();

  void openWaitingToSendScreen();

  void openWaitingToSendScreen(AppModel appModel);

  void showLoading();

  void buildInstalledAppsList(List<AppModel> installedApps);

  Observable<AppModel> selectedApp();

  void openTransferRecord();
}
