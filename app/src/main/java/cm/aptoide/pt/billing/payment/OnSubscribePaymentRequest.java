package cm.aptoide.pt.billing.payment;

import android.support.annotation.NonNull;
import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentDetailsCallback;
import com.adyen.core.interfaces.PaymentMethodCallback;
import com.adyen.core.interfaces.PaymentRequestDetailsListener;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.interfaces.UriCallback;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.models.paymentdetails.InputDetail;
import java.util.Collection;
import java.util.List;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class OnSubscribePaymentRequest extends SyncOnSubscribe<PaymentRequest, AdyenPaymentStatus> {

  private PaymentData paymentData;
  private PaymentDetails paymentDetails;
  private PaymentRequest paymentRequest;

  public OnSubscribePaymentRequest(PaymentData paymentData, PaymentDetails paymentDetails,
      PaymentRequest paymentRequest) {
    this.paymentData = paymentData;
    this.paymentDetails = paymentDetails;
    this.paymentRequest = paymentRequest;
  }

  @Override protected PaymentRequest generateState() {
    return paymentRequest;
  }

  @Override protected PaymentRequest next(PaymentRequest paymentRequest,
      Observer<? super AdyenPaymentStatus> observer) {

    if (paymentDetails.getPaymentRequest() != null) {
      paymentRequest = paymentDetails.getPaymentRequest();
    }

    observer.onNext(new AdyenPaymentStatus(paymentData.getToken(), paymentData.getDataCallback(),
        paymentData.getResult(), paymentDetails.getServiceCallback(), paymentDetails.getServices(),
        paymentDetails.getDetailsCallback(), paymentDetails.getPaymentRequest()));

    return paymentRequest;
  }

  public static class PaymentData implements PaymentRequestListener {

    private String token;
    private PaymentDataCallback dataCallback;
    private PaymentRequestResult result;

    @Override public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest,
        @NonNull String token, @NonNull PaymentDataCallback paymentDataCallback) {
      this.token = token;
      this.dataCallback = paymentDataCallback;
    }

    @Override public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
        @NonNull PaymentRequestResult paymentRequestResult) {
      this.result = paymentRequestResult;
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
  }

  public static class PaymentDetails implements PaymentRequestDetailsListener {

    private PaymentMethodCallback serviceCallback;
    private List<PaymentMethod> services;
    private PaymentDetailsCallback detailsCallback;
    private PaymentRequest paymentRequest;

    @Override public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull List<PaymentMethod> recurringServices, @NonNull List<PaymentMethod> otherServices,
        @NonNull PaymentMethodCallback paymentMethodCallback) {
      this.serviceCallback = paymentMethodCallback;
      recurringServices.addAll(otherServices);
      this.services = recurringServices;
    }

    @Override
    public void onRedirectRequired(@NonNull PaymentRequest paymentRequest, @NonNull String s,
        @NonNull UriCallback uriCallback) {

    }

    @Override public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull Collection<InputDetail> inputDetails,
        @NonNull PaymentDetailsCallback paymentDetailsCallback) {
      this.paymentRequest = paymentRequest;
      this.detailsCallback = paymentDetailsCallback;
    }

    public PaymentMethodCallback getServiceCallback() {
      return serviceCallback;
    }

    public List<PaymentMethod> getServices() {
      return services;
    }

    public PaymentDetailsCallback getDetailsCallback() {
      return detailsCallback;
    }

    public PaymentRequest getPaymentRequest() {
      return paymentRequest;
    }
  }
}
