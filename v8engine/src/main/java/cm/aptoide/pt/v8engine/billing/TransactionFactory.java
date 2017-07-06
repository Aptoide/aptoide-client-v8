/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.v8engine.billing.Transaction;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolTransaction;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalTransaction;

public class TransactionFactory {

  public Transaction create(int productId, String paymentConfirmationId, Transaction.Status status,
      String payerId, int paymentMethodId) {
    return new PayPalTransaction(productId, payerId, paymentConfirmationId, status,
        paymentMethodId);
  }

  public Transaction create(int productId, String payerId) {
    return new Transaction(productId, payerId, Transaction.Status.NEW, -1);
  }

  public Transaction map(int productId, TransactionResponse response, String payerId) {
    final Transaction.Status status = Transaction.Status.valueOf(response.getTransactionStatus());
    if (response.getLocalMetadata() != null) {
      return new PayPalTransaction(productId, payerId, response.getLocalMetadata(), status,
          response.getPaymentMethodId());
    } else if (response.getConfirmationUrl() != null && response.getSuccessUrl() != null) {
      return new MolTransaction(productId, payerId,
          (status.equals(Transaction.Status.PENDING) ? Transaction.Status.PENDING_AUTHORIZATION
              : status), response.getPaymentMethodId(), response.getConfirmationUrl(),
          response.getSuccessUrl());
    } else {
      return new Transaction(productId, payerId, status, response.getPaymentMethodId());
    }
  }

  public PaymentConfirmation map(Transaction transaction) {
    if (transaction instanceof PayPalTransaction) {
      return new PaymentConfirmation(((PayPalTransaction) transaction).getPayPalConfirmationId(),
          transaction.getProductId(), transaction.getStatus()
          .name(), transaction.getPayerId(), transaction.getPaymentMethodId(), null, null);
    } else if (transaction instanceof MolTransaction) {
      return new PaymentConfirmation(null, transaction.getProductId(), transaction.getStatus()
          .name(), transaction.getPayerId(), transaction.getPaymentMethodId(),
          ((MolTransaction) transaction).getConfirmationUrl(),
          ((MolTransaction) transaction).getSuccessUrl());
    } else {
      return new PaymentConfirmation(null, transaction.getProductId(), transaction.getStatus()
          .name(), transaction.getPayerId(), transaction.getPaymentMethodId(), null, null);
    }
  }

  public Transaction map(PaymentConfirmation persistedTransaction) {
    if (persistedTransaction.getLocalMetadata() != null) {
      return new PayPalTransaction(persistedTransaction.getProductId(),
          persistedTransaction.getPayerId(), persistedTransaction.getLocalMetadata(),
          Transaction.Status.valueOf(persistedTransaction.getStatus()),
          persistedTransaction.getPaymentMethodId());
    } else if (persistedTransaction.getConfirmationUrl() != null
        && persistedTransaction.getSuccessUrl() != null) {
      return new MolTransaction(persistedTransaction.getProductId(),
          persistedTransaction.getPayerId(),
          Transaction.Status.valueOf(persistedTransaction.getStatus()),
          persistedTransaction.getPaymentMethodId(), persistedTransaction.getConfirmationUrl(),
          persistedTransaction.getSuccessUrl());
    } else {
      return new Transaction(persistedTransaction.getProductId(), persistedTransaction.getPayerId(),
          Transaction.Status.valueOf(persistedTransaction.getStatus()),
          persistedTransaction.getPaymentMethodId());
    }
  }
}
