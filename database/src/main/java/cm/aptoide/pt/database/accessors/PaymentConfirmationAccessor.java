/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 9/1/16.
 */
public class PaymentConfirmationAccessor extends SimpleAccessor<PaymentConfirmation> {

  protected PaymentConfirmationAccessor(Database database) {
    super(database, PaymentConfirmation.class);
  }

  public Observable<List<PaymentConfirmation>> getPaymentConfirmations(int productId) {
    return database.getAsList(PaymentConfirmation.class, PaymentConfirmation.PRODUCT_ID, productId);
  }

  public void save(PaymentConfirmation paymentConfirmation) {
    database.insert(paymentConfirmation);
  }
}