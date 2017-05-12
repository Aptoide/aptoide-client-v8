/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/11/2016.
 */

package cm.aptoide.pt.database.accessors;

import cm.aptoide.pt.database.realm.PaymentAuthorization;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 23/11/16.
 */

public class PaymentAuthorizationAccessor extends SimpleAccessor<PaymentAuthorization> {

  public PaymentAuthorizationAccessor(Database db) {
    super(db, PaymentAuthorization.class);
  }

  public Observable<PaymentAuthorization> getPaymentAuthorization(String payerId, int paymentId) {
    return database.getRealm()
        .map(realm -> realm.where(PaymentAuthorization.class)
            .equalTo(PaymentAuthorization.PAYER_ID, payerId)
            .equalTo(PaymentAuthorization.PAYMENT_ID, paymentId))
        .flatMap(query -> database.findAsList(query)
            .flatMapIterable(authorizations -> authorizations));
  }

  public void save(PaymentAuthorization paymentAuthorization) {
    database.insert(paymentAuthorization);
  }
}
