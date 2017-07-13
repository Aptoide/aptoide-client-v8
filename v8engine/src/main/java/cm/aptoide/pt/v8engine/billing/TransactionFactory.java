package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.v8engine.billing.methods.braintree.BraintreeTransaction;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolTransaction;

public class TransactionFactory {

  public Transaction create(int productId, String payerId, Transaction.Status status,
      int paymentMethodId, String metadata, String confirmationUrl, String successUrl,
      String clientToken) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.PAYPAL:
      case PaymentMethodMapper.BRAINTREE_CREDIT_CARD:
        if (clientToken == null) {
          return new LocalTransaction(productId, payerId, status, paymentMethodId, metadata);
        }
        return new BraintreeTransaction(productId, payerId, status, paymentMethodId, clientToken);
      case PaymentMethodMapper.MOL_POINTS:
        return new MolTransaction(productId, payerId, status, paymentMethodId, confirmationUrl,
            successUrl);
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Transaction(productId, payerId, status, paymentMethodId);
    }
  }
}