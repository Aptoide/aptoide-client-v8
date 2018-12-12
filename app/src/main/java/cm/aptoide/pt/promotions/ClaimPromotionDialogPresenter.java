package cm.aptoide.pt.promotions;

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
    handleRefreshCaptcha();
    handleSubmitClick();
    handleOnEditTextChanged();
    handleDismissEvents();
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
        .flatMapSingle(__ -> claimPromotionsManager.getCaptcha())
        .observeOn(viewScheduler)
        .doOnNext(captcha -> {
          claimPromotionsManager.saveCaptchaUrl(captcha);
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
        .flatMapSingle(__ -> claimPromotionsManager.getCaptcha())
        .observeOn(viewScheduler)
        .doOnNext(captcha -> view.hideLoadingCaptcha(captcha))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleSubmitClick() {
    subscriptions.add(view.finishClick()
        .doOnNext(wrapper -> view.showLoading())
        .flatMapSingle(wrapper -> claimPromotionsManager.claimPromotion(wrapper.getPackageName(),
            wrapper.getCaptcha()))
        .observeOn(viewScheduler)
        .flatMapSingle(response -> {
          if (response.getStatus()
              .equals(ClaimStatusWrapper.Status.OK)) {
            view.showClaimSuccess();
            return Single.just("success");
          } else {
            return Single.just(handleErrors(response.getErrors()));
          }
        })
        .filter(error -> error.equals("captcha"))
        .flatMapSingle(__ -> claimPromotionsManager.getCaptcha())
        .observeOn(viewScheduler)
        .doOnNext(captcha -> view.showInvalidCaptcha(captcha))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleOnEditTextChanged() {
    subscriptions.add(view.editTextChanges()
        .doOnNext(change -> {
          view.handleEmptyEditText(change.editable());
        })
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleDismissEvents() {
    subscriptions.add(view.dismissClicks()
        .doOnNext(__ -> view.dismissDialog())
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private String handleErrors(List<ClaimStatusWrapper.Error> errors) {
    if (errors.contains(ClaimStatusWrapper.Error.PROMOTION_CLAIMED)) {
      view.showPromotionAlreadyClaimed();
    } else if (errors.contains(ClaimStatusWrapper.Error.WRONG_ADDRESS)) {
      view.showInvalidWalletAddress();
    } else if (errors.contains(ClaimStatusWrapper.Error.WRONG_CAPTCHA)) {
      return "captcha";
    } else {
      view.showGenericError();
    }
    return "error";
  }
}
