package cm.aptoide.pt.view;

import cm.aptoide.pt.app.view.AppCoinsInfoView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoPresenter implements Presenter {

  private final AppCoinsInfoView view;
  private final AppCoinsInfoNavigator appCoinsInfoNavigator;
  private final CrashReport crashReport;

  public AppCoinsInfoPresenter(AppCoinsInfoView view, AppCoinsInfoNavigator appCoinsInfoNavigator,
      CrashReport crashReport) {
    this.view = view;
    this.appCoinsInfoNavigator = appCoinsInfoNavigator;
    this.crashReport = crashReport;
  }

  @Override public void present() {
    handleClickOnCoinbaseLink();
    handleClickOnInstallButton();
  }

  private void handleClickOnInstallButton() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.installButtonClick(), view.appCoinsWalletClick(),
            view.cardViewClick()))
        .doOnNext(click -> appCoinsInfoNavigator.navigateToAppCoinsBDSWallet())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  private void handleClickOnCoinbaseLink() {
    view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.coinbaseLinkClick())
        .doOnNext(click -> appCoinsInfoNavigator.navigateToCoinbaseLink())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }
}
