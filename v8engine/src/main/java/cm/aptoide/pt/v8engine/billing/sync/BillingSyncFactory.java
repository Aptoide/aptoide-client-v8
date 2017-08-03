package cm.aptoide.pt.v8engine.billing.sync;

import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.v8engine.billing.authorization.AuthorizationService;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.transaction.TransactionService;
import cm.aptoide.pt.v8engine.sync.Sync;

public class BillingSyncFactory {

  private final Payer payer;
  private final BillingAnalytics analytics;
  private final TransactionService transactionService;
  private final AuthorizationService authorizationService;
  private final TransactionPersistence transactionPersistence;
  private final AuthorizationPersistence authorizationPersistence;

  public BillingSyncFactory(Payer payer, BillingAnalytics analytics,
      TransactionService transactionService, AuthorizationService authorizationService,
      TransactionPersistence transactionPersistence,
      AuthorizationPersistence authorizationPersistence) {
    this.payer = payer;
    this.analytics = analytics;
    this.transactionService = transactionService;
    this.authorizationService = authorizationService;
    this.transactionPersistence = transactionPersistence;
    this.authorizationPersistence = authorizationPersistence;
  }

  public Sync createAuthorizationSync(int paymentMethodId) {
    return new AuthorizationSync(paymentMethodId, payer, analytics, authorizationService,
        authorizationPersistence, true, true,
        BuildConfig.PAYMENT_AUTHORIZATION_SYNC_INTERVAL_MILLIS, 0);
  }

  public Sync createTransactionSync(Product product) {
    return new TransactionSync(product, transactionPersistence, payer, analytics,
        transactionService, true, true, BuildConfig.PAYMENT_TRANSACTION_SYNC_INTERVAL_MILLIS, 0);
  }
}
