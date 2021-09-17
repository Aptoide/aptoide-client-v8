package cm.aptoide.pt.view;

import androidx.annotation.VisibleForTesting;
import cm.aptoide.pt.AppCoinsManager;
import cm.aptoide.pt.app.view.AppCoinsInfoView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.socialmedia.SocialMediaAnalytics;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
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
  private final String appcWalletPackageName;
  private final Scheduler viewScheduler;
  private final SocialMediaAnalytics socialMediaAnalytics;
  private final AppCoinsManager appCoinsManager;

  public AppCoinsInfoPresenter(AppCoinsInfoView view, AppCoinsInfoNavigator appCoinsInfoNavigator,
      InstallManager installManager, CrashReport crashReport, String appcWalletPackageName,
      Scheduler viewScheduler, SocialMediaAnalytics socialMediaAnalytics,
      AppCoinsManager appCoinsManager) {
    this.view = view;
    this.appCoinsInfoNavigator = appCoinsInfoNavigator;
    this.installManager = installManager;
    this.crashReport = crashReport;
    this.appcWalletPackageName = appcWalletPackageName;
    this.viewScheduler = viewScheduler;
    this.socialMediaAnalytics = socialMediaAnalytics;
    this.appCoinsManager = appCoinsManager;
  }

  @Override public void present() {
    handleClickOnAppcWalletLink();
    handleClickOnCatappultDevButton();
    handleClickOnInstallButton();
    handleButtonText();
    handlePlaceHolderVisibilityChange();
    handleSocialMediaPromotionClick();
    handleBonusPercentage();
    handleOpenESkills();
  }

  private void handleOpenESkills() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.eSkillsClick())
        .doOnNext(socialMediaType -> appCoinsInfoNavigator.navigateToESkills())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void handleBonusPercentage() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> RxJavaInterop.toV1Single(appCoinsManager.getBonusAppc()))
        .observeOn(viewScheduler)
        .doOnNext(bonusAppcModel -> {
          if (bonusAppcModel.getHasBonusAppc()) {
            view.setBonusAppc(bonusAppcModel.getBonusPercentage());
          } else {
            view.setNoBonusAppcView();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  private void handleSocialMediaPromotionClick() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.socialMediaClick())
        .doOnNext(socialMediaType -> {
          appCoinsInfoNavigator.navigateToSocialMedia(socialMediaType);
          socialMediaAnalytics.sendPromoteSocialMediaClickEvent(socialMediaType);
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> crashReport.log(throwable));
  }

  @VisibleForTesting public void handlePlaceHolderVisibilityChange() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(created -> view.appItemVisibilityChanged())
        .doOnNext(scrollEvent -> {
          if (scrollEvent.getItemShown()) {
            view.removeBottomCardAnimation();
          } else if (!scrollEvent.getItemShown()) {
            view.addBottomCardAnimation();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  private void handleClickOnCatappultDevButton() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.catappultButtonClick())
        .observeOn(viewScheduler)
        .doOnNext(__ -> appCoinsInfoNavigator.navigateToCatappultWebsite())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleClickOnInstallButton() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(view.installButtonClick(), view.cardViewClick()))
        .flatMap(click -> installManager.isInstalled(appcWalletPackageName))
        .observeOn(viewScheduler)
        .doOnNext(isInstalled -> {
          if (isInstalled) {
            view.openApp(appcWalletPackageName);
          } else {
            appCoinsInfoNavigator.navigateToAppCoinsWallet();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleClickOnAppcWalletLink() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> view.appCoinsWalletLinkClick())
        .doOnNext(click -> appCoinsInfoNavigator.navigateToAppCoinsWallet())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }

  @VisibleForTesting public void handleButtonText() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> installManager.isInstalled(appcWalletPackageName))
        .doOnNext(view::setButtonText)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, crashReport::log);
  }
}
