/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentAuthorizationSync extends AbstractSync {

  private final PaymentAuthorizationRepository authorizationRepository;

  public PaymentAuthorizationSync(PaymentAuthorizationRepository authorizationRepository) {
    this.authorizationRepository = authorizationRepository;
  }

  @Override public void sync(SyncResult syncResult) {
    authorizationRepository.getPaymentAuthorizations()
        .first()
        .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
        .flatMap(paymentAuthorization -> {
          if (paymentAuthorization.isCancelled()) {
            return authorizationRepository.removePaymentAuthorization(paymentAuthorization.getPaymentId());
          } else if (!paymentAuthorization.isAuthorized()) {
            rescheduleIncompletedAuthorizationSync(paymentAuthorization, syncResult);
          }
          return authorizationRepository.savePaymentAuthorization(paymentAuthorization);
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
