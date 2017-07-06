/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import java.util.List;
import rx.Observable;

public class TransactionAccessor implements TransactionPersistence {

  private final Database database;

  public TransactionAccessor(Database database) {
    this.database = database;
  }

  @Override
  public Observable<List<PaymentConfirmation>> getTransaction(int productId, String payerId) {
    return database.getRealm()
        .map(realm -> realm.where(PaymentConfirmation.class)
            .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
            .equalTo(PaymentConfirmation.PAYER_ID, payerId))
        .flatMap(query -> database.findAsList(query));
  }

  @Override public void removeTransaction(int productId) {
    database.delete(PaymentConfirmation.class, PaymentConfirmation.PRODUCT_ID, productId);
  }

  @Override public void removeAllTransactions() {
    database.deleteAll(PaymentConfirmation.class);
  }

  @Override public void saveTransaction(PaymentConfirmation transaction) {
    database.insert(transaction);
  }
}