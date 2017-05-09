package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentNotAuthorizedException;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationRepository;
import rx.Completable;
import rx.Observable;

public abstract class AuthorizationPayment extends AptoidePayment {

  public AuthorizationPayment(int id, String name, String description,
      PaymentConfirmationRepository confirmationRepository) {
    super(id, name, description, confirmationRepository);
  }

  @Override public Completable process(Product product) {
    return authorize().andThen(super.process(product));
  }

  public abstract Completable authorize();

  public abstract Observable<? extends Authorization> getAuthorization();
}