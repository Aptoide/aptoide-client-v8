/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 17/02/2017.
 */

package cm.aptoide.pt.v8engine.presenter;

import android.content.SharedPreferences;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public class PaymentMethodSelector {

  private static final String SELECTED_PAYMENT_ID = "selectedPaymentId";
  private final int defaultPaymentId;
  private final SharedPreferences preferences;

  public PaymentMethodSelector(int defaultPaymentMethodId, SharedPreferences preferences) {
    this.defaultPaymentId = defaultPaymentMethodId;
    this.preferences = preferences;
  }

  public Single<PaymentMethod> selectedPaymentMethod(List<PaymentMethod> paymentMethods) {
    return getSelectedPaymentMethodId().flatMap(
        selectedPaymentId -> paymentMethod(paymentMethods, selectedPaymentId).switchIfEmpty(
            paymentMethod(paymentMethods, defaultPaymentId))
            .switchIfEmpty(Observable.just(paymentMethods.get(0)))
            .first()
            .toSingle());
  }

  public Completable selectPaymentMethod(PaymentMethod selectedPaymentMethod) {
    return Completable.fromAction(() -> preferences.edit()
        .putInt(SELECTED_PAYMENT_ID, selectedPaymentMethod.getId())
        .commit())
        .subscribeOn(Schedulers.io());
  }

  private Single<Integer> getSelectedPaymentMethodId() {
    return Single.fromCallable(() -> preferences.getInt(SELECTED_PAYMENT_ID, -1))
        .subscribeOn(Schedulers.io());
  }

  private Observable<PaymentMethod> paymentMethod(List<PaymentMethod> paymentMethods,
      int paymentId) {
    return Observable.from(paymentMethods)
        .filter(payment -> paymentId != -1 && paymentId == payment.getId());
  }
}
