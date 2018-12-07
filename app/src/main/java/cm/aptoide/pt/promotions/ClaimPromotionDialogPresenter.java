package cm.aptoide.pt.promotions;

import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.presenter.Presenter;
import java.util.List;
import rx.Scheduler;
import rx.Single;
import rx.subscriptions.CompositeSubscription;

public class ClaimPromotionDialogPresenter implements Presenter {

  private CompositeSubscription subscriptions;
  private Scheduler viewScheduler;
  private ClaimPromotionsManager claimPromotionsManager;
  private ClaimPromotionDialogView view;
  private IdsRepository idsRepository;

  public ClaimPromotionDialogPresenter(ClaimPromotionDialogView view,
      CompositeSubscription subscriptions, Scheduler viewScheduler,
      ClaimPromotionsManager claimPromotionsManager, IdsRepository idsRepository) {
    this.view = view;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.claimPromotionsManager = claimPromotionsManager;
    this.idsRepository = idsRepository;
  }

  @Override public void present() {
    handleFindAddressClick();
    handleContinueClick();
    handleRefreshCaptcha();
    handleSubmitClick();
  }

  public void dispose() {
    subscriptions.clear();
  }

  private void handleFindAddressClick() {
    subscriptions.add(view.getWalletClick()
        .doOnNext(__ -> view.sendWalletIntent())
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleContinueClick() {
    subscriptions.add(view.continueWalletClick()
        .doOnNext(address -> {
          claimPromotionsManager.saveWalletAddress(address);
          view.showLoading();
        })
        .map(__ -> idsRepository.getUniqueIdentifier())
        .flatMapSingle(uid -> claimPromotionsManager.getCaptcha(uid))
        .observeOn(viewScheduler)
        .doOnNext(captcha -> {
          view.showCaptchaView(captcha);
        })
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleRefreshCaptcha() {
    subscriptions.add(view.refreshCaptchaClick()
        .doOnNext(__ -> view.showLoadingCaptcha())
        .map(__ -> idsRepository.getUniqueIdentifier())
        .flatMapSingle(uid -> claimPromotionsManager.getCaptcha(uid))
        .observeOn(viewScheduler)
        .doOnNext(captcha -> view.hideLoadingCaptcha(captcha))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleSubmitClick() {
    subscriptions.add(view.finishClick()
        .doOnNext(submission -> view.showLoading())
        .flatMapSingle(
            submission -> claimPromotionsManager.claimPromotion(submission.getPackageName(),
                submission.getCaptcha()))
        .observeOn(viewScheduler)
        .flatMapSingle(response -> {
          if (response.getStatus()
              .equals(ClaimStatusWrapper.Status.ok)) {
            view.showClaimSuccess();
            return Single.just("success");
          } else {
            return Single.just(handleErrors(response.getErrors()));
          }
        })
        .filter(error -> error.equals("captcha"))
        .doOnNext(__ -> idsRepository.getUniqueIdentifier())
        .flatMapSingle(uid -> claimPromotionsManager.getCaptcha(uid))
        .observeOn(viewScheduler)
        .doOnNext(captcha -> view.showInvalidCaptcha(captcha))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private String handleErrors(List<ClaimStatusWrapper.Error> errors) {
    if (errors.contains(ClaimStatusWrapper.Error.promotionClaimed)) {
      view.showPromotionAlreadyClaimed();
    } else if (errors.contains(ClaimStatusWrapper.Error.wrongAddress)) {
      view.showInvalidWalletAddress();
    } else if (errors.contains(ClaimStatusWrapper.Error.wrongCaptcha)) {
      return "captcha";
    } else {
      view.showGenericError();
    }
    return "error";
  }
}
