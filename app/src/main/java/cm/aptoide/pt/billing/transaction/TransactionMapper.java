/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionsRequest;
import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

  private final TransactionFactory transactionFactory;

  public TransactionMapper(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(GetTransactionsRequest.ResponseBody.Data.Transaction response) {
    return transactionFactory.create(response.getId(), String.valueOf(response.getUser()
        .getId()), response.getService()
        .getId(), response.getProduct()
        .getId(), Transaction.Status.valueOf(response.getStatus()));
  }

  public List<Transaction> map(
      List<GetTransactionsRequest.ResponseBody.Data.Transaction> responseList) {

    final List<Transaction> transactions = new ArrayList<>(responseList.size());

    for (GetTransactionsRequest.ResponseBody.Data.Transaction response : responseList) {
      transactions.add(map(response));
    }
    return transactions;
  }
}
