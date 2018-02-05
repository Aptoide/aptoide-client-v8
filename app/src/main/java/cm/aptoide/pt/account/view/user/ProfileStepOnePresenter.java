package cm.aptoide.pt.account.view.user;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileStepOnePresenter implements Presenter {

  private static final String TAG = ProfileStepOnePresenter.class.getSimpleName();
  private final ProfileStepOneView view;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;
  private final AccountAnalytics accountAnalytics;

  public ProfileStepOnePresenter(ProfileStepOneView view, CrashReport crashReport,
      AptoideAccountManager accountManager, AccountNavigator accountNavigator,
      AccountAnalytics accountAnalytics) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.moreInfoButtonClick()
            .doOnNext(__1 -> accountAnalytics.accountProfileAction(1,
                AccountAnalytics.ProfileAction.MORE_INFO))
            .doOnNext(__1 -> accountNavigator.navigateToProfileStepTwoView()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));

    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.continueButtonClick()
            .doOnNext(__11 -> view.showWaitDialog())
            .flatMap(
                isExternalLogin -> makeUserProfilePublic().observeOn(AndroidSchedulers.mainThread())
                    .doOnCompleted(() -> accountAnalytics.accountProfileAction(1,
                        AccountAnalytics.ProfileAction.CONTINUE))
                    .doOnCompleted(() -> view.dismissWaitDialog())
                    .doOnCompleted(() -> {
                      if (isExternalLogin) {
                        accountNavigator.navigateToHomeView();
                      } else {
                        accountNavigator.navigateToCreateStoreView();
                      }
                    })
                    .toObservable())
            .doOnError(throwable -> Logger.e(TAG, throwable))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private Completable makeUserProfilePublic() {
    return accountManager.updateAccount(Account.Access.PUBLIC)
        .onErrorResumeNext(err -> {
          crashReport.log(err);
          return view.showGenericErrorMessage();
        });
  }
}
