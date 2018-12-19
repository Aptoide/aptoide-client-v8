package cm.aptoide.pt.promotions;

import android.app.Activity;
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
  private PromotionsAnalytics promotionsAnalytics;
  private ClaimPromotionsNavigator navigator;

  public ClaimPromotionDialogPresenter(ClaimPromotionDialogView view,
      CompositeSubscription subscriptions, Scheduler viewScheduler,
      ClaimPromotionsManager claimPromotionsManager, PromotionsAnalytics promotionsAnalytics,
      ClaimPromotionsNavigator navigator) {
    this.view = view;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.claimPromotionsManager = claimPromotionsManager;
    this.promotionsAnalytics = promotionsAnalytics;
    this.navigator = navigator;
  }

  @Override public void present() {
    handleFindAddressClick();
    handleContinueClick();
    handleRefreshCaptcha();
    handleSubmitClick();
    handleOnEditTextChanged();
    handleDismissGenericError();
    handleWalletCancelClick();
    handleCaptchaCancelClick();
    handleDismissGenericMessage();
  }

  public void dispose() {
    subscriptions.clear();
  }

  private void handleFindAddressClick() {
    subscriptions.add(view.getWalletClick()
        .doOnNext(packageName -> {
          promotionsAnalytics.sendClickOnWalletDialogFindWallet(packageName);
          view.sendWalletIntent();
        })
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleContinueClick() {
    subscriptions.add(view.continueWalletClick()
        .doOnNext(wrapper -> {
          promotionsAnalytics.sendClickOnWalletDialogNext(wrapper.getPackageName());
          claimPromotionsManager.saveWalletAddress(wrapper.getWalletAddress());
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
        .doOnNext(wrapper -> {
          promotionsAnalytics.sendClickOnCaptchaDialogClaim(wrapper.getPackageName());
          view.showLoading();
        })
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

  private void handleDismissGenericError() {
    subscriptions.add(view.dismissGenericErrorClick()
        .doOnNext(__ -> view.dismissDialog())
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleWalletCancelClick() {
    subscriptions.add(view.walletCancelClick()
        .doOnNext(packageName -> {
          promotionsAnalytics.sendClickOnWalletDialogCancel(packageName);
          view.dismissDialog();
        })
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleCaptchaCancelClick() {
    subscriptions.add(view.captchaCancelClick()
        .doOnNext(packageName -> {
          promotionsAnalytics.sendClickOnCaptchaDialogCancel(packageName);
          view.dismissDialog();
        })
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        }));
  }

  private void handleDismissGenericMessage() {
    subscriptions.add(view.dismissGenericMessage()
        .doOnNext(message -> {
          navigator.popDialogWithResult(message.getPackageName(),
              message.isOk() ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
          view.dismissDialog();
        })
        .subscribe(__ -> {
        }, throwable -> view.showGenericError()));
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
