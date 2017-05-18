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

  public Observable<List<PaymentConfirmation>> getPaymentConfirmations(int productId,
      String payerId) {
    return database.getRealm()
        .map(realm -> realm.where(PaymentConfirmation.class)
            .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
            .equalTo(PaymentConfirmation.PAYER_ID, payerId))
        .flatMap(query -> database.findAsList(query));
  }

  public void save(PaymentConfirmation confirmation) {
    database.insert(confirmation);
  }

  public void remove(int productId) {
    database.delete(PaymentConfirmation.class, PaymentConfirmation.PRODUCT_ID, productId);
  }
}