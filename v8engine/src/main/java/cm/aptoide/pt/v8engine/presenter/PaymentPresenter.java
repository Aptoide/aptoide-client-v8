/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/08/2016.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.AptoidePay;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.view.PaymentView;
import cm.aptoide.pt.v8engine.view.View;
import java.util.List;
import javax.security.auth.login.LoginException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/19/16.
 */
public class PaymentPresenter implements Presenter {

  private static final String EXTRA_IS_PROCESSING_LOGIN =
      "cm.aptoide.pt.v8engine.payment.extra.IS_PROCESSING_LOGIN";

  private final PaymentView view;
  private final AptoidePay aptoidePay;
  private final AptoideProduct product;

  private boolean isProcessingLogin;

  public PaymentPresenter(PaymentView view, AptoidePay aptoidePay, AptoideProduct product) {
    this.view = view;
    this.aptoidePay = aptoidePay;
    this.product = product;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.RESUME.equals(event))
        .flatMap(resumed -> Observable.merge(login(), cancellationSelection()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();

    view.getLifecycle()
        .filter(event -> View.LifecycleEvent.CREATE.equals(event))
        .flatMap(created -> pay())
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void saveState(Bundle state) {
    state.putBoolean(EXTRA_IS_PROCESSING_LOGIN, isProcessingLogin);
  }

  @Override public void restoreState(Bundle state) {
    this.isProcessingLogin = state.getBoolean(EXTRA_IS_PROCESSING_LOGIN);
  }

  private Observable<Void> login() {
    return getProcessingLoginResult().onErrorResumeNext(
        AptoideAccountManager.login(view.getContext())
            .doOnSubscribe(() -> saveLoginState())
            .map(success -> true)).<Void>flatMap(loggedIn -> (loggedIn) ? Observable.just(null)
        : Observable.error(new LoginException("Not logged In. Payment can not be processed!")))
        .doOnNext(loggedIn -> clearLoginState())
        .doOnError(throwable -> clearLoginStateAndDismiss(throwable))
        .onErrorReturn(throwable -> null)
        .subscribeOn(Schedulers.computation());
  }

  private Observable<Purchase> pay() {
    return aptoidePay.getPurchase(product)
        .doOnSubscribe(() -> showProductAndShowLoading(product))
        .flatMap(purchase -> {
          if (purchase == null) {
            return aptoidePay.getProductPayments(view.getContext(), product)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(payments -> removeLoadingAndShowPayments(payments))
                .filter(payments -> !payments.isEmpty())
                .flatMap(payments -> paymentSelection());
          }
          return Observable.just(purchase);
        })
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> removeLoadingAndDismiss(throwable))
        .doOnNext(purchase -> removeLoadingAndDismiss(purchase))
        .onErrorReturn(throwable -> null)
        .subscribeOn(AndroidSchedulers.mainThread());
  }

  @NonNull private Observable<Purchase> paymentSelection() {
    return view.paymentSelection()
        .doOnNext(payment -> view.showLoading())
        .flatMap(payment -> aptoidePay.pay(payment))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(purchase -> view.removeLoading())
        .retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread())
            .doOnNext(throwable -> view.removeLoading())
            .<Throwable>flatMap(throwable -> {
              if (throwable instanceof PaymentCancellationException) {
                return Observable.just(throwable);
              }
              return Observable.error(throwable);
            }));
  }

  private void showProductAndShowLoading(AptoideProduct product) {
    view.showLoading();
    view.showProduct(product);
  }

  private void removeLoadingAndShowPayments(List<Payment> payments) {
    view.removeLoading();
    if (payments.isEmpty()) {
      view.showPaymentsNotFoundMessage();
    } else {
      view.showPayments(payments);
    }
  }

  private void clearLoginStateAndDismiss(Throwable throwable) {
    clearLoginState();
    view.dismiss(throwable);
  }

  private void removeLoadingAndDismiss(Throwable throwable) {
    view.removeLoading();
    view.dismiss(throwable);
  }

  private void removeLoadingAndDismiss(Purchase purchase) {
    view.removeLoading();
    view.dismiss(purchase);
  }

  private boolean clearLoginState() {
    return isProcessingLogin = false;
  }

  private boolean saveLoginState() {
    return isProcessingLogin = true;
  }

  private Observable<Boolean> getProcessingLoginResult() {
    return Observable.just(isProcessingLogin).flatMap(isProcessingLogin -> {
      if (isProcessingLogin) {
        return Observable.just(AptoideAccountManager.isLoggedIn());
      }
      return Observable.error(new IllegalStateException("No login currently being processed."));
    });
  }

  @NonNull private Observable<Void> cancellationSelection() {
    return view.cancellationSelection()
        .doOnNext(cancellation -> view.dismiss())
        .compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE));
  }
}
