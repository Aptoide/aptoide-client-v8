package cm.aptoide.pt.promotions;

import android.app.Activity;
import android.content.Intent;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Scheduler;
import rx.Single;
import rx.subscriptions.CompositeSubscription;

public class ClaimPromotionDialogPresenter implements Presenter {
  private static final String WALLET_ADDRESS = "WALLET_ADDRESS";

  private CompositeSubscription subscriptions;
  private Scheduler viewScheduler;
  private ClaimPromotionsManager claimPromotionsManager;
  private ClaimPromotionDialogView view;
  private PromotionsAnalytics promotionsAnalytics;
  private ClaimPromotionsNavigator navigator;

  private boolean shouldSendIntent;

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
    this.shouldSendIntent = true;
  }

  @Override public void present() {
    handleOnResumeEvent();
    handleOnActivityResult();
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

  private void handleOnResumeEvent() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.RESUME)
        .doOnNext(__ -> {
          if (shouldSendIntent) {
            view.fetchWalletAddressByIntent();
            shouldSendIntent = false;
          } else {
            view.fetchWalletAddressByClipboard();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.fetchWalletAddressByClipboard();
        });
  }

  private void handleOnActivityResult() {
    view.getActivityResults()
        .doOnNext(result -> {
          if (result.getRequestCode() != 123) return;
          if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultIntent = result.getData();
            if (resultIntent != null && resultIntent.getExtras() != null) {
              view.updateWalletText(resultIntent.getExtras()
                  .getString(WALLET_ADDRESS));
            } else {
              view.fetchWalletAddressByClipboard();
            }
          } else {
            view.fetchWalletAddressByClipboard();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          view.showGenericError();
        });
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
        .doOnNext(packageName -> {
          promotionsAnalytics.sendRefreshCaptchaEvent(packageName);
          view.showLoadingCaptcha();
        })
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
