package cm.aptoide.pt.v8engine.view.account.user;

import android.os.Bundle;
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

  public ManageUserPresenter(ManageUserView view, CrashReport crashReport,
      AptoideAccountManager accountManager, ThrowableToStringMapper errorMapper,
      FragmentNavigator fragmentNavigator) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.errorMapper = errorMapper;
    this.fragmentNavigator = fragmentNavigator;
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
        .retry()
        .map(__ -> null);

    Observable<Void> handleCancelClick = view.cancelButtonClick()
        .doOnNext(__ -> navigateBack());

    Observable<Void> handleSelectImageClick = view.selectUserImageClick()
        .retry()
        .doOnNext(__ -> view.showLoadImageDialog());

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(
            __ -> Observable.merge(handleCancelClick, handleSaveDataClick, handleSelectImageClick))
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
      navigateBack();
    } else if (showPrivacyConfigs) {
      navigateToProfileStepOne();
    } else {
      navigateToHome();
    }
  }

  private void navigateToProfileStepOne() {
    //fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateToWithoutBackSave(ProfileStepOneFragment.newInstance());
  }

  private void navigateToHome() {
    fragmentNavigator.navigateToHomeCleaningBackStack();
  }

  private void navigateBack() {
    fragmentNavigator.popBackStack();
  }

  private Completable saveUSerData(ManageUserFragment.ViewModel userData) {
    return accountManager.updateAccount(userData.getName(), userData.getImage());
  }
}
