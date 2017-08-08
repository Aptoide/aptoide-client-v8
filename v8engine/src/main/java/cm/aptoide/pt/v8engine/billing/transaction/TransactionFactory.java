package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.v8engine.billing.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.transaction.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.transaction.mol.MolTransaction;

public class TransactionFactory {

  public Transaction create(String productId, String payerId, Transaction.Status status,
      int paymentMethodId, String metadata, String confirmationUrl, String successUrl,
      String clientToken, String payload) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.PAYPAL:
      case PaymentMethodMapper.BRAINTREE_CREDIT_CARD:
        if (clientToken == null) {
          return new LocalTransaction(productId, payerId, status, paymentMethodId, metadata,
              payload);
        }
        return new BraintreeTransaction(productId, payerId, status, paymentMethodId, clientToken,
            payload);
      case PaymentMethodMapper.MOL_POINTS:
        return new MolTransaction(productId, payerId, status, paymentMethodId, confirmationUrl,
            successUrl, payload);
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Transaction(productId, payerId, status, paymentMethodId, payload);
    }
  }
}