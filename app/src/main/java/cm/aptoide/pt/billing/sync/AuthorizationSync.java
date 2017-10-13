/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.AuthorizationService;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.spotandshare.socket.Log;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class AuthorizationSync extends Sync {

  private final String transactionId;
  private final Customer customer;
  private final AuthorizationService authorizationService;
  private final AuthorizationPersistence authorizationPersistence;

  public AuthorizationSync(String id, Customer customer, String transactionId,
      AuthorizationService authorizationService, AuthorizationPersistence authorizationPersistence,
      boolean periodic, boolean exact, long interval, long trigger) {
    super(id, periodic, exact, trigger, interval);
    this.transactionId = transactionId;
    this.customer = customer;
    this.authorizationService = authorizationService;
    this.authorizationPersistence = authorizationPersistence;
  }

  @Override public Completable execute() {
    return customer.getId()
        .flatMapCompletable(
            customerId -> syncRemoteAuthorization(transactionId).andThen(
                syncPayPalAuthorization(customerId, transactionId)));
  }

  private Completable syncPayPalAuthorization(String customerId, String transactionId) {
    return authorizationPersistence.getAuthorization(customerId, transactionId)
        .first()
        .filter(authorization -> authorization instanceof PayPalAuthorization)
        .cast(PayPalAuthorization.class)
        .filter(authorization -> authorization.isPendingSync())
        .flatMapSingle(authorization -> authorizationService.updateAuthorization(
            authorization.getTransactionId(), authorization.getMetadata()))
        .flatMapCompletable(
            authorization -> authorizationPersistence.saveAuthorization(authorization))
        .toCompletable();
  }

  private Completable syncRemoteAuthorization(String transactionId) {
    return authorizationService.getAuthorization(transactionId)
        .flatMapCompletable(
            authorization -> authorizationPersistence.saveAuthorization(authorization));
  }
}