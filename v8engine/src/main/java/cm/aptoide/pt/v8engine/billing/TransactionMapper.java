/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.v8engine.billing.methods.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolTransaction;

public class TransactionMapper {

  private final TransactionFactory transactionFactory;

  public TransactionMapper(TransactionFactory transactionFactory) {
    this.transactionFactory = transactionFactory;
  }

  public Transaction map(int productId, TransactionResponse response, String payerId) {
    return transactionFactory.create(productId, payerId,
        Transaction.Status.valueOf(response.getTransactionStatus()), response.getPaymentMethodId(),
        response.getLocalMetadata(), response.getConfirmationUrl(), response.getSuccessUrl(),
        response.getClientToken());
  }

  public PaymentConfirmation map(Transaction transaction) {
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

    return new PaymentConfirmation(metadata, transaction.getProductId(), transaction.getStatus()
        .name(), transaction.getPayerId(), transaction.getPaymentMethodId(), confirmationUrl,
        successUrl, clientToken);
  }

  public Transaction map(PaymentConfirmation persistedTransaction) {
    return transactionFactory.create(persistedTransaction.getProductId(),
        persistedTransaction.getPayerId(),
        Transaction.Status.valueOf(persistedTransaction.getStatus()),
        persistedTransaction.getPaymentMethodId(), persistedTransaction.getLocalMetadata(),
        persistedTransaction.getConfirmationUrl(), persistedTransaction.getSuccessUrl(),
        persistedTransaction.getClientToken());
  }
}
