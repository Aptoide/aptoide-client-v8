package cm.aptoide.pt.v8engine.view.account.user;

import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileStepTwoPresenter implements Presenter {

  private final ProfileStepTwoView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final FragmentNavigator fragmentNavigator;

  public ProfileStepTwoPresenter(ProfileStepTwoView view, AptoideAccountManager accountManager,
      CrashReport crashReport, FragmentNavigator fragmentNavigator) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.fragmentNavigator = fragmentNavigator;
  }

  @Override public void present() {
    Observable<Void> handleContinueClick = view.continueButtonClick()
        .doOnNext(__ -> view.showWaitDialog())
        .flatMapCompletable(
            externalLogin -> makeAccountPublic().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> sendAnalytics(Analytics.Account.ProfileAction.CONTINUE))
                .doOnCompleted(() -> view.dismissWaitDialog())
                .doOnCompleted(() -> navigate(externalLogin)))
        .retry()
        .map(__ -> null);

    Observable<Void> handlePrivateProfileClick = view.makePrivateProfileButtonClick()
        .doOnNext(__ -> view.showWaitDialog())
        .flatMapCompletable(
            externalLogin -> makeAccountPrivate().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> sendAnalytics(Analytics.Account.ProfileAction.PRIVATE_PROFILE))
                .doOnCompleted(() -> view.dismissWaitDialog())
                .doOnCompleted(() -> navigate(externalLogin)))
        .retry()
        .map(__ -> null);

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> Observable.merge(handleContinueClick, handlePrivateProfileClick))
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

  private Completable makeAccountPublic() {
    return accountManager.syncCurrentAccount(Account.Access.PUBLIC)
        .onErrorResumeNext(err -> {
          crashReport.log(err);
          return view.showGenericErrorMessage();
        });
  }

  private Completable makeAccountPrivate() {
    return accountManager.syncCurrentAccount(Account.Access.UNLISTED)
        .onErrorResumeNext(err -> {
          crashReport.log(err);
          return view.showGenericErrorMessage();
        });
  }

  private Completable sendAnalytics(Analytics.Account.ProfileAction action) {
    return Completable.fromAction(() -> Analytics.Account.accountProfileAction(2, action));
  }

  private void navigate(boolean externalLogin) {
    if (externalLogin) {
      fragmentNavigator.navigateToHomeCleaningBackStack();
    } else {
      fragmentNavigator.cleanBackStack();
      fragmentNavigator.navigateTo(
          ManageStoreFragment.newInstance(new ManageStoreFragment.ViewModel(), true));
    }
  }
}
