package cm.aptoide.pt.promotions;

import android.app.Activity;
import android.content.Intent;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Scheduler;
import rx.Single;
import rx.subscriptions.CompositeSubscription;

public class ClaimPromotionDialogPresenter implements Presenter {
  private static final int WALLET_VERIFICATION_RESULT_OK = 0;
  private static final int WALLET_VERIFICATION_RESULT_CANCELED = 1;
  private static final int WALLET_VERIFICATION_RESULT_FAILED = 2;
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
    handleWalletPermissionsResult();
    handleFindAddressClick();
    handleContinueClick();
    handleOnEditTextChanged();
    handleDismissGenericError();
    handleWalletCancelClick();
    handleDismissGenericMessage();
    handleWalletVerificationResult();
  }

  private void handleWalletVerificationResult() {
    view.getActivityResults()
        .filter(result -> result.getRequestCode()
            == ClaimPromotionDialogFragment.WALLET_VERIFICATION_INTENT_REQUEST_CODE)
        .map(Result::getResultCode)
        .doOnNext(this::handleWalletVerificationErrors)
        .filter(code -> code == WALLET_VERIFICATION_RESULT_OK)
        .doOnNext(__ -> view.showLoading())
        .flatMapSingle(__ -> claimPromotion())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, Throwable::printStackTrace);
  }

  private void handleWalletVerificationErrors(Integer result) {
    if (result == WALLET_VERIFICATION_RESULT_CANCELED) {
      view.showCanceledVerificationError();
    } else if (result.equals(WALLET_VERIFICATION_RESULT_FAILED)) {
      view.showGenericError();
    }
  }

  public void dispose() {
    subscriptions.clear();
  }

  private void handleOnResumeEvent() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.RESUME)
        .doOnNext(__ -> {
          if (!shouldSendIntent) {
            view.fetchWalletAddressByClipboard();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
        });
  }

  private void handleWalletPermissionsResult() {
    view.getActivityResults()
        .filter(result -> result.getRequestCode()
            == ClaimPromotionDialogFragment.WALLET_PERMISSIONS_INTENT_REQUEST_CODE)
        .doOnNext(result -> {
          if (result.getResultCode() == Activity.RESULT_OK) {
            Intent resultIntent = result.getData();
            if (resultIntent != null && resultIntent.getExtras() != null) {
              view.updateWalletText(resultIntent.getExtras()
                  .getString(WALLET_ADDRESS));
            } else {
              shouldSendIntent = false;
              view.sendWalletIntent();
            }
          } else if (result.getResultCode() != Activity.RESULT_CANCELED) {
            shouldSendIntent = false;
            view.sendWalletIntent();
          }
        })
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          shouldSendIntent = false;
          view.sendWalletIntent();
        });
  }

  private void handleFindAddressClick() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.CREATE)
        .observeOn(viewScheduler)
        .flatMap(__ -> view.getWalletClick()
            .doOnNext(packageName -> {
              promotionsAnalytics.sendClickOnWalletDialogFindWallet(packageName);
              view.fetchWalletAddressByIntent();
            })
            .doOnError(___ -> {
              shouldSendIntent = false;
              view.sendWalletIntent();
            })
            .retry())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
        });
  }

  private void handleContinueClick() {
    subscriptions.add(view.continueWalletClick()
        .doOnNext(wrapper -> {
          promotionsAnalytics.sendClickOnWalletDialogNext(wrapper.getPackageName());
          claimPromotionsManager.saveWalletAddress(wrapper.getWalletAddress());
          view.showLoading();
        })
        .flatMapSingle(__ -> claimPromotion())
        .subscribe(__ -> {
        }, throwable -> view.showGenericError()));
  }

  private Single<String> claimPromotion() {
    return claimPromotionsManager.claimPromotion()
        .observeOn(viewScheduler)
        .flatMap(response -> {
          if (response.getStatus()
              .equals(ClaimStatusWrapper.Status.OK)) {
            view.showClaimSuccess();
            return Single.just("success");
          } else {
            return Single.just(handleErrors(response.getErrors()));
          }
        });
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
          navigator.popDialogWithResult(packageName, Activity.RESULT_CANCELED);
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
    } else if (errors.contains(ClaimStatusWrapper.Error.WALLET_NOT_VERIFIED)) {
      view.verifyWallet();
    } else {
      view.showGenericError();
    }
    return "error";
  }
}
