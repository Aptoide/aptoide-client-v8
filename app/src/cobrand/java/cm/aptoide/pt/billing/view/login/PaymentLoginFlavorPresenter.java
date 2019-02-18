package cm.aptoide.pt.billing.view.login;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.orientation.ScreenOrientationManager;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import java.util.Collection;
import rx.Observable;
import rx.Scheduler;

public class PaymentLoginFlavorPresenter extends PaymentLoginPresenter {

  private final PaymentLoginView view;
  private final AccountAnalytics accountAnalytics;
  private final AptoideAccountManager accountManager;
  private final ThrowableToStringMapper errorMapper;
  private final CrashReport crashReport;
  private final ScreenOrientationManager orientationManager;
  private final Scheduler viewScheduler;
  private final int requestCode;
  private final AccountNavigator accountNavigator;

  public PaymentLoginFlavorPresenter(PaymentLoginView view, int requestCode,
      Collection<String> permissions, AccountNavigator accountNavigator,
      Collection<String> requiredPermissions, AptoideAccountManager accountManager,
      CrashReport crashReport, ThrowableToStringMapper errorMapper, Scheduler viewScheduler,
      ScreenOrientationManager orientationManager, AccountAnalytics accountAnalytics) {
    super(view, requestCode, permissions, accountNavigator, requiredPermissions, accountManager,
        crashReport, errorMapper, viewScheduler, orientationManager, accountAnalytics);
    this.view = view;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
    this.requestCode = requestCode;
    this.crashReport = crashReport;
    this.errorMapper = errorMapper;
    this.viewScheduler = viewScheduler;
    this.orientationManager = orientationManager;
    this.accountAnalytics = accountAnalytics;
  }

  @Override public void present() {

    super.present();

    handleCobrandText();
    showAptoideSignUpEvent();
    handleAptoideShowSignUpEvent();
    handleAptoideSignUpEvent();
    hideTCandPP();
    handleBackButtonAndUpNavigationEvent();
    hidePasswordContainerEvent();
  }

  private void handleAptoideSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.aptoideSignUpEvent()
            .doOnNext(__ -> {
              view.showLoading();
              orientationManager.lock();
              accountAnalytics.sendAptoideSignUpButtonPressed();
            })
            .flatMapCompletable(
                result -> accountManager.signUp(AptoideAccountManager.APTOIDE_SIGN_UP_TYPE, result)
                    .observeOn(viewScheduler)
                    .doOnTerminate(() -> {
                      view.hideLoading();
                      orientationManager.unlock();
                    })
                    .doOnError(throwable -> {
                      accountAnalytics.sendSignUpErrorEvent(AccountAnalytics.LoginMethod.APTOIDE,
                          throwable);
                      view.showError(errorMapper.map(throwable));
                      crashReport.log(throwable);
                    }))
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  public void hideTCandPP() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.hideTCandPP())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  private void handleAptoideShowSignUpEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> showAptoideSignUpEvent())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          view.hideLoading();
          view.showError(errorMapper.map(err));
          crashReport.log(err);
        });
  }

  private Observable<Boolean> showAptoideSignUpEvent() {
    return view.showAptoideSignUpAreaClick()
        .doOnNext(__ -> view.showUsernamePasswordContainer(false, false));
  }

  private void handleCobrandText() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .doOnNext(__ -> view.setCobrandText())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
        });
  }

  private void hidePasswordContainerEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.hidePasswordContainerEvent())
        .doOnNext(__ -> view.hideUsernamePasswordContainer(false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> {
          crashReport.log(err);
        });
  }

  private void handleBackButtonAndUpNavigationEvent() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.backButtonEvent(), view.upNavigationEvent()))
        .doOnNext(__ -> accountNavigator.popViewWithResult(requestCode, false))
        .doOnNext(__ -> view.hideUsernamePasswordContainer(false))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }
}
