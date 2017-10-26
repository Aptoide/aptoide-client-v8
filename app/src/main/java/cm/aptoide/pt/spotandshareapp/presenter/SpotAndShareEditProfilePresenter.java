package cm.aptoide.pt.spotandshareapp.presenter;

import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalAvatarsProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareEditProfileView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 21-06-2017.
 */

public class SpotAndShareEditProfilePresenter implements Presenter {

  private SpotAndShareEditProfileView view;
  private final SpotAndShareLocalUserManager spotAndShareUserManager;
  private final SpotAndShareLocalAvatarsProvider avatarsProvider;
  private final CrashReport crashReport;

  public SpotAndShareEditProfilePresenter(SpotAndShareEditProfileView view,
      SpotAndShareLocalUserManager spotAndShareUserManager,
      SpotAndShareLocalAvatarsProvider spotAndShareUserAvatarsProvider, CrashReport crashReport) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.avatarsProvider = spotAndShareUserAvatarsProvider;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            created -> saveProfileChanges().compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> crashReport.log(error));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(created -> avatarsProvider.getAvailableAvatars())
        .doOnNext(list -> view.setAvatarsList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> crashReport.log(error));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.onSelectedAvatar())
        .doOnNext(avatar -> view.selectAvatar(avatar))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> crashReport.log(error));
  }

  private Subscription createLifeCycleSubscription(Observable<Void> voidObservable) {
    return view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> voidObservable.compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private Observable<SpotAndShareLocalUser> saveProfileChanges() {
    return view.saveProfileChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(user -> saveUser(user));
  }

  private int loadChosenAvatar() {
    return spotAndShareUserManager.getUser()
        .getAvatar()
        .getAvatarId();
  }

  private void saveUser(SpotAndShareLocalUser user) {
    Log.d("saving user", user.getUsername() + " -  " + user.getAvatar()
        .getAvatarId());
    if (spotAndShareUserManager.getUser() == null) {
      spotAndShareUserManager.createUser(user);
    } else if (spotAndShareUserManager.getUser() != user) {
      spotAndShareUserManager.updateUser(user);
    }
    view.goBack();
  }
}
