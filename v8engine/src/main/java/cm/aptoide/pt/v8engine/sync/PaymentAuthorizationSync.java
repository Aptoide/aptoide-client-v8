/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.payment.PaymentAuthorization;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import rx.Observable;

/**
 * Created by marcelobenites on 22/11/16.
 */
public class PaymentAuthorizationSync extends AbstractSync {

  private final PaymentAuthorizationRepository authorizationRepository;

  public PaymentAuthorizationSync(PaymentAuthorizationRepository authorizationRepository) {
    this.authorizationRepository = authorizationRepository;
  }

  @Override public void sync(SyncResult syncResult) {
    try {
      authorizationRepository.getPaymentAuthorizations()
          .first()
          .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
          .flatMap(paymentAuthorization -> {

            if (paymentAuthorization.isCancelled()) {
              return authorizationRepository.removePaymentAuthorization(paymentAuthorization.getPaymentId());
            } else if (!paymentAuthorization.isAuthorized()) {
              rescheduleSync(syncResult);
            }

            return Observable.empty();
          })
          .toList()
          .onErrorReturn(throwable -> null)
          .toBlocking()
          .subscribe();
    } catch (RuntimeException e) {
      rescheduleSync(syncResult);
    }
  }
}