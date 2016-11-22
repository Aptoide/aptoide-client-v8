/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.content.SyncResult;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import java.io.IOException;

/**
 * Created by marcelobenites on 22/11/16.
 */

public class PaymentConfirmationSync extends AbstractSync {

  private final PaymentRepository paymentRepository;

  public PaymentConfirmationSync(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  @Override public void sync(SyncResult syncResult) {
    paymentRepository.getPaymentConfirmations()
        .first()
        .flatMapIterable(paymentConfirmations -> paymentConfirmations)
        .flatMap(paymentConfirmation -> {
          if (paymentConfirmation.isFailed()) {
            return paymentRepository.removePaymentConfirmation(
                paymentConfirmation.getPaymentConfirmationId());
          } else if (!paymentConfirmation.isCompleted()) {
            rescheduleIncompletedPaymentSync(paymentConfirmation, syncResult);
          }
          return paymentRepository.savePaymentConfirmation(paymentConfirmation);
        })
        .toList()
        .onErrorReturn(throwable -> {
          rescheduleOrCancelSync(syncResult, throwable);
          return null;
        })
        .toBlocking()
        .firstOrDefault(null);
  }

  private void rescheduleIncompletedPaymentSync(PaymentConfirmation paymentConfirmation,
      SyncResult syncResult) {
    if (!paymentConfirmation.isCompleted()) {
      rescheduleSync(syncResult);
    }
  }
}
