package cm.aptoide.pt.v8engine.billing;

import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public interface AuthorizationPersistence {

  Completable saveAuthorization(Authorization authorization);

  Observable<Authorization> getAuthorization(int paymentId, String payerId);

  Completable saveAuthorizations(List<Authorization> authorizations);

  Single<Authorization> createAuthorization(int paymentId, String payerId,
      Authorization.Status status);
}
