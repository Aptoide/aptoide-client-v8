package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
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

  void openAppSelectionFragment(boolean fromMainFragment);

  void openEditProfile();

  void loadProfileInformation(SpotAndShareLocalUser user);

  Observable<Void> shareAptoideApk();

  void openShareAptoideFragment();
}
