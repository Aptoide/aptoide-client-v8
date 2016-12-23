/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.exception.PaymentAlreadyProcessedException;
import cm.aptoide.pt.v8engine.payment.exception.PaymentFailureException;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository,
      ProductRepository productRepository) {
    this.confirmationRepository = confirmationRepository;
    this.productRepository = productRepository;
  }

  public Single<List<Payment>> getProductPayments(Context context, AptoideProduct product) {
    return productRepository.getPayments(context, product);
  }

  public Single<Purchase> getPurchase(AptoideProduct product) {
    return productRepository.getPurchase(product)
        .flatMap(purchase -> Single.<Purchase>error(new PaymentAlreadyProcessedException(
            "Product " + product.getId() + " already purchased.")))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return getOngoingPurchase(product);
          }
          return Single.<Purchase>error(throwable);
        });
  }

  public Single<Purchase> pay(Payment payment) {
    return payment.process().andThen(productRepository.getPurchase(
        (AptoideProduct) payment.getProduct()));
  }

  private Single<Purchase> getOngoingPurchase(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product)
        .distinctUntilChanged(paymentConfirmation -> paymentConfirmation.getStatus())
        .first(paymentConfirmation -> paymentConfirmation.isCompleted() || paymentConfirmation.isFailed())
        .flatMap(paymentConfirmation -> {
          if (paymentConfirmation.isFailed()) {
            return Observable.just(null);
          }
          return productRepository.getPurchase(product).toObservable();
        })
        .toSingle();
  }
}