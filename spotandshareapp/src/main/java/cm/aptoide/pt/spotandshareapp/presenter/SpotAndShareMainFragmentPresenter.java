package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareapp.SpotAndSharePermissionProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.permission.PermissionProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {

  private SpotAndShareUserManager spotAndShareUserManager;
  private SpotAndSharePermissionProvider spotAndSharePermissionProvider;
  private SpotAndShareMainFragmentView view;
  private static final int SPOT_AND_SHARE_PERMISSIONS_REQUEST_CODE = 0;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShareUserManager spotAndShareUserManager,
      SpotAndSharePermissionProvider spotAndSharePermissionProvider) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.spotAndSharePermissionProvider = spotAndSharePermissionProvider;
  }

  @Override public void present() {

    loadProfileInformationOnView();

    getSubscribe(startReceive());

    getSubscribe(startSend());

    getSubscribe(editProfile());

    handleSendPermissionsResult();
    handleReceivePermissionsResult();
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
          spotAndSharePermissionProvider.requestNormalSpotAndSharePermissions(
              SPOT_AND_SHARE_PERMISSIONS_REQUEST_CODE);
        });
  }

  private Observable<Void> startReceive() {
    return view.startReceive()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          spotAndSharePermissionProvider.requestNormalSpotAndSharePermissions(
              SPOT_AND_SHARE_PERMISSIONS_REQUEST_CODE);
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

  private void handleSendPermissionsResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.normalPermissionResultSpotAndShare(
            SPOT_AND_SHARE_PERMISSIONS_REQUEST_CODE)
            .filter(permissions -> {
              for (PermissionProvider.Permission permission : permissions) {
                if (!permission.isGranted()) {
                  return false;
                }
              }
              return true;
            })
            .doOnNext(selection -> {
              Log.i(getClass().getName(), "GOING TO START SENDING");
              view.openAppSelectionFragment(true);
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleReceivePermissionsResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.normalPermissionResultSpotAndShare(
            SPOT_AND_SHARE_PERMISSIONS_REQUEST_CODE)
            .filter(permissions -> {
              for (PermissionProvider.Permission permission : permissions) {
                if (!permission.isGranted()) {
                  return false;
                }
              }
              return true;
            })
            .doOnNext(selection -> {
              Log.i(getClass().getName(), "GOING TO START RECEIVING");
              view.openWaitingToReceiveFragment();
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

}
