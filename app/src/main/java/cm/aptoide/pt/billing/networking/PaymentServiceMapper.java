/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.billing.payment.TokenPaymentService;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import java.util.ArrayList;
import java.util.List;

public class PaymentServiceMapper {

  public static final String PAYPAL = "PAYPAL";
  public static final String BOA_COMPRA = "BOA_COMPRA";
  public static final String SANDBOX = "SANDBOX";
  public static final String BOA_COMPRA_GOLD = "BOA_COMPRA_GOLD";
  public static final String MOL_POINTS = "MOLPOINTS";
  public static final String ADYEN = "ADYEN";

  private final CrashLogger crashLogger;
  private final BillingIdManager billingIdManager;
  private final Adyen adyen;

  public PaymentServiceMapper(CrashLogger crashLogger, BillingIdManager billingIdManager, Adyen adyen) {
    this.crashLogger = crashLogger;
    this.billingIdManager = billingIdManager;
    this.adyen = adyen;
  }

  public List<PaymentService> map(List<GetServicesRequest.ResponseBody.Service> responseList) {

    final List<PaymentService> paymentServices = new ArrayList<>(responseList.size());
    for (GetServicesRequest.ResponseBody.Service service : responseList) {
      try {
        paymentServices.add(map(service));
      } catch (IllegalArgumentException exception) {
        crashLogger.log(exception);
      }
    }
    return paymentServices;
  }

  private PaymentService map(GetServicesRequest.ResponseBody.Service response) {
    switch (response.getName()) {
      case PAYPAL:
      case BOA_COMPRA:
      case BOA_COMPRA_GOLD:
      case MOL_POINTS:
      case SANDBOX:
        return new PaymentService(billingIdManager.generateServiceId(response.getId()),
            response.getName(), response.getLabel(), response.getDescription(), response.getIcon());
      case ADYEN:
        return new TokenPaymentService(billingIdManager.generateServiceId(response.getId()),
            response.getName(), response.getLabel(), response.getDescription(), response.getIcon(),
            adyen);
      default:
        throw new IllegalArgumentException("Payment service not supported: " + response.getName());
    }
  }
}