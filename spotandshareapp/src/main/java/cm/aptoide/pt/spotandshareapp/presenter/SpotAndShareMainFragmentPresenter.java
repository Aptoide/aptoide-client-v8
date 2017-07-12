package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {

  private final SpotAndShare spotAndShare;
  private SpotAndShareUserManager spotAndShareUserManager;
  private SpotAndShareMainFragmentView view;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShare spotAndShare, SpotAndShareUserManager spotAndShareUserManager) {
    this.view = view;
    this.spotAndShare = spotAndShare;
    this.spotAndShareUserManager = spotAndShareUserManager;
  }

  @Override public void present() {

    loadProfileInformationOnView();

    getSubscribe(startReceive());

    getSubscribe(startSend());

    getSubscribe(editProfile());
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Subscription getSubscribe(Observable<Void> voidObservable) {
    return view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> voidObservable.compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private Observable<Void> startSend() {
    return view.startSend()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          Log.i(getClass().getName(), "GOING TO START SENDING");
          view.openAppSelectionFragment(true);
        });
  }

  private Observable<Void> startReceive() {
    return view.startReceive()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          Log.i(getClass().getName(), "GOING TO START RECEIVING");
          view.openWaitingToReceiveFragment();
        });
  }

  private Observable<Void> editProfile() {
    return view.editProfile()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openEditProfile();
        });
  }

  private SpotAndShareUser getSpotAndShareProfileInformation() {
    return spotAndShareUserManager.getUser();
  }

  private void loadProfileInformationOnView() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.loadProfileInformation(getSpotAndShareProfileInformation()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }
}
