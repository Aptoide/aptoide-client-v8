package cm.aptoide.pt.spotandshareapp.view;

import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;

/**
 * Created by filipe on 21-06-2017.
 */

public interface SpotAndShareEditProfileView extends View {

  void finish();

  Observable<Void> cancelProfileChanges();

  Observable<SpotAndShareUser> saveProfileChanges();

  void goBack();

  Observable<Void> selectedFirstAvatar();

  Observable<Void> selectedSecondAvatar();

  Observable<Void> selectedThirdAvatar();

  Observable<Void> selectedFourthAvatar();

  Observable<Void> selectedFifthAvatar();

  Observable<Void> selectedSixthAvatar();

  void selectedAvatar(int avatar);

  void setActualAvatar(Integer avatar);
}
