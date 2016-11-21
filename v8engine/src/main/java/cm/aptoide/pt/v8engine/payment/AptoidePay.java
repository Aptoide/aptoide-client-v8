/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.payment.exception.PaymentAlreadyProcessedException;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

import static rx.Observable.error;

/**
 * Created by marcelobenites on 8/12/16.
 */
@AllArgsConstructor public class AptoidePay {

  private final AptoidePreferencesConfiguration configuration;
  private final AccountManager accountManager;
  private final PaymentRepository paymentRepository;

  public Observable<List<Payment>> getProductPayments(Context context, AptoideProduct product) {
    return paymentRepository.getPayments(context, product);
  }

  public Observable<Purchase> getPurchase(AptoideProduct product) {
    return paymentRepository.getPaymentConfirmation(product.getId())
        .first(paymentConfirmation -> paymentConfirmation.isCompleted())
        .flatMap(paymentConfirmation -> paymentRepository.getPurchase(product))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(null);
          }
          return Observable.error(throwable);
        });
  }

  public Observable<Purchase> pay(Payment payment) {
    return isProductAlreadyPurchased((AptoideProduct) payment.getProduct()).flatMap(alreadyPurchased -> {

      if (alreadyPurchased) {
        return Observable.<Purchase>error(new PaymentAlreadyProcessedException(
            "Product " + payment.getProduct().getId() + " already purchased."));
      }

      return paymentRepository.getPaymentConfirmation(payment.getProduct().getId())
          .onErrorResumeNext(confirmationThrowable -> {
            if (confirmationThrowable instanceof RepositoryItemNotFoundException) {
              return processPaymentAndGetConfirmation(payment);
            }
            return Observable.<PaymentConfirmation>error(confirmationThrowable);
          })
          .first(paymentConfirmation -> paymentConfirmation.isCompleted())
          .flatMap(saved -> paymentRepository.getPurchase((AptoideProduct) payment.getProduct()));
    }).subscribeOn(Schedulers.computation());
  }

  private Observable<PaymentConfirmation> processPaymentAndGetConfirmation(Payment payment) {
    return payment.process()
        .flatMap(paymentConfirmation -> paymentRepository.savePaymentConfirmation(
            paymentConfirmation))
        .doOnNext(saved -> syncPaymentConfirmationInBackground())
        .flatMap(saved -> paymentRepository.getPaymentConfirmation(payment.getProduct().getId()));
  }

  private void syncPaymentConfirmationInBackground() {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    ContentResolver.requestSync(getAccount(), configuration.getSyncAdapterAuthority(), bundle);
  }

  @NonNull private Account getAccount() {
    Account[] accounts = accountManager.getAccountsByType(configuration.getAccountType());
    if (accounts != null && accounts.length > 0) {
      return accounts[0];
    }
    throw new IllegalStateException("User not logged in. Can't complete payment.");
  }

  private Observable<Boolean> isProductAlreadyPurchased(AptoideProduct product) {
    return paymentRepository.getPurchase(product)
        .map(purchase -> true)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(false);
          }
          return error(throwable);
        });
  }
}