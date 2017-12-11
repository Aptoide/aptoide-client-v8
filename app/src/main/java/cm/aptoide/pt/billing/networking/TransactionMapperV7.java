/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;

public class TransactionMapperV7 {

  private final TransactionFactory transactionFactory;
  private final BillingIdManager billingIdManager;

  public TransactionMapperV7(TransactionFactory transactionFactory,
      BillingIdManager billingIdManager) {
    this.transactionFactory = transactionFactory;
    this.billingIdManager = billingIdManager;
  }

  public Transaction map(GetTransactionRequest.ResponseBody.Transaction response) {
    return transactionFactory.create(billingIdManager.generateTransactionId(response.getId()),
        String.valueOf(response.getUser()
            .getId()), billingIdManager.generateProductId(response.getProduct()
            .getId()), Transaction.Status.valueOf(response.getStatus()));
  }
}
