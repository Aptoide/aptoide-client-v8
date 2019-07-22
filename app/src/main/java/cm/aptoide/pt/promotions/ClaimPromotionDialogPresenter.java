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

import static cm.aptoide.pt.promotions.ClaimPromotionDialogFragment.WALLET_PERMISSIONS_INTENT_REQUEST_CODE;

public class ClaimPromotionDialogPresenter implements Presenter {
  private static final String WALLET_ADDRESS = "WALLET_ADDRESS";
  private final String promotionId;
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
      ClaimPromotionsNavigator navigator, String promotionId) {
    this.view = view;
    this.subscriptions = subscriptions;
    this.viewScheduler = viewScheduler;
    this.claimPromotionsManager = claimPromotionsManager;
    this.promotionsAnalytics = promotionsAnalytics;
    this.navigator = navigator;
    this.promotionId = promotionId;
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
        .filter(result -> result.getRequestCode() == WALLET_PERMISSIONS_INTENT_REQUEST_CODE)
        .doOnNext(this::handleWalletPermissionsResult)
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          shouldSendIntent = false;
          view.sendWalletIntent();
        });
  }

  private void handleWalletPermissionsResult(Result result) {
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
        .flatMapSingle(
            wrapper -> claimPromotionsManager.claimPromotion(wrapper.getPackageName(), promotionId))
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
    } else if (errors.contains(ClaimStatusWrapper.Error.WRONG_CAPTCHA)) {
      return "captcha";
    } else if (errors.contains(ClaimStatusWrapper.Error.WALLET_NOT_VERIFIED)) {
      view.verifyWallet();
    } else {
      view.showGenericError();
    }
    return "error";
  }
}
