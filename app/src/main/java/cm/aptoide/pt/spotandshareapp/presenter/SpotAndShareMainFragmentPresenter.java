package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {
  public static final int WRITE_SETTINGS_REQUEST_CODE_SEND = 3;
  public static final int WRITE_SETTINGS_REQUEST_CODE_RECEIVE = 4;
  public static final int WRITE_SETTINGS_REQUEST_CODE_SHARE_APTOIDE = 5;

  private SpotAndShareLocalUserManager spotAndShareUserManager;
  private final CrashReport crashReport;
  private SpotAndShareMainFragmentView view;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShareLocalUserManager spotAndShareUserManager, CrashReport crashReport) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.crashReport = crashReport;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> {
          if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            view.hideShareAptoideButton();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, error -> crashReport.log(error));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.startSend())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> {
          if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            view.showAutoEnableHotspotSendError();
          } else {
            view.openAppSelectionFragment(true);
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.startReceive())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openWaitingToReceiveFragment();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> view.editProfile())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openEditProfile();
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    loadProfileInformationOnView();

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.shareAptoideApk())
        .doOnNext(__ -> view.openShareAptoideFragment())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

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
        }, error -> crashReport.log(error));
  }
}
