/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.v8engine.billing.transaction.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.transaction.mol.MolTransaction;

public class TransactionMapper {

  private final TransactionFactory transactionFactory;

  public TransactionMapper(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(String productId, TransactionResponse response, String payerId,
      String payload, String sellerId) {
    return transactionFactory.create(sellerId, payerId, response.getPaymentMethodId(), productId,
        Transaction.Status.valueOf(response.getTransactionStatus()), response.getLocalMetadata(),
        response.getConfirmationUrl(), response.getSuccessUrl(), response.getClientToken(),
        payload);
  }

  public PaymentConfirmation map(Transaction transaction, String id) {
    String metadata = null;
    String confirmationUrl = null;
    String successUrl = null;
    String clientToken = null;

    if (transaction instanceof BraintreeTransaction) {
      clientToken = ((BraintreeTransaction) transaction).getToken();
    }

    {
      if (transaction instanceof LocalTransaction) {
        metadata = ((LocalTransaction) transaction).getLocalMetadata();
      }
    }

    if (transaction instanceof MolTransaction) {
      confirmationUrl = ((MolTransaction) transaction).getConfirmationUrl();
      successUrl = ((MolTransaction) transaction).getSuccessUrl();
    }

    return new PaymentConfirmation(id, metadata, transaction.getProductId(),
        transaction.getSellerId(), transaction.getStatus()
        .name(), transaction.getPayerId(), transaction.getPaymentMethodId(), confirmationUrl,
        successUrl, clientToken, transaction.getPayload());
  }

  public Transaction map(PaymentConfirmation persistedTransaction) {
    return transactionFactory.create(persistedTransaction.getSellerId(),
        persistedTransaction.getPayerId(), persistedTransaction.getPaymentMethodId(),
        persistedTransaction.getProductId(),
        Transaction.Status.valueOf(persistedTransaction.getStatus()),
        persistedTransaction.getLocalMetadata(), persistedTransaction.getConfirmationUrl(),
        persistedTransaction.getSuccessUrl(), persistedTransaction.getClientToken(),
        persistedTransaction.getPayload());
  }
}
