/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.methods.braintree.BraintreeCreditCard;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompra;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolPoints;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPal;
import cm.aptoide.pt.v8engine.billing.methods.sandbox.Sandbox;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;

public class PaymentMethodMapper {

  public static final int PAYPAL = 1;
  public static final int BOA_COMPRA = 7;
  public static final int SANDBOX = 8;
  public static final int BOA_COMPRA_GOLD = 9;
  public static final int MOL_POINTS = 10;
  public static final int BRAINTREE_CREDIT_CARD = 11;

  private final TransactionRepository transactionRepository;
  private final AuthorizationRepository authorizationRepository;

  public PaymentMethodMapper(TransactionRepository transactionRepository,
      AuthorizationRepository authorizationRepository) {
    this.transactionRepository = transactionRepository;
    this.authorizationRepository = authorizationRepository;
  }

  public PaymentMethod map(PaymentServiceResponse paymentService) {
    switch (paymentService.getId()) {
      case PAYPAL:
        return new PayPal(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
        return new BoaCompra(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository, authorizationRepository);
      case MOL_POINTS:
        return new MolPoints(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      case BRAINTREE_CREDIT_CARD:
        return new BraintreeCreditCard(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      case SANDBOX:
        return new Sandbox(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      default:
        throw new IllegalArgumentException("Payment not supported: " + paymentService.getName());
    }
  }
}