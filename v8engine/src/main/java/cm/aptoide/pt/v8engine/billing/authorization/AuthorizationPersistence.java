package cm.aptoide.pt.v8engine.billing.authorization;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(String payerId, int paymentId);

  Completable saveAuthorizations(List<Authorization> authorizations);

  Single<Authorization> createAuthorization(String payerId, int paymentId,
      Authorization.Status status);
}
