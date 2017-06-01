package cm.aptoide.pt.v8engine.view.account.user;

import android.os.Bundle;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileStepOnePresenter implements Presenter {

  private final ProfileStepOneView view;
  private final CrashReport crashReport;
  private final AptoideAccountManager accountManager;

  public ProfileStepOnePresenter(ProfileStepOneView view, CrashReport crashReport,
      AptoideAccountManager accountManager) {
    this.view = view;
    this.crashReport = crashReport;
    this.accountManager = accountManager;
  }

  @Override public void present() {

    Observable<Void> handleContinueClick = view.continueButtonClick()
        .doOnNext(__ -> view.showWaitDialog())
        .flatMap(
            isExternalLogin -> makeUserProfilePublic().observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> Analytics.Account.accountProfileAction(1,
                    Analytics.Account.ProfileAction.CONTINUE))
                .doOnCompleted(() -> view.dismissWaitDialog())
                .doOnCompleted(() -> {
                  if (isExternalLogin) {
                    view.navigateToHome();
                  } else {
                    view.navigateToCreateStore();
                  }
                })
                .toObservable())
        .retry()
        .map(__ -> null);

    Observable<Void> handleMoreInfoClick = view.moreInfoButtonClick()
        .doOnNext(__ -> Analytics.Account.accountProfileAction(1,
            Analytics.Account.ProfileAction.MORE_INFO))
        .doOnNext(__ -> view.navigateToProfileStepTwoView());

    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> Observable.merge(handleContinueClick, handleMoreInfoClick))
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

  private Completable makeUserProfilePublic() {
    return accountManager.updateAccount(Account.Access.PUBLIC)
        .onErrorResumeNext(err -> {
          crashReport.log(err);
          view.showGenericErrorMessage();
          return Completable.complete();
        });
  }
}
