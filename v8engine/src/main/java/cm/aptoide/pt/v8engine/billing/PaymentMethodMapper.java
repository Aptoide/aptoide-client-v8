/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompraPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolPointsPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.sandbox.SandboxPaymentMethod;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationRepository;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;

public class PaymentMethodMapper {

  public static final int PAYPAL = 1;
  public static final int BOA_COMPRA = 7;
  public static final int SANDBOX = 8;
  public static final int BOA_COMPRA_GOLD = 9;
  public static final int MOL_POINTS = 10;

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
        return new PayPalPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
        return new BoaCompraPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository, authorizationRepository);
      case MOL_POINTS:
        return new MolPointsPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      case SANDBOX:
        return new SandboxPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepository);
      default:
        throw new IllegalArgumentException("Payment not supported: " + paymentService.getName());
    }
  }
}