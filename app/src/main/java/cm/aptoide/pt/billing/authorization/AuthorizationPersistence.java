package cm.aptoide.pt.billing.authorization;

import rx.Completable;
import rx.Observable;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(String customerId, String transactionId);
}
