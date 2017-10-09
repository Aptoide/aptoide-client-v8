/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import com.jakewharton.rxrelay.PublishRelay;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Cleanup;
import rx.Completable;
import rx.Observable;

public class RealmAuthorizationPersistence implements AuthorizationPersistence {

  private final Map<Long, Authorization> remoteAuthorizations;
  private final PublishRelay<List<Authorization>> authorizationRelay;
  private final Database localAuthorizationsDatabase;
  private final AuthorizationMapper authorizationMapper;
  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationPersistence(Map<Long, Authorization> remoteAuthorizations,
      PublishRelay<List<Authorization>> authorizationRelay, Database database,
      AuthorizationMapper authorizationMapper, AuthorizationFactory authorizationFactory) {
    this.remoteAuthorizations = remoteAuthorizations;
    this.authorizationRelay = authorizationRelay;
    this.localAuthorizationsDatabase = database;
    this.authorizationMapper = authorizationMapper;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> {

      if (authorization instanceof PayPalAuthorization) {
        localAuthorizationsDatabase.insert(
            authorizationMapper.map((PayPalAuthorization) authorization));
        remoteAuthorizations.remove(authorization.getId());
      } else {
        remoteAuthorizations.put(authorization.getId(), authorization);
      }

      authorizationRelay.call(new ArrayList<>(remoteAuthorizations.values()));
    });
  }

  @Override
  public Observable<Authorization> getAuthorization(String customerId, long transactionId) {
    return authorizationRelay.startWith(new ArrayList<Authorization>(remoteAuthorizations.values()))
        .flatMap(authorizations -> Observable.from(authorizations)
            .filter(authorization -> authorization.getCustomerId()
                .equals(customerId) && authorization.getTransactionId() == transactionId)
            .flatMap(authorization -> resolveAuthorization(authorization))
            .defaultIfEmpty(
                authorizationFactory.create(-1, customerId, "", Authorization.Status.PENDING, null,
                    null, null, null, null, transactionId)));
  }

  private Observable<Authorization> resolveAuthorization(Authorization remoteAuthorization) {

    final Authorization localAuthorization = getLocalAuthorization(remoteAuthorization.getId());

    if (localAuthorization == null && remoteAuthorization == null) {
      return Observable.empty();
    }

    if (remoteAuthorization == null) {
      return Observable.just(localAuthorization);
    }

    if (localAuthorization == null) {
      return Observable.just(remoteAuthorization);
    }

    if (remoteAuthorization.getStatus()
        .equals(Authorization.Status.PENDING)) {
      return Observable.just(localAuthorization);
    }

    if (localAuthorization.isInactive()) {
      return Observable.just(localAuthorization);
    }

    return Observable.just(remoteAuthorization);
  }

  private Authorization getLocalAuthorization(long id) {
    @Cleanup Realm realm = localAuthorizationsDatabase.get();

    final RealmAuthorization realmAuthorization = getRealmAuthorization(id, realm);

    Authorization localAuthorization = null;
    if (realmAuthorization != null) {
      localAuthorization = authorizationMapper.map(realmAuthorization);
    }
    return localAuthorization;
  }

  private RealmAuthorization getRealmAuthorization(long id, Realm realm) {
    return realm.where(RealmAuthorization.class)
        .equalTo(RealmAuthorization.ID, id)
        .findFirst();
  }
}