/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.payment.services.paypal.PayPalPayment;
import cm.aptoide.pt.v8engine.payment.services.web.WebAuthorizationPayment;
import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PaymentFactory {

  public static final String PAYPAL = "paypal_future";
  public static final String BOACOMPRA = "boacompra";
  public static final String BOACOMPRAGOLD = "boacompragold";
  public static final String DUMMY = "dummy";

  private final Context context;
  private final PaymentConfirmationRepository confirmationRepository;
  private final PaymentAuthorizationRepository authorizationRepository;
  private final PaymentAuthorizationFactory authorizationFactory;
  private final Payer payer;
  private PayPalConfiguration payPalConfiguration;

  public PaymentFactory(Context context, PaymentConfirmationRepository confirmationRepository,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentAuthorizationFactory authorizationFactory, Payer payer) {
    this.context = context;
    this.confirmationRepository = confirmationRepository;
    this.authorizationRepository = authorizationRepository;
    this.authorizationFactory = authorizationFactory;
    this.payer = payer;
    this.payPalConfiguration = new PayPalConfiguration();
    this.payPalConfiguration.environment(BuildConfig.PAYPAL_ENVIRONMENT);
    this.payPalConfiguration.clientId(BuildConfig.PAYPAL_KEY);
  }

  public Payment create(PaymentServiceResponse paymentService) {
    switch (paymentService.getShortName()) {
      case PAYPAL:
        return new PayPalPayment(context, paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), confirmationRepository, authorizationRepository,
            payPalConfiguration, paymentService.isAuthorizationRequired(), authorizationFactory, payer);
      case BOACOMPRA:
      case BOACOMPRAGOLD:
        return new WebAuthorizationPayment(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), confirmationRepository, authorizationRepository,
            paymentService.isAuthorizationRequired(), authorizationFactory, payer);
      case DUMMY:
        return new AptoidePayment(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), confirmationRepository);
      default:
        throw new IllegalArgumentException(
            "Payment not supported: " + paymentService.getShortName());
    }
  }
}