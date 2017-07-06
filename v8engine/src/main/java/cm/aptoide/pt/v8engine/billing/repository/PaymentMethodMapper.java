/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.repository;

import cm.aptoide.pt.dataprovider.model.v3.PaymentServiceResponse;
import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.boacompra.BoaCompraPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.mol.MolPointsPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.paypal.PayPalPaymentMethod;
import cm.aptoide.pt.v8engine.billing.methods.sandbox.SandboxPaymentMethod;

public class PaymentMethodMapper {

  private static final String PAYPAL = "paypal";
  private static final String BOA_COMPRA = "boacompra";
  private static final String BOA_COMPRA_GOLD = "boacompragold";
  private static final String MOL_POINTS = "molpoints";
  private static final String SANDBOX = "dummy";

  private final TransactionRepositorySelector transactionRepositorySelector;
  private final AuthorizationRepository authorizationRepository;

  public PaymentMethodMapper(TransactionRepositorySelector transactionRepositorySelector,
      AuthorizationRepository authorizationRepository) {
    this.transactionRepositorySelector = transactionRepositorySelector;
    this.authorizationRepository = authorizationRepository;
  }

  public PaymentMethod map(PaymentServiceResponse paymentService) {
    switch (paymentService.getShortName()) {
      case PAYPAL:
        return new PayPalPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepositorySelector);
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
        return new BoaCompraPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepositorySelector,
            authorizationRepository);
      case MOL_POINTS:
        return new MolPointsPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepositorySelector);
      case SANDBOX:
        return new SandboxPaymentMethod(paymentService.getId(), paymentService.getName(),
            paymentService.getDescription(), transactionRepositorySelector);
      default:
        throw new IllegalArgumentException(
            "Payment not supported: " + paymentService.getShortName());
    }
  }
}