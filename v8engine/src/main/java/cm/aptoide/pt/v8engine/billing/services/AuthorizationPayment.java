package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Authorization;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import rx.Completable;
import rx.Observable;

public abstract class AuthorizationPayment extends AptoidePayment {

  public AuthorizationPayment(int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory) {
    super(id, name, description, paymentRepositoryFactory);
  }

  @Override public Completable process(Product product) {
    return authorize().andThen(super.process(product));
  }

  public abstract Completable authorize();

  public abstract Observable<? extends Authorization> getAuthorization();
}