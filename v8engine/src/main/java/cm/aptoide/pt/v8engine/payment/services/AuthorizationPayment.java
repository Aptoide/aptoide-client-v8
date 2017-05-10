package cm.aptoide.pt.v8engine.payment.services;

import cm.aptoide.pt.v8engine.payment.Authorization;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentRepositoryFactory;
import cm.aptoide.pt.v8engine.payment.services.AptoidePayment;
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