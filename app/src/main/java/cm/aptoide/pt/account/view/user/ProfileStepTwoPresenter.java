package cm.aptoide.pt.account.view.user;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileStepTwoPresenter implements Presenter {

  private final ProfileStepTwoView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;
  private final AccountNavigator accountNavigator;

  public ProfileStepTwoPresenter(ProfileStepTwoView view, AptoideAccountManager accountManager,
      CrashReport crashReport, AccountNavigator accountNavigator) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
    this.accountNavigator = accountNavigator;
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

  private Completable makeAccountPublic() {
    return accountManager.updateAccount(Account.Access.PUBLIC)
        .onErrorResumeNext(err -> {
          crashReport.log(err);
          return view.showGenericErrorMessage();
        });
  }

  private Completable makeAccountPrivate() {
    return accountManager.updateAccount(Account.Access.UNLISTED)
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
      accountNavigator.navigateToHomeView();
    } else {
      accountNavigator.navigateToCreateStoreView();
    }
  }
}
