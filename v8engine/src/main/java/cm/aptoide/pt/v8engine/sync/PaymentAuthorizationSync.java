/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentAuthorizationSync extends AbstractSync {

  private final PaymentRepository paymentRepository;

  public PaymentAuthorizationSync(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  @Override public void sync(SyncResult syncResult) {
    paymentRepository.getPaymentAuthorizations()
        .first()
        .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
        .flatMap(paymentAuthorization -> {
          if (paymentAuthorization.isCancelled()) {
            return paymentRepository.removePaymentAuthorization(paymentAuthorization.getPaymentId());
          } else if (!paymentAuthorization.isAuthorized()) {
            rescheduleIncompletedAuthorizationSync(paymentAuthorization, syncResult);
          }
          return paymentRepository.savePaymentAuthorization(paymentAuthorization);
        })
        .toList()
        .onErrorReturn(throwable -> {
          rescheduleOrCancelSync(syncResult, throwable);
          return null;
        })
        .toBlocking()
        .firstOrDefault(null);
  }

  private void rescheduleIncompletedAuthorizationSync(PaymentAuthorization paymentAuthorization,
      SyncResult syncResult) {
    if (!paymentAuthorization.isAuthorized()) {
      rescheduleSync(syncResult);
    }
  }
}
