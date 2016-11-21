/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.web;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import rx.Observable;

/**
 * Created by marcelobenites on 14/10/16.
 */
public class WebPayment implements Payment {

  private final Context context;
  private final int id;
  private final String type;
  private final Product product;
  private final Price price;
  private final String description;
  private PaymentRepository paymentRepository;
  private final AptoidePreferencesConfiguration configuration;
  private AccountManager accountManager;

  public WebPayment(Context context, int id, String type, Product product, Price price,
      String description, PaymentRepository paymentRepository,
      AptoidePreferencesConfiguration configuration, AccountManager accountManager) {
    this.context = context;
    this.id = id;
    this.type = type;
    this.product = product;
    this.price = price;
    this.description = description;
    this.paymentRepository = paymentRepository;
    this.configuration = configuration;
    this.accountManager = accountManager;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getType() {
    return type;
  }

  @Override public Product getProduct() {
    return product;
  }

  @Override public Price getPrice() {
    return price;
  }

  @Override public String getDescription() {
    return description;
  }

  @Override public Observable<PaymentConfirmation> process() {
    return getOrCreateAuthorization().flatMap(authorization -> {
      if (authorization.isAuthorized()) {
        return paymentRepository.createPaymentConfirmation(this);
      } else if (authorization.displayAuthorizationView()) {
        startWebAuthorizationActivity(authorization.getUrl(), authorization.getRedirectUrl());
        return Observable.empty();
      } else if (authorization.isCancelled()) {
        return Observable.error(new PaymentFailureException("Authorization Failed."));
      }
      return Observable.error(new PaymentFailureException("Invalid authorization status."));
    });
  }

  private Observable<PaymentAuthorization> getOrCreateAuthorization() {
    return paymentRepository.getPaymentAuthorization(id).onErrorResumeNext(throwable -> {
      if (throwable instanceof RepositoryItemNotFoundException) {
        return paymentRepository.createPaymentAuthorization(id)
            .flatMap(paymentAuthorization -> paymentRepository.savePaymentAuthorization(
                paymentAuthorization))
            .flatMap(success -> paymentRepository.getPaymentAuthorization(id)
                .doOnSubscribe(() -> syncPaymentAuthorizationInBackground()));
      }
      return Observable.<PaymentAuthorization>error(throwable);
    });
  }

  private void syncPaymentAuthorizationInBackground() {
    final Bundle bundle = new Bundle();
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    ContentResolver.requestSync(getAccount(), configuration.getSyncAdapterAuthority(), bundle);
  }

  @NonNull private Account getAccount() {
    Account[] accounts = accountManager.getAccountsByType(configuration.getAccountType());
    if (accounts != null && accounts.length > 0) {
      return accounts[0];
    }
    throw new IllegalStateException("User not logged in. Can't complete payment.");
  }

  private void startWebAuthorizationActivity(String url, String resultUrl) {
    context.startActivity(WebPaymentActivity.getIntent(context, url, resultUrl));
  }
}