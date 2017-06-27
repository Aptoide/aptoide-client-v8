package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

/**
 * Created by filipe on 08-06-2017.
 */

public interface SpotAndShareMainFragmentView extends View {

  void finish();

  Observable<Void> startSend();

  Observable<Void> startReceive();

  Observable<Void> editProfile();

  void openWaitingToReceiveFragment();

  void openAppSelectionFragment();

  boolean requestPermissionToReceiveApp(AndroidAppInfo androidAppInfo);

  void onCreateGroupError(Throwable throwable);

  void onJoinGroupError(Throwable throwable);

  void openEditProfile();

  void loadProfileInformation(SpotAndShareUser user);
}
