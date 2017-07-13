package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
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
  private SpotAndShareUserManager spotAndShareUserManager;

  public SpotAndShareEditProfilePresenter(SpotAndShareEditProfileView view,
      SpotAndShareUserManager spotAndShareUserManager) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
  }

  @Override public void present() {

    createLifeCycleSubscription(cancelChanges());
    createLifeCycleSubscription(chosenFirstAvatar());
    createLifeCycleSubscription(chosenSecondAvatar());
    createLifeCycleSubscription(chosenThirdAvatar());
    createLifeCycleSubscription(chosenFourthAvatar());
    createLifeCycleSubscription(chosenFifthAvatar());
    createLifeCycleSubscription(chosenSixthAvatar());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(
            created -> saveProfileChanges().compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .map(resumed -> loadChosenAvatar())
        .doOnNext(avatar -> view.setActualAvatar(avatar))
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

  private Observable<Void> chosenFirstAvatar() {
    return view.selectedFirstAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(0));
  }

  private Observable<Void> chosenSecondAvatar() {
    return view.selectedSecondAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(1));
  }

  private Observable<Void> chosenThirdAvatar() {
    return view.selectedThirdAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(2));
  }

  private Observable<Void> chosenFourthAvatar() {
    return view.selectedFourthAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(3));
  }

  private Observable<Void> chosenFifthAvatar() {
    return view.selectedFifthAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(4));
  }

  private Observable<Void> chosenSixthAvatar() {
    return view.selectedSixthAvatar()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(cancel -> view.selectedAvatar(5));
  }

  private Observable<SpotAndShareUser> saveProfileChanges() {
    return view.saveProfileChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(user -> saveUser(user));
  }

  private int loadChosenAvatar() {
    return spotAndShareUserManager.getUser()
        .getAvatar()
        .getResourceID();
  }

  private void saveUser(SpotAndShareUser user) {
    Log.d("saving user", user.getUsername() + " -  " + user.getAvatar()
        .getResourceID());
    if (spotAndShareUserManager.getUser() == null) {
      spotAndShareUserManager.createUser(user);
    } else if (spotAndShareUserManager.getUser() != user) {
      spotAndShareUserManager.updateUser(user);
    }
    view.goBack();
  }
}
