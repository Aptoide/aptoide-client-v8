/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolTransaction;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalTransaction;

public class TransactionFactory {

  public Transaction createNewTransaction(int productId, String payerId) {
    return getTransaction(productId, payerId, Transaction.Status.NEW, -1, null, null, null);
  }

  public Transaction create(int productId, Transaction.Status status, String payerId,
      int paymentMethodId, String metadata) {
    return getTransaction(productId, payerId, status, paymentMethodId, metadata, null, null);
  }

  public Transaction map(int productId, TransactionResponse response, String payerId) {
    return getTransaction(productId, payerId,
        Transaction.Status.valueOf(response.getTransactionStatus()), response.getPaymentMethodId(),
        response.getLocalMetadata(), response.getConfirmationUrl(), response.getSuccessUrl());
  }

  public PaymentConfirmation map(Transaction transaction) {
    String metadata = null;
    String confirmationUrl = null;
    String successUrl = null;
    if (transaction instanceof PayPalTransaction) {
      metadata = ((PayPalTransaction) transaction).getPayPalConfirmationId();
    }

    if (transaction instanceof MolTransaction) {
      confirmationUrl = ((MolTransaction) transaction).getConfirmationUrl();
      successUrl = ((MolTransaction) transaction).getSuccessUrl();
    }

    return new PaymentConfirmation(metadata, transaction.getProductId(), transaction.getStatus()
        .name(), transaction.getPayerId(), transaction.getPaymentMethodId(), confirmationUrl,
        successUrl);
  }

  public Transaction map(PaymentConfirmation persistedTransaction) {
    return getTransaction(persistedTransaction.getProductId(), persistedTransaction.getPayerId(),
        Transaction.Status.valueOf(persistedTransaction.getStatus()),
        persistedTransaction.getPaymentMethodId(), persistedTransaction.getLocalMetadata(),
        persistedTransaction.getConfirmationUrl(), persistedTransaction.getSuccessUrl());
  }

  private Transaction getTransaction(int productId, String payerId, Transaction.Status status,
      int paymentMethodId, String metadata, String confirmationUrl, String successUrl) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.PAYPAL:
        return new PayPalTransaction(productId, payerId, metadata, status, paymentMethodId);
      case PaymentMethodMapper.MOL_POINTS:
        return new MolTransaction(productId, payerId,
            (status.equals(Transaction.Status.PENDING) ? Transaction.Status.PENDING_USER_AUTHORIZATION
                : status), paymentMethodId, confirmationUrl, successUrl);
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Transaction(productId, payerId, status, paymentMethodId);
    }
  }
}
