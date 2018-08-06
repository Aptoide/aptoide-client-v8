package cm.aptoide.pt.view;

import android.support.annotation.VisibleForTesting;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppCoinsInfoView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Completable;
import rx.Observable;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoPresenter implements Presenter {

  private final AppCoinsInfoView view;
  private final AppCoinsInfoNavigator appCoinsInfoNavigator;
  private final AppCoinsInfoManager appCoinsInfoManager;
  private final CrashReport crashReport;

  public AppCoinsInfoPresenter(AppCoinsInfoView view, AppCoinsInfoNavigator appCoinsInfoNavigator,
      AppCoinsInfoManager appCoinsInfoManager, CrashReport crashReport) {
    this.view = view;
    this.appCoinsInfoNavigator = appCoinsInfoNavigator;
    this.appCoinsInfoManager = appCoinsInfoManager;
    this.crashReport = crashReport;
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
        .flatMap(click -> appCoinsInfoManager.loadButtonState())
        .flatMapCompletable(isInstalled -> {
          if (isInstalled) {
            return openInstalledApp(AppCoinsInfoFragment.APPCWALLETPACKAGENAME);
          } else {
            appCoinsInfoNavigator.navigateToAppCoinsBDSWallet();
            return Completable.complete();
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
        .doOnNext(click -> appCoinsInfoNavigator.navigateToAppCoinsBDSWallet())
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
        .flatMap(__ -> appCoinsInfoManager.loadButtonState())
        .doOnNext(view::setButtonText)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private Completable openInstalledApp(String packageName) {
    return Completable.fromAction(() -> view.openApp(packageName));
  }
}
