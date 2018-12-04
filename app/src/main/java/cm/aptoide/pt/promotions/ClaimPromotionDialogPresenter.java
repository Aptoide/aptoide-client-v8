package cm.aptoide.pt.promotions;

import cm.aptoide.pt.presenter.Presenter;
import rx.Scheduler;
import rx.subscriptions.CompositeSubscription;

public class ClaimPromotionDialogPresenter implements Presenter {

  private CompositeSubscription subscriptions;
  private Scheduler viewScheduler;
  private ClaimPromotionsManager claimPromotionsManager;
  private ClaimPromotionDialogView view;

  public ClaimPromotionDialogPresenter(ClaimPromotionDialogView view,
      CompositeSubscription subscriptions, Scheduler viewScheduler,
      ClaimPromotionsManager claimPromotionsManager) {
    this.view = view;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.claimPromotionsManager = claimPromotionsManager;
  }

  @Override public void present() {
    handleFindAddressClick();
    handleContinueClick();
  }

  public void dispose() {
    subscriptions.clear();
  }

  private void handleFindAddressClick() {
    subscriptions.add(view.getWalletClick()
        .doOnNext(__ -> view.sendWalletIntent())
        .subscribe(__ -> {
        }, throwable -> {
        }));
  }

  private void handleContinueClick() {
    subscriptions.add(view.continueClick()
        .doOnNext(address -> {
          claimPromotionsManager.saveWalletAddres(address);
          view.showCaptcha();
        })
        .subscribe(__ -> {
        }, throwable -> {
        }));
  }
}
