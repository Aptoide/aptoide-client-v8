package cm.aptoide.pt.v8engine.view.account.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import cm.aptoide.pt.v8engine.view.account.UriToPathResolver;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class ManageUserPresenter implements Presenter {
  private final ManageUserView view;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;
  private final ThrowableToStringMapper errorMapper;
  private final ManageUserNavigator navigator;
  private final ManageUserFragment.ViewModel userData;
  private final boolean isEditProfile;
  private final UriToPathResolver uriToPathResolver;

  public ManageUserPresenter(ManageUserView view, CrashReport crashReport,
      AptoideAccountManager accountManager, ThrowableToStringMapper errorMapper,
      ManageUserNavigator navigator, ManageUserFragment.ViewModel userData, boolean isEditProfile,
      UriToPathResolver uriToPathResolver) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
    this.navigator = navigator;
    this.userData = userData;
    this.isEditProfile = isEditProfile;
    this.uriToPathResolver = uriToPathResolver;
  }

  @Override public void present() {
    handleSaveDataClick();
    handleCancelClick();
    onViewCreatedLoadUserData();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void onViewCreatedLoadUserData() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMapSingle(__ -> accountManager.accountStatus()
            .first()
            .toSingle())
        .map(userAccount -> {

          // in case of configuration changes, after an edition, this is prefered
          if (userData.hasData()) {
            return userData;
          }

          // if it is an edition and not after a configuration change event
          // after a configuration change this values could differ
          if (isEditProfile) {
            return new ManageUserFragment.ViewModel(userAccount.getNickname(),
                userAccount.getAvatar());
          }

          return null;
        })
        .filter(data -> data != null)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(data -> {
          view.loadImageStateless(data.getPictureUri());
          view.setUserName(data.getName());
        }, err -> crashReport.log(err));
  }

  private void navigateAway() {
    final boolean showPrivacyConfigs = Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable();
    if (isEditProfile) {
      navigator.goBack();
    } else if (showPrivacyConfigs) {
      navigator.toProfileStepOne();
    } else {
      navigator.goToHome();
    }
  }

  private void handleSaveDataClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveUserDataButtonClick()
            .doOnNext(__2 -> view.showProgressDialog())
            .flatMapCompletable(userData -> saveUserData(userData))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @NonNull private Completable saveUserData(ManageUserFragment.ViewModel userData) {
    return updateUserAccount(userData).observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.hideProgressDialog())
        .doOnCompleted(() -> sendAnalytics(userData))
        .doOnCompleted(() -> navigateAway())
        .onErrorResumeNext(err -> handleSaveUserDataError(err));
  }

  private void handleCancelClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelButtonClick()
            .doOnNext(__2 -> navigator.goBack()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Completable handleSaveUserDataError(Throwable throwable) {
    final String message = errorMapper.map(throwable);
    Completable errorHandler;
    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      errorHandler = view.showErrorMessage(message)
          .doOnCompleted(() -> navigateAway());
    } else {
      errorHandler = view.showErrorMessage(message);
    }

    return Completable.fromAction(() -> view.hideProgressDialog())
        .andThen(errorHandler);
  }

  private void sendAnalytics(ManageUserFragment.ViewModel userData) {
    Analytics.Account.createdUserProfile(!TextUtils.isEmpty(userData.getPictureUri()));
  }

  private Completable updateUserAccount(ManageUserFragment.ViewModel userData) {
    if (userData.hasNewPicture()) {
      final String mediaStoragePath =
          uriToPathResolver.getMediaStoragePath(Uri.parse(userData.getPictureUri()));
      return accountManager.syncCurrentAccount(userData.getName(), mediaStoragePath);
    }
    return accountManager.syncCurrentAccount(userData.getName());
  }
}
