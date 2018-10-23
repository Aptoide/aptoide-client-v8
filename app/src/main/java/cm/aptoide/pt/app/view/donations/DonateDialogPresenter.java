package cm.aptoide.pt.app.view.donations;

import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.presenter.Presenter;
import rx.Scheduler;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subscriptions.CompositeSubscription;

public class DonateDialogPresenter implements Presenter {

  private CompositeSubscription subscriptions;
  private Scheduler viewScheduler;
  private DonateDialogView view;
  private DonationsService service;
  private AppNavigator appNavigator;

  public DonateDialogPresenter(DonateDialogView view, DonationsService service,
      CompositeSubscription subscriptions, Scheduler viewScheduler, AppNavigator appNavigator) {
    this.view = view;
    this.service = service;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.appNavigator = appNavigator;
  }

  @Override public void present() {
    handleDonateClick();
    handleNoWalletContinueClick();
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
            .doOnNext(address -> view.sendWalletIntent(result.getValue(), address,
                result.getPackageName(), result.getNickname())))
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }

  private void handleNoWalletContinueClick() {
    subscriptions.add(view.noWalletContinueClick()
        .doOnNext(__ -> {
          appNavigator.navigateWithPackageName("com.appcoins.wallet",
              NewAppViewFragment.OpenType.OPEN_ONLY);
          view.dismissDialog();
        })
        .subscribe(lifecycleEvent -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        }));
  }
}
