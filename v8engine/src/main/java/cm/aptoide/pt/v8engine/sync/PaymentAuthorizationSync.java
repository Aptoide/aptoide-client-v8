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
        .flatMapIterable(paymentAuthorizations -> paymentAuthorizations)
        .doOnNext(paymentAuthorization -> {
          if (!paymentAuthorization.isAuthorized()) {
            rescheduleSync(syncResult);
          }
        })
        .toList()
        .onErrorReturn(throwable -> null)
        .toBlocking()
        .firstOrDefault(null);
  }
}