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

  private final Map<Long, Authorization> authorizations;
  private final PublishRelay<List<Authorization>> authorizationRelay;
  private final Database database;
  private final AuthorizationMapper authorizationMapper;
  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationPersistence(Map<Long, Authorization> authorizations,
      PublishRelay<List<Authorization>> authorizationRelay, Database database,
      AuthorizationMapper authorizationMapper, AuthorizationFactory authorizationFactory) {
    this.authorizations = authorizations;
    this.authorizationRelay = authorizationRelay;
    this.database = database;
    this.authorizationMapper = authorizationMapper;
    this.authorizationFactory = authorizationFactory;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> {

      if (authorization.isPendingSync()) {
        database.insert(authorizationMapper.map((PayPalAuthorization) authorization));
      }
      authorizations.put(authorization.getId(), authorization);

      authorizationRelay.call(new ArrayList<>(authorizations.values()));
    });
  }

  @Override
  public Observable<Authorization> getAuthorization(String customerId, long transactionId) {
    return authorizationRelay.startWith(new ArrayList<Authorization>(authorizations.values()))
        .flatMap(authorizations -> Observable.from(authorizations)
            .filter(authorization -> authorization.getCustomerId()
                .equals(customerId) && authorization.getTransactionId() == transactionId)
            .flatMap(authorization -> resolveAuthorization(authorization))
            .defaultIfEmpty(
                authorizationFactory.create(-1, customerId, "", Authorization.Status.NEW, null,
                    null, null, null, null, transactionId)));
  }

  private Observable<Authorization> resolveAuthorization(Authorization authorization) {

    final Authorization pendingSyncAuthorization =
        getPendingSyncAuthorization(authorization.getId());

    if (pendingSyncAuthorization == null && authorization == null) {
      return Observable.empty();
    }

    if (pendingSyncAuthorization == null || authorization.isActive()) {
      return Observable.just(authorization);
    }

    return Observable.just(pendingSyncAuthorization);
  }

  private Authorization getPendingSyncAuthorization(long id) {
    @Cleanup Realm realm = database.get();

    final RealmAuthorization realmAuthorization = realm.where(RealmAuthorization.class)
        .equalTo(RealmAuthorization.ID, id)
        .findFirst();

    Authorization pendingSyncAuthorization = null;
    if (realmAuthorization != null) {
      pendingSyncAuthorization = authorizationMapper.map(realmAuthorization);
    }
    return pendingSyncAuthorization;
  }
}