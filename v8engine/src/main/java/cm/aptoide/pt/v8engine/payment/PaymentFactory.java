/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.accounts.Account;
import android.content.Context;
import android.support.annotation.NonNull;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.payment.providers.dummy.DummyPayment;
import cm.aptoide.pt.v8engine.payment.providers.paypal.PayPalPayment;
import cm.aptoide.pt.v8engine.payment.providers.web.WebPayment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentFactory {

  public static final String PAYPAL = "paypal";
  public static final String BOACOMPRA = "boacompra";
  public static final String DUMMY = "dummy";
  private Account account;

  public Payment create(Context context, PaymentService paymentService, Product product) {
    switch (paymentService.getShortName()) {
      case PAYPAL:
        return new PayPalPayment(context, paymentService.getId(), paymentService.getShortName(),
            paymentService.getName(), paymentService.getSign(),
            getPrice(paymentService.getPrice(), paymentService.getCurrency(),
                paymentService.getTaxRate()), getPayPalConfiguration(), product,
            paymentService.getTypes().get(0).getLabel(), RepositoryFactory.getPaymentConfirmationRepository(context, product));
      case BOACOMPRA:
        return new WebPayment(context, paymentService.getId(), paymentService.getShortName(),
            product, getPrice(paymentService.getPrice(), paymentService.getCurrency(),
            paymentService.getTaxRate()), paymentService.getName(),
            RepositoryFactory.getPaymentAuthorizationRepository(context),
            RepositoryFactory.getPaymentConfirmationRepository(context, product));
      case DUMMY:
        return new DummyPayment(paymentService.getId(), paymentService.getShortName(), product,
            getPrice(paymentService.getPrice(), paymentService.getCurrency(),
                paymentService.getTaxRate()), paymentService.getName(),
            RepositoryFactory.getPaymentConfirmationRepository(context, product));
      default:
        throw new IllegalArgumentException(
            "Payment not supported: " + paymentService.getShortName());
    }
  }

  @NonNull private Price getPrice(double price, String currency, double taxRate) {
    return new Price(price, currency, taxRate);
  }

  private PayPalConfiguration getPayPalConfiguration() {
    final PayPalConfiguration configuration = new PayPalConfiguration();
    configuration.environment(BuildConfig.PAYPAL_ENVIRONMENT);
    configuration.clientId(BuildConfig.PAYPAL_KEY);
    return configuration;
  }

  public Account getAccount() {
    return account;
  }
}