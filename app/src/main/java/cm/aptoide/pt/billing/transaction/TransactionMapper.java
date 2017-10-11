/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

  private final TransactionFactory transactionFactory;

  public TransactionMapper(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(GetTransactionRequest.ResponseBody.Transaction response) {
    return transactionFactory.create(response.getId(), String.valueOf(response.getUser()
        .getId()), response.getService()
        .getId(), response.getProduct()
        .getId(), Transaction.Status.valueOf(response.getStatus()));
  }

  public List<Transaction> map(List<GetTransactionRequest.ResponseBody.Transaction> responseList) {

    final List<Transaction> transactions = new ArrayList<>(responseList.size());

    for (GetTransactionRequest.ResponseBody.Transaction response : responseList) {
      transactions.add(map(response));
    }
    return transactions;
  }

  public Transaction map(String customerId, long productId, TransactionResponse response) {

    if (response.hasErrors()) {
      return getErrorTransaction(response.getErrors(), customerId, productId, -1);
    }

    return transactionFactory.create(productId, customerId, response.getServiceId(), productId,
        Transaction.Status.valueOf(response.getStatus()));
  }

  private Transaction getErrorTransaction(List<ErrorResponse> errors, String customerId,
      long productId, int serviceId) {

    Transaction transaction = transactionFactory.create(productId, customerId, serviceId, productId,
        Transaction.Status.FAILED);

    if (errors == null || errors.isEmpty()) {
      return transaction;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code) || "PRODUCT-209".equals(error.code)) {
      transaction = transactionFactory.create(productId, customerId, serviceId, productId,
          Transaction.Status.PENDING_SERVICE_AUTHORIZATION);
    }

    if ("PRODUCT-200".equals(error.code)) {
      transaction = transactionFactory.create(productId, customerId, serviceId, productId,
          Transaction.Status.COMPLETED);
    }

    if ("PRODUCT-214".equals(error.code)) {
      transaction = transactionFactory.create(productId, customerId, serviceId, productId,
          Transaction.Status.NEW);
    }

    if ("PRODUCT-216".equals(error.code)) {
      transaction = transactionFactory.create(productId, customerId, serviceId, productId,
          Transaction.Status.PROCESSING);
    }

    if ("PRODUCT-7".equals(error.code)
        || "PRODUCT-8".equals(error.code)
        || "PRODUCT-9".equals(error.code)
        || "PRODUCT-102".equals(error.code)
        || "PRODUCT-104".equals(error.code)
        || "PRODUCT-206".equals(error.code)
        || "PRODUCT-207".equals(error.code)
        || "PRODUCT-208".equals(error.code)
        || "PRODUCT-215".equals(error.code)
        || "PRODUCT-217".equals(error.code)) {
      transaction = transactionFactory.create(productId, customerId, serviceId, productId,
          Transaction.Status.FAILED);
    }

    return transaction;
  }
}
