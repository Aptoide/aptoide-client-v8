/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.billing.authorization.LocalIdGenerator;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import com.jakewharton.rxrelay.PublishRelay;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Cleanup;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class RealmAuthorizationPersistence implements AuthorizationPersistence {

  private final Map<String, Authorization> authorizations;
  private final PublishRelay<List<Authorization>> authorizationRelay;
  private final Database database;
  private final RealmAuthorizationMapper authorizationMapper;
  private final Scheduler scheduler;
  private final AuthorizationFactory authorizationFactory;
  private final LocalIdGenerator idGenerator;

  public RealmAuthorizationPersistence(Map<String, Authorization> authorizations,
      PublishRelay<List<Authorization>> authorizationRelay, Database database,
      RealmAuthorizationMapper authorizationMapper, Scheduler scheduler,
      AuthorizationFactory authorizationFactory, LocalIdGenerator idGenerator) {
    this.authorizations = authorizations;
    this.authorizationRelay = authorizationRelay;
    this.database = database;
    this.authorizationMapper = authorizationMapper;
    this.scheduler = scheduler;
    this.authorizationFactory = authorizationFactory;
    this.idGenerator = idGenerator;
  }

  @Override public Completable saveAuthorization(Authorization authorization) {
    return Completable.fromAction(() -> {

      if (authorization.isPendingSync()) {
        database.insert(authorizationMapper.map(authorization));
      } else {
        authorizations.put(authorization.getId(), authorization);
      }

      authorizationRelay.call(getAuthorizations());
    })
        .subscribeOn(scheduler);
  }

  @Override
  public Single<Authorization> updateAuthorization(String customerId, String transactionId,
      Authorization.Status status, String metadata) {
    return Observable.from(getAuthorizations())
        .filter(authorization -> authorization.getCustomerId()
            .equals(customerId) && authorization.getTransactionId()
            .equals(transactionId))
        .first()
        .toSingle()
        .flatMap(authorization -> {
          final Authorization updatedAuthorization =
              authorizationFactory.create(authorization.getId(), authorization.getCustomerId(),
                  authorizationFactory.getType(authorization), status, null, null, metadata, null,
                  null, authorization.getTransactionId(), null);
          return saveAuthorization(updatedAuthorization).andThen(Single.just(updatedAuthorization));
        });
  }

  @Override
  public Single<Authorization> createAuthorization(String customerId, String transactionId,
      Authorization.Status status) {
    final Authorization authorization =
        authorizationFactory.create(idGenerator.generate(), customerId, null, status, null, null,
            null, null, null, transactionId, null);
    return saveAuthorization(authorization).andThen(Single.just(authorization));
  }

  @Override
  public Observable<Authorization> getAuthorization(String customerId, String transactionId) {
    return authorizationRelay.startWith(getAuthorizations())
        .flatMap(authorizations -> Observable.from(authorizations)
            .filter(authorization -> authorization.getCustomerId()
                .equals(customerId) && authorization.getTransactionId()
                .equals(transactionId)))
        .subscribeOn(scheduler);
  }

  private List<Authorization> getAuthorizations() {

    final Map<String, Authorization> resolvedAuthorizations = new HashMap<>(authorizations);
    for (Authorization localAuthorization : getLocalAuthorization()) {
      resolvedAuthorizations.put(localAuthorization.getId(),
          resolveAuthorization(authorizations.get(localAuthorization.getId()), localAuthorization));
    }
    return new ArrayList<>(resolvedAuthorizations.values());
  }

  private Authorization resolveAuthorization(Authorization authorization,
      Authorization localAuthorization) {

    if (authorization == null) {
      return localAuthorization;
    }

    if (localAuthorization == null
        || authorization.isProcessing()
        || authorization.isActive()
        || authorization.isFailed()) {
      return authorization;
    }

    return localAuthorization;
  }

  private List<Authorization> getLocalAuthorization() {
    @Cleanup Realm realm = database.get();

    final RealmResults<RealmAuthorization> realmAuthorizations =
        realm.where(RealmAuthorization.class)
            .findAll();

    final List<Authorization> pendingSyncAuthorizations =
        new ArrayList<>(realmAuthorizations.size());

    for (RealmAuthorization realmAuthorization : realmAuthorizations) {
      pendingSyncAuthorizations.add(authorizationMapper.map(realmAuthorization));
    }

    return pendingSyncAuthorizations;
  }
}