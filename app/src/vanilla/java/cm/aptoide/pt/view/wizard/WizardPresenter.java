package cm.aptoide.pt.view.wizard;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.custom.AptoideViewPager;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class WizardPresenter implements Presenter, AptoideViewPager.OnPageChangeListener {

  private final WizardView view;
  private final AptoideAccountManager accountManager;
  private final CrashReport crashReport;

  public WizardPresenter(WizardView view, AptoideAccountManager accountManager,
      CrashReport crashReport) {
    this.view = view;
    this.accountManager = accountManager;
    this.crashReport = crashReport;
  }

  private Completable createViewsAndButtons() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .observeOn(AndroidSchedulers.mainThread())
        .flatMapCompletable(account -> view.createWizardAdapter(account));
  }

  private Observable<Void> setupHandlers() {
    Observable<Void> goToNextPageClick = view.goToNextPageClick()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.goToNextPage());

    Observable<Void> skipWizardClick = view.skipWizardClick()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.skipWizard());

    return Observable.merge(goToNextPageClick, skipWizardClick);
  }

  @Override public void present() {
    view.getLifecycle()
        .filter(event -> event == View.LifecycleEvent.CREATE)
        .flatMap(__ -> createViewsAndButtons().andThen(setupHandlers()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> crashReport.log(err));
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override public void onPageSelected(int position) {
    if (position == 2) {
      //Inside the wizards third page
      Analytics.Account.enterAccountScreen(Analytics.Account.AccountOrigins.WIZARD);
    }

    view.handleSelectedPage(position);

    if (position > 0 && position < view.getWizardButtonsCount() - 1) {
      view.showArrow();
    } else if (position == view.getWizardButtonsCount() - 1) {
      view.showSkipButton();
    }
  }

  @Override public void onPageScrollStateChanged(int state) {
  }
}
