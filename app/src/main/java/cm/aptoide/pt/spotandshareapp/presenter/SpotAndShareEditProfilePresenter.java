package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserAvatarsProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareEditProfileView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 21-06-2017.
 */

public class SpotAndShareEditProfilePresenter implements Presenter {

  private SpotAndShareEditProfileView view;
  private final SpotAndShareUserManager spotAndShareUserManager;
  private final SpotAndShareUserAvatarsProvider avatarsProvider;

  public SpotAndShareEditProfilePresenter(SpotAndShareEditProfileView view,
      SpotAndShareUserManager spotAndShareUserManager,
      SpotAndShareUserAvatarsProvider spotAndShareUserAvatarsProvider) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.avatarsProvider = spotAndShareUserAvatarsProvider;
  }

  @Override public void present() {

    createLifeCycleSubscription(cancelChanges());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            created -> saveProfileChanges().compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .map(created -> avatarsProvider.getAvailableAvatars())
        .doOnNext(list -> view.setAvatarsList(list))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.onSelectedAvatar())
        .doOnNext(avatar -> view.selectAvatar(avatar))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> error.printStackTrace());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Subscription createLifeCycleSubscription(Observable<Void> voidObservable) {
    return view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> voidObservable.compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private Observable<Void> cancelChanges() {
    return view.cancelProfileChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.goBack());
  }

  private Observable<SpotAndShareUser> saveProfileChanges() {
    return view.saveProfileChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(user -> saveUser(user));
  }

  private int loadChosenAvatar() {
    return spotAndShareUserManager.getUser()
        .getAvatar()
        .getAvatarId();
  }

  private void saveUser(SpotAndShareUser user) {
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
