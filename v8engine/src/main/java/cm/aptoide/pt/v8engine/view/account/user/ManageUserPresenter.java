package cm.aptoide.pt.v8engine.view.account.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
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
  private final FragmentNavigator fragmentNavigator;
  private final ManageUserFragment.ViewModel userData;
  private final boolean isEditProfile;

  public ManageUserPresenter(ManageUserView view, CrashReport crashReport,
      AptoideAccountManager accountManager, ThrowableToStringMapper errorMapper,
      FragmentNavigator fragmentNavigator, ManageUserFragment.ViewModel userData,
      boolean isEditProfile) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
    this.fragmentNavigator = fragmentNavigator;
    this.userData = userData;
    this.isEditProfile = isEditProfile;
  }

  @Override public void present() {
    handleSaveDataClick();
    handleCancelClick();
    handleSelectImageClick();
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
          if (userData == null && isEditProfile) {
            return new ManageUserFragment.ViewModel(userAccount.getNickname(), null,
                userAccount.getAvatar());
          } else {
            return userData;
          }
        })
        .filter(data -> data != null)
        .subscribe(data -> {
          view.loadUserImage(data.getImagePathToView());
          view.loadUserName(data.getName());
        }, err -> crashReport.log(err));
  }

  private void handleSaveDataClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.saveUserDataButtonClick()
            .doOnNext(__2 -> view.showProgressDialog())
            .flatMap(userData -> saveUserData(userData))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @NonNull private Observable<Void> saveUserData(ManageUserFragment.ViewModel userData) {
    return updateUserAccount(userData).observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(() -> view.dismissProgressDialog())
        .doOnCompleted(() -> sendAnalytics(userData.hasImageToView()))
        .doOnCompleted(() -> navigateAway(isEditProfile))
        .onErrorResumeNext(err -> handleSaveUserDataError(err, isEditProfile))
        .toObservable();
  }

  private void handleCancelClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.cancelButtonClick()
            .doOnNext(__2 -> navigateBack()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleSelectImageClick() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> view.selectUserImageClick()
            .retry()
            .doOnNext(__2 -> view.showLoadImageDialog()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Completable handleSaveUserDataError(Throwable throwable, boolean isEditProfile) {
    final String message = errorMapper.map(throwable);
    Completable errorHandler;
    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      // navigate away
      errorHandler = view.showErrorMessage(message)
          .doOnCompleted(() -> navigateAway(isEditProfile));
    } else {
      // show message but do not navigate
      errorHandler = view.showErrorMessage(message);
    }

    return Completable.fromAction(() -> view.dismissProgressDialog())
        .andThen(errorHandler);
  }

  private void sendAnalytics(boolean hasImage) {
    Analytics.Account.createdUserProfile(hasImage);
  }

  private void navigateAway(boolean isEditProfile) {
    final boolean showPrivacyConfigs = Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable();
    if (isEditProfile) {
      navigateBack();
    } else if (showPrivacyConfigs) {
      navigateToProfileStepOne();
    } else {
      navigateToHome();
    }
  }

  private void navigateToProfileStepOne() {
    fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateTo(ProfileStepOneFragment.newInstance());
  }

  private void navigateToHome() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateBack() {
    fragmentNavigator.popBackStack();
  }

  private Completable updateUserAccount(ManageUserFragment.ViewModel userData) {
    if (userData.hasImageToUpload()) {
      return accountManager.updateAccount(userData.getName(), userData.getImagePathToUpload());
    }
    return accountManager.updateAccount(userData.getName());
  }
}
