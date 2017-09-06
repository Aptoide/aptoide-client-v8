package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUserManager;
import cm.aptoide.pt.spotandshareapp.SpotAndSharePermissionProvider;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import cm.aptoide.pt.view.permission.PermissionProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {
  public static final int EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND = 0;
  public static final int EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE = 1;
  public static final int WRITE_SETTINGS_REQUEST_CODE_SEND = 2;
  public static final int WRITE_SETTINGS_REQUEST_CODE_RECEIVE = 3;

  private SpotAndShareLocalUserManager spotAndShareUserManager;
  private SpotAndSharePermissionProvider spotAndSharePermissionProvider;
  private SpotAndShareMainFragmentView view;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShareLocalUserManager spotAndShareUserManager,
      SpotAndSharePermissionProvider spotAndSharePermissionProvider) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.spotAndSharePermissionProvider = spotAndSharePermissionProvider;
  }

  @Override public void present() {

    loadProfileInformationOnView();

    subscribe(clickedReceive());

    subscribe(clickedSend());

    subscribe(editProfile());

    handleLocationAndExternalStoragePermissionsResult();

    handleWriteSettingsPermissionResult();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Subscription subscribe(Observable<Void> voidObservable) {
    return view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> voidObservable.compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private Observable<Void> clickedSend() {
    return view.startSend()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spotAndSharePermissionProvider.requestLocationAndExternalStorageSpotAndSharePermissions(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND);
          } else {
            Log.i(getClass().getName(), "GOING TO START SENDING");
            view.openAppSelectionFragment(true);
          }
        });
  }

  private Observable<Void> clickedReceive() {
    return view.startReceive()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spotAndSharePermissionProvider.requestLocationAndExternalStorageSpotAndSharePermissions(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE);
          } else {
            Log.i(getClass().getName(), "GOING TO START RECEIVING");
            view.openWaitingToReceiveFragment();
          }
        });
  }

  private Observable<Void> editProfile() {
    return view.editProfile()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openEditProfile();
        });
  }

  private SpotAndShareLocalUser getSpotAndShareProfileInformation() {
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

  private void handleLocationAndExternalStoragePermissionsResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            __ -> spotAndSharePermissionProvider.locationAndExternalStoragePermissionsResultSpotAndShare(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND)

                .filter(permissions -> {
                  for (PermissionProvider.Permission permission : permissions) {
                    if (!permission.isGranted()) {
                      return false;
                    }
                  }
                  return true;
                })
                .doOnNext(permissions -> {
                  spotAndSharePermissionProvider.requestWriteSettingsPermission(
                      WRITE_SETTINGS_REQUEST_CODE_SEND);
                }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(
            __ -> spotAndSharePermissionProvider.locationAndExternalStoragePermissionsResultSpotAndShare(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE)
                .filter(permissions -> {
                  for (PermissionProvider.Permission permission : permissions) {
                    if (!permission.isGranted()) {
                      return false;
                    }
                  }
                  return true;
                })
                .doOnNext(permissions -> {
                  spotAndSharePermissionProvider.requestWriteSettingsPermission(
                      WRITE_SETTINGS_REQUEST_CODE_RECEIVE);
                }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void handleWriteSettingsPermissionResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.writeSettingsPermissionResult())
        .filter(requestCode -> requestCode == WRITE_SETTINGS_REQUEST_CODE_SEND)
        .doOnNext(requestCode -> {
          Log.i(getClass().getName(), "GOING TO START SENDING");
          view.openAppSelectionFragment(true);
        })
        .subscribe(created -> {
        }, error -> error.printStackTrace());

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.writeSettingsPermissionResult())
        .filter(requestCode -> requestCode == WRITE_SETTINGS_REQUEST_CODE_RECEIVE)
        .doOnNext(requestCode -> {
          Log.i(getClass().getName(), "GOING TO START RECEIVING");
          view.openWaitingToReceiveFragment();
        })
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }
}
