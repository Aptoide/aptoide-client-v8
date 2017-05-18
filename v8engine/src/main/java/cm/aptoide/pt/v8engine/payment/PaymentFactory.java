/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.pt.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.payment.providers.paypal.PayPalPayment;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import com.paypal.android.sdk.payments.PayPalConfiguration;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentFactory {

  public static final String PAYPAL = "paypal";
  public static final String BOACOMPRA = "boacompra";
  public static final String BOACOMPRAGOLD = "boacompragold";
  public static final String DUMMY = "dummy";

  private final Context context;

  public PaymentFactory(Context context) {
    this.context = context;
  }

  public Payment create(PaymentServiceResponse paymentService, Authorization authorization,
      PaymentConfirmationRepository confirmationRepository) {
    switch (paymentService.getShortName()) {
      case PAYPAL:
        return new PayPalPayment(context, paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), confirmationRepository, authorization,
            getPayPalConfiguration());
      case BOACOMPRA:
      case BOACOMPRAGOLD:
      case DUMMY:
        return new AptoidePayment(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), confirmationRepository, authorization);
      default:
        throw new IllegalArgumentException(
            "Payment not supported: " + paymentService.getShortName());
    }
  }

  private PayPalConfiguration getPayPalConfiguration() {
    final PayPalConfiguration configuration = new PayPalConfiguration();
    configuration.environment(BuildConfig.PAYPAL_ENVIRONMENT);
    configuration.clientId(BuildConfig.PAYPAL_KEY);
    return configuration;
  }
}