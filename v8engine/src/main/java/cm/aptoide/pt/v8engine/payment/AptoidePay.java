/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.pt.v8engine.payment.exception.PaymentAlreadyProcessedException;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationRepository;
import cm.aptoide.pt.v8engine.repository.ProductRepository;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

import static rx.Observable.error;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class AptoidePay {

  private final PaymentConfirmationRepository confirmationRepository;
  private ProductRepository productRepository;

  public AptoidePay(PaymentConfirmationRepository confirmationRepository, ProductRepository productRepository) {
    this.confirmationRepository = confirmationRepository;
    this.productRepository = productRepository;
  }

  public Observable<List<Payment>> getProductPayments(Context context, AptoideProduct product) {
    return productRepository.getPayments(context, product);
  }

  public Observable<Purchase> getPurchase(AptoideProduct product) {
    return confirmationRepository.getPaymentConfirmation(product)
        .first(paymentConfirmation -> paymentConfirmation.isCompleted())
        .flatMap(paymentConfirmation -> productRepository.getPurchase(product))
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(null);
          }
          return Observable.error(throwable);
        });
  }

  public Observable<Purchase> pay(Payment payment) {
    return isProductAlreadyPurchased((AptoideProduct) payment.getProduct()).flatMap(alreadyPurchased -> {

      if (alreadyPurchased) {
        return Observable.<Purchase>error(new PaymentAlreadyProcessedException(
            "Product " + payment.getProduct().getId() + " already purchased."));
      }

      return confirmationRepository.getPaymentConfirmation(payment.getProduct())
          .onErrorResumeNext(confirmationThrowable -> {
            if (confirmationThrowable instanceof RepositoryItemNotFoundException) {
              return payment.process();
            }
            return Observable.<PaymentConfirmation>error(confirmationThrowable);
          })
          .first(paymentConfirmation -> paymentConfirmation.isCompleted())
          .flatMap(saved -> productRepository.getPurchase((AptoideProduct) payment.getProduct()));
    }).subscribeOn(Schedulers.computation());
  }

  private Observable<Boolean> isProductAlreadyPurchased(AptoideProduct product) {
    return productRepository.getPurchase(product)
        .map(purchase -> true)
        .onErrorResumeNext(throwable -> {
          if (throwable instanceof RepositoryItemNotFoundException) {
            return Observable.just(false);
          }
          return error(throwable);
        });
  }
}