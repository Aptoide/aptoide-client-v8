package cm.aptoide.pt.billing.authorization;

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
          getAuthorizationKey(authorization.getCustomerId(), authorization.getPaymentId()),
          authorization);
      authorizationRelay.call(authorization);
    });
  }

  @Override public Observable<Authorization> getAuthorization(String customerId, int paymentMethodId) {
    return authorizationRelay.startWith(Observable.defer(() -> {
      if (authorizations.containsKey(getAuthorizationKey(customerId, paymentMethodId))) {
        return Observable.just(authorizations.get(getAuthorizationKey(customerId, paymentMethodId)));
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

  @Override public Single<Authorization> createAuthorization(String customerId, int paymentMethodId,
      Authorization.Status status) {
    final Authorization authorization =
        authorizationFactory.create(paymentMethodId, status, customerId, "", "");
    return saveAuthorization(authorization).andThen(Single.just(authorization));
  }

  private String getAuthorizationKey(String customerId, int paymentMethodId) {
    return customerId + paymentMethodId;
  }
}