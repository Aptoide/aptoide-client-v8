package cm.aptoide.pt.billing.authorization;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(String customerId, int paymentId);

  Completable saveAuthorizations(List<Authorization> authorizations);

  Single<Authorization> createAuthorization(String customerId, int paymentId,
      Authorization.Status status);
}
