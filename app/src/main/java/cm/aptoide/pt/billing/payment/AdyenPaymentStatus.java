package cm.aptoide.pt.billing.payment;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentDetailsCallback;
import com.adyen.core.interfaces.PaymentMethodCallback;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import java.util.List;

public class AdyenPaymentStatus {

  private final String token;
  private final PaymentDataCallback dataCallback;
  private final PaymentRequestResult result;
  private final PaymentMethodCallback serviceCallback;
  private final List<PaymentMethod> recurringServices;
  private final List<PaymentMethod> services;
  private final PaymentDetailsCallback detailsCallback;
  private final PaymentRequest paymentRequest;

  public AdyenPaymentStatus(String token, PaymentDataCallback dataCallback,
      PaymentRequestResult result, PaymentMethodCallback serviceCallback,
      List<PaymentMethod> recurringServices, List<PaymentMethod> services,
      PaymentDetailsCallback detailsCallback, PaymentRequest paymentRequest) {
    this.token = token;
    this.dataCallback = dataCallback;
    this.result = result;
    this.serviceCallback = serviceCallback;
    this.recurringServices = recurringServices;
    this.services = services;
    this.detailsCallback = detailsCallback;
    this.paymentRequest = paymentRequest;
  }

  public List<PaymentMethod> getServices() {
    return services;
  }

  public String getToken() {
    return token;
  }

  public PaymentDataCallback getDataCallback() {
    return dataCallback;
  }

  public PaymentRequestResult getResult() {
    return result;
  }

  public PaymentMethodCallback getServiceCallback() {
    return serviceCallback;
  }

  public PaymentDetailsCallback getDetailsCallback() {
    return detailsCallback;
  }

  public PaymentRequest getPaymentRequest() {
    return paymentRequest;
  }

  public List<PaymentMethod> getRecurringServices() {
    return recurringServices;
  }
}
