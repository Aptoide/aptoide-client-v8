/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.v8engine.billing.PayPalTransaction;
import cm.aptoide.pt.v8engine.billing.Transaction;

public class TransactionFactory {

  public Transaction create(int productId, String paymentConfirmationId, Transaction.Status status,
      String payerId, int paymentMethodId) {
    return new PayPalTransaction(productId, payerId, paymentConfirmationId, status,
        paymentMethodId);
  }

  public Transaction create(int productId, Transaction.Status status, String payerId,
      int paymentMethodId) {
    return new Transaction(productId, payerId, status, paymentMethodId);
  }

  public Transaction map(int productId, TransactionResponse response, String payerId) {
    if (response.getLocalMetadata() != null) {
      return new PayPalTransaction(productId, payerId, response.getLocalMetadata(),
          Transaction.Status.valueOf(response.getTransactionStatus()),
          response.getPaymentMethodId());
    } else {
      return new Transaction(productId, payerId,
          Transaction.Status.valueOf(response.getTransactionStatus()),
          response.getPaymentMethodId());
    }
  }

  public PaymentConfirmation map(Transaction transaction) {
    if (transaction instanceof PayPalTransaction) {
      return new PaymentConfirmation(((PayPalTransaction) transaction).getPayPalConfirmationId(),
          transaction.getProductId(), transaction.getStatus()
          .name(), transaction.getPayerId(), transaction.getPaymentMethodId());
    } else {
      return new PaymentConfirmation(null, transaction.getProductId(), transaction.getStatus()
          .name(), transaction.getPayerId(), transaction.getPaymentMethodId());
    }
  }

  public Transaction map(PaymentConfirmation persistedTransaction) {
    if (persistedTransaction.getLocalMetadata() != null) {
      return new PayPalTransaction(persistedTransaction.getProductId(),
          persistedTransaction.getPayerId(), persistedTransaction.getLocalMetadata(),
          Transaction.Status.valueOf(persistedTransaction.getStatus()),
          persistedTransaction.getPaymentMethodId());
    } else {
      return new Transaction(persistedTransaction.getProductId(), persistedTransaction.getPayerId(),
          Transaction.Status.valueOf(persistedTransaction.getStatus()),
          persistedTransaction.getPaymentMethodId());
    }
  }
}
