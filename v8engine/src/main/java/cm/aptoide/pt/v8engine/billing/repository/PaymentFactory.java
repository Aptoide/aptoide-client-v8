/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import android.content.Context;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.services.AptoidePayment;
import cm.aptoide.pt.v8engine.billing.services.paypal.PayPalPayment;
import cm.aptoide.pt.v8engine.billing.services.web.WebAuthorizationPayment;
import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PaymentFactory {

  public static final String PAYPAL = "paypal_future";
  public static final String BOACOMPRA = "boacompra";
  public static final String BOACOMPRAGOLD = "boacompragold";
  public static final String DUMMY = "dummy";

  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;
  private PayPalConfiguration payPalConfiguration;

  public PaymentFactory(PaymentRepositoryFactory paymentRepositoryFactory,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, Payer payer) {
    this.paymentRepositoryFactory = paymentRepositoryFactory;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
    this.payPalConfiguration = new PayPalConfiguration();
    this.payPalConfiguration.environment(BuildConfig.PAYPAL_ENVIRONMENT);
    this.payPalConfiguration.clientId(BuildConfig.PAYPAL_KEY);
  }

  public Payment create(Context context, PaymentServiceResponse paymentService) {
    switch (paymentService.getShortName()) {
      case PAYPAL:
        return new PayPalPayment(context, paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), paymentRepositoryFactory, authorizationRepository,
            payPalConfiguration, paymentService.isAuthorizationRequired(), authorizationFactory, payer);
      case BOACOMPRA:
      case BOACOMPRAGOLD:
        return new WebAuthorizationPayment(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), paymentRepositoryFactory, authorizationRepository,
            paymentService.isAuthorizationRequired(), authorizationFactory, payer);
      case DUMMY:
        return new AptoidePayment(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), paymentRepositoryFactory);
      default:
        throw new IllegalArgumentException(
            "Payment not supported: " + paymentService.getShortName());
    }
  }
}