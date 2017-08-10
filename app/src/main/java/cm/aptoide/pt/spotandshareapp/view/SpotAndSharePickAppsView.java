package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.AppModel;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public interface SpotAndSharePickAppsView extends View {

  void finish();

  void buildInstalledAppsList(List<AppModel> installedApps);

  void onLeaveGroupError();

  Observable<AppModel> selectedApp();

  void openTransferRecord();

  void openWaitingToSendScreen(AppModel selectedApp);

  void onCreateGroupError(Throwable throwable);

  void hideLoading();

  void showLoading();

  void navigateBack();
}
