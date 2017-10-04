package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.billing.PaymentMethodMapper;
import cm.aptoide.pt.billing.transaction.braintree.BraintreeTransaction;
import cm.aptoide.pt.billing.transaction.mol.MolTransaction;

public class TransactionFactory {

  public Transaction create(String sellerId, String customerId, int paymentMethodId, String productId,
      Transaction.Status status, String metadata, String confirmationUrl, String successUrl,
      String clientToken, String payload) {
    switch (paymentMethodId) {
      case PaymentMethodMapper.PAYPAL:
      case PaymentMethodMapper.BRAINTREE_CREDIT_CARD:
        if (clientToken == null) {
          return new LocalTransaction(productId, customerId, status, paymentMethodId, metadata,
              payload, sellerId);
        }
        return new BraintreeTransaction(productId, customerId, status, paymentMethodId, clientToken,
            payload, sellerId);
      case PaymentMethodMapper.MOL_POINTS:
        return new MolTransaction(productId, customerId, status, paymentMethodId, confirmationUrl,
            successUrl, payload, sellerId);
      case PaymentMethodMapper.BOA_COMPRA:
      case PaymentMethodMapper.BOA_COMPRA_GOLD:
      case PaymentMethodMapper.SANDBOX:
      default:
        return new Transaction(productId, customerId, status, paymentMethodId, payload, sellerId);
    }
  }
}