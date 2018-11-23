package cm.aptoide.pt.app.view.donations;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.presenter.Presenter;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

public class DonateDialogPresenter implements Presenter {

  private CompositeSubscription subscriptions;
  private Scheduler viewScheduler;
  private DonateDialogView view;
  private DonationsService service;
  private AppNavigator appNavigator;
  private DonationsAnalytics donationsAnalytics;

  public DonateDialogPresenter(DonateDialogView view, DonationsService service,
      CompositeSubscription subscriptions, Scheduler viewScheduler, AppNavigator appNavigator,
      DonationsAnalytics donationsAnalytics) {
    this.view = view;
    this.service = service;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.appNavigator = appNavigator;
    this.donationsAnalytics = donationsAnalytics;
  }

  @Override public void present() {
    handleDonateClick();
    handleNoWalletContinueClick();
    handleCancelClick();
  }

  public void dispose() {
    subscriptions.clear();
  }

  private void handleDonateClick() {

    subscriptions.add(view.donateClick()
        .doOnNext(result -> view.showLoading())
        .flatMap(result -> service.getWalletAddress(result.getPackageName())
            .toObservable()
            .observeOn(viewScheduler)
            .doOnNext(address -> {
              donationsAnalytics.sendDonateInteractEvent(result.getPackageName(), result.getValue(),
                  !result.getNickname()
                      .isEmpty());
              view.sendWalletIntent(result.getValue(), address, result.getPackageName(),
                  result.getNickname());
            }))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          view.showErrorMessage();
        }));
  }

  private void handleCancelClick() {
    subscriptions.add(view.cancelClick()
        .doOnNext(result -> {
          donationsAnalytics.sendCancelInteractEvent(result.getPackageName(), result.getValue(),
              !result.getNickname()
                  .isEmpty());
          view.dismissDialog();
        })
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          view.showErrorMessage();
        }));
  }

  private void handleNoWalletContinueClick() {
    subscriptions.add(view.noWalletContinueClick()
        .doOnNext(__ -> {
          appNavigator.navigateWithPackageName("com.appcoins.wallet",
              AppViewFragment.OpenType.OPEN_ONLY);
          view.dismissDialog();
        })
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          view.showErrorMessage();
        }));
  }
}
