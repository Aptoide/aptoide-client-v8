package cm.aptoide.pt.account.view.user;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.UriToPathResolver;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ThrowableToStringMapper;
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
  private final boolean showPrivacyConfigs;

  public ManageUserPresenter(ManageUserView view, CrashReport crashReport,
      AptoideAccountManager accountManager, ThrowableToStringMapper errorMapper,
      ManageUserNavigator navigator, ManageUserFragment.ViewModel userData, boolean isEditProfile,
      UriToPathResolver uriToPathResolver, boolean showPrivacyConfigs) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
    this.navigator = navigator;
    this.userData = userData;
    this.isEditProfile = isEditProfile;
    this.uriToPathResolver = uriToPathResolver;
    this.showPrivacyConfigs = showPrivacyConfigs;
  }

  @Override public void present() {
    handleSaveDataClick();
    handleCancelClick();
    onViewCreatedLoadUserData();
  }

  private void onViewCreatedLoadUserData() {
    view.getLifecycleEvents()
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
    if (isEditProfile) {
      navigator.goBack();
    } else if (showPrivacyConfigs) {
      navigator.toProfileStepOne();
    } else {
      navigator.goToHome();
    }
  }

  private void handleSaveDataClick() {
    view.getLifecycleEvents()
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
    view.getLifecycleEvents()
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
      return accountManager.updateAccount(userData.getName(), mediaStoragePath);
    }
    return accountManager.updateAccount(userData.getName());
  }
}
