/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 9/1/16.
 */
public class PaymentAccessor extends SimpleAccessor<PaymentConfirmation> {

  protected PaymentAccessor(Database database) {
    super(database, PaymentConfirmation.class);
  }

  public Observable<PaymentConfirmation> getPaymentConfirmation(int productId) {
    return database.get(PaymentConfirmation.class, PaymentConfirmation.PRODUCT_ID, productId);
  }

  public Observable<List<PaymentConfirmation>> getPaymentConfirmations() {
    return database.getAll(PaymentConfirmation.class);
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(int paymentId) {
    return database.get(PaymentAuthorization.class, PaymentAuthorization.PAYMENT_ID, paymentId);
  }

  public Observable<List<PaymentAuthorization>> getPaymentAuthorizations() {
    return database.getAll(PaymentAuthorization.class);
  }

  public void delete(String paymentConfirmationId) {
    database.delete(PaymentConfirmation.class, PaymentConfirmation.PAYMENT_CONFIRMATION_ID,
        paymentConfirmationId);
  }

  public void save(PaymentConfirmation paymentConfirmation) {
    database.insert(paymentConfirmation);
  }

  public void save(PaymentAuthorization paymentAuthorization) {
    database.insert(paymentAuthorization);
  }

  public void deleteAuthorization(int paymentId) {
    database.delete(PaymentAuthorization.class, PaymentAuthorization.PAYMENT_ID, paymentId);
  }
}