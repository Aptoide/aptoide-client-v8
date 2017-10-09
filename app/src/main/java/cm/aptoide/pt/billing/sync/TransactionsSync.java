/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class TransactionsSync extends Sync {

  private final TransactionPersistence transactionPersistence;
  private final TransactionService transactionService;

  public TransactionsSync(String id, TransactionPersistence transactionPersistence,
      TransactionService transactionService, boolean periodic, boolean exact, long interval,
      long trigger) {
    super(id, periodic, exact, trigger, interval);
    this.transactionPersistence = transactionPersistence;
    this.transactionService = transactionService;
  }

  @Override public Completable execute() {
    return transactionService.getTransactions()
        .flatMapCompletable(transactions -> transactionPersistence.saveTransactions(transactions));
  }
}
