/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.paypal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.exception.PaymentCancellationException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.providers.AbstractPayment;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import java.util.Locale;
import rx.Observable;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PayPalPayment extends AbstractPayment {

  public static final String PAYMENT_RESULT_ACTION =
      "cm.aptoide.pt.v8engine.payment.service.action.PAYMENT_RESULT";
  public static final String PAYMENT_CONFIRMATION_EXTRA =
      "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_CONFIRMATION";
  public static final String PAYMENT_STATUS_EXTRA =
      "cm.aptoide.pt.v8engine.payment.service.extra.PAYMENT_STATUS";

  public static final int PAYMENT_STATUS_OK = 0;
  public static final int PAYMENT_STATUS_FAILED = 1;
  public static final int PAYMENT_STATUS_CANCELLED = 2;

  private final Context context;
  private final PayPalConfiguration configuration;
  private final PayPalConverter converter;

  public PayPalPayment(Context context, int id, String type, String name, String sign, Price price,
      PayPalConfiguration configuration, PayPalConverter converter, Product product, String methodLabel) {
    super(id, type, product, price, String.format(Locale.getDefault(), "%s - %.2f %s", methodLabel, price.getAmount(), sign));
    this.context = context;
    this.configuration = configuration;
    this.converter = converter;
  }

  @Override public Observable<PaymentConfirmation> process() {
    final IntentFilter paymentResultFilter = new IntentFilter();
    paymentResultFilter.addAction(PAYMENT_RESULT_ACTION);
    return Observable.create(
        new BroadcastRegisterOnSubscribe(context, paymentResultFilter, null, null))
        .doOnSubscribe(() -> startPayPalActivity(getPrice(),   getProduct()))
        .filter(intent -> isPaymentConfirmationIntent(intent))
        .flatMap(intent -> convertToPaymentConfirmation(intent, getId(), getProduct(), getPrice()));
  }

  @NonNull
  private Observable<PaymentConfirmation> convertToPaymentConfirmation(Intent intent, int id,
      Product product, Price price) {
    final com.paypal.android.sdk.payments.PaymentConfirmation payPalConfirmation;
    switch (intent.getIntExtra(PAYMENT_STATUS_EXTRA, PAYMENT_STATUS_FAILED)) {
      case PAYMENT_STATUS_OK:
        payPalConfirmation = intent.getParcelableExtra(PAYMENT_CONFIRMATION_EXTRA);
        if (payPalConfirmation != null) {
          return Observable.just(
              converter.convertFromPayPal(payPalConfirmation, id, product, price));
        }
        return Observable.error(new PaymentFailureException(
            "PayPal payment returned invalid payment confirmation"));
      case PAYMENT_STATUS_CANCELLED:
        return Observable.error(
            new PaymentCancellationException("PayPal payment cancelled by user"));
      case PAYMENT_STATUS_FAILED:
      default:
        return Observable.error(new PaymentFailureException("PayPal payment failed"));
    }
  }

  private boolean isPaymentConfirmationIntent(Intent intent) {
    return intent != null
        && PAYMENT_RESULT_ACTION.equals(intent.getAction())
        && intent.hasExtra(PAYMENT_STATUS_EXTRA);
  }

  private void startPayPalActivity(Price price, Product product) {
    context.startActivity(PayPalPaymentActivity.getIntent(context,
        converter.convertToPayPal(price.getAmount(), price.getCurrency(), product.getTitle()),
        configuration));
  }
}