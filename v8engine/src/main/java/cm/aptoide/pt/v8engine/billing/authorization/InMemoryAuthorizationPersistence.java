package cm.aptoide.pt.v8engine.billing.authorization;

import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class InMemoryAuthorizationPersistence implements AuthorizationPersistence {

  private final Map<String, Authorization> authorizations;
  private final AuthorizationFactory authorizationFactory;
  private final PublishRelay<Authorization> authorizationRelay;

  public InMemoryAuthorizationPersistence(Map<String, Authorization> authorizations,
      PublishRelay<Authorization> authorizationRelay, AuthorizationFactory authorizationFactory) {
    this.authorizations = authorizations;
    this.authorizationRelay = authorizationRelay;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> {
      authorizations.put(
          getAuthorizationKey(authorization.getPayerId(), authorization.getPaymentId()),
          authorization);
      authorizationRelay.call(authorization);
    });
  }

  @Override public Observable<Authorization> getAuthorization(String payerId, int paymentMethodId) {
    return authorizationRelay.startWith(Observable.defer(() -> {
      if (authorizations.containsKey(getAuthorizationKey(payerId, paymentMethodId))) {
        return Observable.just(authorizations.get(getAuthorizationKey(payerId, paymentMethodId)));
      }
      return Observable.empty();
    }))
        .filter(authorization -> authorization.getPaymentId() == paymentMethodId);
  }

  @Override public Completable saveAuthorizations(List<Authorization> authorizations) {
    return Observable.from(authorizations)
        .flatMapCompletable(authorization -> saveAuthorization(authorization))
        .toCompletable();
  }

  @Override public Single<Authorization> createAuthorization(String payerId, int paymentMethodId,
      Authorization.Status status) {
    final Authorization authorization =
        authorizationFactory.create(paymentMethodId, status, payerId, "", "");
    return saveAuthorization(authorization).andThen(Single.just(authorization));
  }

  private String getAuthorizationKey(String payerId, int paymentMethodId) {
    return payerId + paymentMethodId;
  }
}