package cm.aptoide.pt.view;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppCoinsInfoView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoPresenter implements Presenter {

  private final AppCoinsInfoView view;
  private final AppCoinsInfoNavigator appCoinsInfoNavigator;
  private final InstallManager installManager;
  private final CrashReport crashReport;
  private final Scheduler viewScheduler;

  public AppCoinsInfoPresenter(AppCoinsInfoView view, AppCoinsInfoNavigator appCoinsInfoNavigator,
      InstallManager installManager, CrashReport crashReport, Scheduler viewScheduler) {
    this.view = view;
    this.appCoinsInfoNavigator = appCoinsInfoNavigator;
    this.installManager = installManager;
    this.crashReport = crashReport;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    handleClickOnCoinbaseLink();
    handleClickOnAppcWalletLink();
    handleClickOnInstallButton();
    handleButtonText();
  }

  @VisibleForTesting public void handleClickOnInstallButton() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.installButtonClick(), view.cardViewClick()))
        .flatMap(click -> installManager.isInstalled(AppCoinsInfoFragment.APPC_WALLET_PACKAGE_NAME))
        .observeOn(viewScheduler)
        .doOnNext(isInstalled -> {
          if (isInstalled) {
            view.openApp(AppCoinsInfoFragment.APPC_WALLET_PACKAGE_NAME);
          } else {
            appCoinsInfoNavigator.navigateToAppCoinsWallet();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleClickOnAppcWalletLink() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.appCoinsWalletLinkClick())
        .doOnNext(click -> appCoinsInfoNavigator.navigateToAppCoinsWallet())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleClickOnCoinbaseLink() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.coinbaseLinkClick())
        .doOnNext(click -> appCoinsInfoNavigator.navigateToCoinbaseLink())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleButtonText() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> installManager.isInstalled(AppCoinsInfoFragment.APPC_WALLET_PACKAGE_NAME))
        .doOnNext(view::setButtonText)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }
}
