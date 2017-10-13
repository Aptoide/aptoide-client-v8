/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.IdResolver;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import java.util.List;

public class TransactionMapperV7 {

  private final TransactionFactory transactionFactory;
  private final IdResolver idResolver;

  public TransactionMapperV7(TransactionFactory transactionFactory, IdResolver idResolver) {
    this.transactionFactory = transactionFactory;
    this.idResolver = idResolver;
  }

  public Transaction map(GetTransactionRequest.ResponseBody.Transaction response) {
    return transactionFactory.create(idResolver.generateTransactionId(response.getId()),
        String.valueOf(response.getUser()
            .getId()), idResolver.generateServiceId(response.getService()
            .getId()), idResolver.generateProductId(response.getProduct()
            .getId()), Transaction.Status.valueOf(response.getStatus()));
  }
}
