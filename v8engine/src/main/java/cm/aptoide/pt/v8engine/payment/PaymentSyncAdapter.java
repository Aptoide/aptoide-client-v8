/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import cm.aptoide.pt.v8engine.repository.PaymentRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.io.IOException;

/**
 * Created by marcelobenites on 18/11/16.
 */

public class PaymentSyncAdapter extends AbstractThreadedSyncAdapter {

  private PaymentRepository paymentRepository;

  public PaymentSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
    super(context, autoInitialize, allowParallelSyncs);
    paymentRepository = RepositoryFactory.getPaymentRepository(context);
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {

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

  private void rescheduleIncompletedPaymentSync(PaymentConfirmation paymentConfirmation,
      SyncResult syncResult) {
    if (!paymentConfirmation.isCompleted()) {
      rescheduleSync(syncResult);
    }
  }

  private void rescheduleOrCancelSync(SyncResult syncResult, Throwable throwable) {
    if (throwable instanceof IOException) {
      rescheduleSync(syncResult);
    } else {
      cancelSync(syncResult);
    }
  }

  private void cancelSync(SyncResult syncResult) {
    syncResult.tooManyRetries = true;
  }

  private void rescheduleSync(SyncResult syncResult) {
    syncResult.stats.numIoExceptions++;
  }
}
