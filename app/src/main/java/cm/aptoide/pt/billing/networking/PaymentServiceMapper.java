/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.billing.networking;

import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.payment.Adyen;
import cm.aptoide.pt.billing.payment.AdyenPaymentService;
import cm.aptoide.pt.billing.payment.PaymentService;
import cm.aptoide.pt.crashreports.CrashLogger;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import java.util.ArrayList;
import java.util.List;

public class PaymentServiceMapper {

  public static final String PAYPAL = "PAYPAL";
  public static final String ADYEN = "ADYEN";

  private final CrashLogger crashLogger;
  private final BillingIdManager billingIdManager;
  private final Adyen adyen;
  private final int currentAPILevel;
  private final int minimumAPILevelAdyen;
  private final int minimumAPILevelPayPal;

  public PaymentServiceMapper(CrashLogger crashLogger, BillingIdManager billingIdManager,
      Adyen adyen, int currentAPILevel, int minimumAPILevelAdyen, int minimumAPILevelPayPal) {
    this.crashLogger = crashLogger;
    this.billingIdManager = billingIdManager;
    this.adyen = adyen;
    this.currentAPILevel = currentAPILevel;
    this.minimumAPILevelAdyen = minimumAPILevelAdyen;
    this.minimumAPILevelPayPal = minimumAPILevelPayPal;
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
        if (currentAPILevel >= minimumAPILevelPayPal) {
          return new PaymentService(billingIdManager.generateServiceId(response.getId()),
              response.getName(), response.getLabel(), response.getDescription(),
              response.getIcon());
        }
        throw new IllegalArgumentException(
            "PayPal not supported in Android API lower than " + minimumAPILevelPayPal);
      case ADYEN:
        if (currentAPILevel >= minimumAPILevelAdyen) {
          return new AdyenPaymentService(billingIdManager.generateServiceId(response.getId()),
              response.getName(), response.getLabel(), response.getDescription(),
              response.getIcon(), adyen);
        }
        throw new IllegalArgumentException(
            "Adyen not supported in Android API lower than " + minimumAPILevelAdyen);
      default:
        throw new IllegalArgumentException("Payment service not supported: " + response.getName());
    }
  }
}