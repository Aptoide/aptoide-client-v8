package cm.aptoide.pt.billing.authorization;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(String customerId, String transactionId);

  Single<Authorization> updateAuthorization(String customerId, String transactionId,
      Authorization.Status status, String metadata);

  Single<Authorization> createAuthorization(String customerId, String transactionId,
      Authorization.Status status);
}
