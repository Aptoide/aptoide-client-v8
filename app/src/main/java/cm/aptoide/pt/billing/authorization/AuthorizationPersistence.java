package cm.aptoide.pt.billing.authorization;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(String customerId, long transactionId);
}
