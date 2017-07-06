package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import java.util.List;
import rx.Observable;

public interface TransactionPersistence {

  Observable<List<PaymentConfirmation>> getTransaction(int productId, String payerId);

  void removeTransaction(int productId);

  void removeAllTransactions();

  void saveTransaction(PaymentConfirmation transaction);
}
