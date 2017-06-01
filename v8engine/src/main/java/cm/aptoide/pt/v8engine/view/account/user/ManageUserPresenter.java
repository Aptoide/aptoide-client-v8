package cm.aptoide.pt.v8engine.view.account.user;

import android.os.Bundle;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ManageUserPresenter implements Presenter {
  private final ManageUserView view;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;
  private final ThrowableToStringMapper errorMapper;

  public ManageUserPresenter(ManageUserView view, CrashReport crashReport,
      AptoideAccountManager accountManager, ThrowableToStringMapper errorMapper) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
  }

  @Override public void present() {

    Observable<Void> handleSaveDataClick = view.saveUserDataButtonClick()
        .doOnNext(__ -> view.showProgressDialog())
        .flatMap(userData -> saveUSerData(userData).observeOn(AndroidSchedulers.mainThread())
            .doOnCompleted(() -> sendAnalytics(userData.hasImage()))
            .doOnCompleted(() -> navigateAway(userData.isEditProfile()))
            .onErrorResumeNext(err -> handleSaveUserDataError(err, userData.isEditProfile()))
            .toObservable())
        .doOnNext(__ -> view.dismissProgressDialog())
        .map(__ -> null);

    Observable<Void> handleCancelClick = view.cancelButtonClick()
        .doOnNext(__ -> view.navigateBack());

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> Observable.merge(handleCancelClick, handleSaveDataClick))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Completable handleSaveUserDataError(Throwable throwable, boolean isEditProfile) {
    final String message = errorMapper.map(throwable);
    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      // navigate away
      return view.showErrorMessage(message)
          .doOnCompleted(() -> navigateAway(isEditProfile));
    }
    // show message but do not navigate
    return view.showErrorMessage(message);
  }

  private void sendAnalytics(boolean hasImage) {
    Analytics.Account.createdUserProfile(hasImage);
  }

  private void navigateAway(boolean isEditProfile) {
    final boolean showPrivacyConfigs = Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable();
    if (isEditProfile) {
      view.navigateBack();
    } else if (showPrivacyConfigs) {
      view.navigateToProfileStepOne();
    } else {
      view.navigateToHome();
    }
  }

  private Completable saveUSerData(ManageUserFragment.ViewModel userData) {
    return accountManager.updateAccount(userData.getName(), userData.getImage());
  }
}
