package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RealmAuthorizationPersistence implements AuthorizationPersistence {

  private final Database realm;
  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationPersistence(Database realm, AuthorizationFactory authorizationFactory) {
    this.realm = realm;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> realm.insert(authorizationFactory.map(authorization)));
  }

  @Override public Observable<Authorization> getAuthorization(int paymentId, String payerId) {
    return realm.getRealm()
        .map(realm -> realm.where(PaymentAuthorization.class)
            .equalTo(PaymentAuthorization.PAYER_ID, payerId)
            .equalTo(PaymentAuthorization.PAYMENT_ID, paymentId))
        .flatMap(query -> realm.findAsList(query))
        .flatMap(authorizations -> Observable.from(authorizations)
            .map(paymentAuthorization -> authorizationFactory.map(paymentAuthorization)));
  }

  @Override public Completable saveAuthorizations(List<Authorization> authorizations) {
    return Observable.from(authorizations)
        .map(authorization -> authorizationFactory.map(authorization))
        .toList()
        .doOnNext(paymentAuthorizations -> realm.insertAll(paymentAuthorizations))
        .toCompletable();
  }

  @Override public Single<Authorization> createAuthorization(int paymentId, String payerId,
      Authorization.Status status) {
    final Authorization authorization =
        authorizationFactory.create(paymentId, status, payerId, "", "");
    return saveAuthorization(authorization).andThen(Single.just(authorization));
  }
}