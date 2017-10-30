package cm.aptoide.pt.billing.payment;

import android.content.Context;
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
import com.adyen.core.models.paymentdetails.PaymentDetails;
import com.jakewharton.rxrelay.PublishRelay;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

public class Adyen {

  private final Context context;
  private final Charset dataCharset;
  private final Scheduler scheduler;

  private PublishRelay<AdyenPaymentStatus> status;
  private PaymentRequest paymentRequest;
  private DetailsStatus detailsStatus;
  private PaymentStatus paymentStatus;

  public Adyen(Context context, Charset dataCharset, Scheduler scheduler,
      PublishRelay<AdyenPaymentStatus> paymentRequestStatus) {
    this.context = context;
    this.dataCharset = dataCharset;
    this.scheduler = scheduler;
    this.status = paymentRequestStatus;
  }

  public Single<String> createToken() {
    cancelPreviousToken();
    return getStatus().filter(status -> status.getToken() != null)
        .map(status -> status.getToken())
        .first()
        .toSingle();
  }

  public Completable createPayment(String session) {
    return getStatus().first()
        .toSingle()
        .flatMapCompletable(status -> {
          if (status.getDataCallback() == null) {
            return Completable.error(
                new IllegalStateException("Not possible to create payment no callback available."));
          }
          status.getDataCallback()
              .completionWithPaymentData(session.getBytes(dataCharset));
          return Completable.complete();
        });
  }

  public Completable selectPaymentService(PaymentMethod service) {
    return getStatus().first()
        .toSingle()
        .flatMapCompletable(status -> {
          if (status.getServiceCallback() == null) {
            return Completable.error(new IllegalStateException(
                "Not possible to select payment service no callback available."));
          }
          status.getServiceCallback()
              .completionWithPaymentMethod(service);
          return Completable.complete();
        });
  }

  public Completable finishPayment(PaymentDetails details) {
    return getStatus().first()
        .toSingle()
        .flatMapCompletable(status -> {
          if (status.getDetailsCallback() == null) {
            return Completable.error(new IllegalStateException(
                "Not possible to finish payment with details no callback available."));
          }
          status.getDetailsCallback()
              .completionWithPaymentDetails(details);
          return Completable.complete();
        });
  }

  public Single<PaymentRequestResult> getPaymentResult() {
    return getStatus().filter(status -> status.getResult() != null)
        .map(status -> status.getResult())
        .first()
        .toSingle();
  }

  public Single<PaymentRequest> getPaymentData() {
    return getStatus().filter(status -> status.getPaymentRequest() != null)
        .map(status -> status.getPaymentRequest())
        .first()
        .toSingle();
  }

  public Single<PaymentMethod> getCreditCardPaymentService() {
    return getStatus().flatMap(
        status -> getRecurringPaymentService(status.getRecurringServices()).switchIfEmpty(
            getPaymentService(status.getServices(), PaymentMethod.Type.CARD)))
        .first()
        .toSingle();
  }

  private Observable<PaymentMethod> getPaymentService(List<PaymentMethod> services,
      String paymentType) {
    return Observable.from(services)
        .filter(service -> paymentType.equals(service.getType()))
        .take(1);
  }

  private Observable<PaymentMethod> getRecurringPaymentService(List<PaymentMethod> services) {
    return Observable.from(services)
        .take(1);
  }

  private Observable<AdyenPaymentStatus> getStatus() {
    return status.startWith((AdyenPaymentStatus) null)
        .map(event -> new AdyenPaymentStatus(paymentStatus.getToken(),
            paymentStatus.getDataCallback(), paymentStatus.getResult(),
            detailsStatus.getServiceCallback(), detailsStatus.getRecurringServices(),
            detailsStatus.getServices(), detailsStatus.getDetailsCallback(),
            detailsStatus.getPaymentRequest()))
        .subscribeOn(scheduler);
  }

  private void cancelPreviousToken() {

    if (paymentRequest != null) {
      detailsStatus.clearStatus();
      paymentStatus.clearStatus();
      paymentRequest.cancel();
    }

    paymentStatus = new PaymentStatus(status);
    detailsStatus = new DetailsStatus(status, Collections.emptyList(), Collections.emptyList());
    paymentRequest = new PaymentRequest(context, paymentStatus, detailsStatus);
    paymentRequest.start();
  }

  public static class PaymentStatus implements PaymentRequestListener {

    private PublishRelay<AdyenPaymentStatus> status;
    private String token;
    private PaymentDataCallback dataCallback;
    private PaymentRequestResult result;

    public PaymentStatus(PublishRelay<AdyenPaymentStatus> status) {
      this.status = status;
    }

    @Override public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest,
        @NonNull String token, @NonNull PaymentDataCallback paymentDataCallback) {
      this.token = token;
      this.dataCallback = paymentDataCallback;
      notifyStatus();
    }

    @Override public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
        @NonNull PaymentRequestResult paymentRequestResult) {
      this.result = paymentRequestResult;
      notifyStatus();
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

    public void clearStatus() {
      this.status = null;
    }

    private void notifyStatus() {
      if (status != null) {
        this.status.call(null);
      }
    }
  }

  public static class DetailsStatus implements PaymentRequestDetailsListener {

    private PublishRelay<AdyenPaymentStatus> status;
    private PaymentMethodCallback serviceCallback;
    private List<PaymentMethod> services;
    private List<PaymentMethod> recurringServices;
    private PaymentDetailsCallback detailsCallback;
    private PaymentRequest paymentRequest;

    public DetailsStatus(PublishRelay<AdyenPaymentStatus> status, List<PaymentMethod> services,
        List<PaymentMethod> recurringServices) {
      this.status = status;
      this.services = services;
      this.recurringServices = recurringServices;
    }

    @Override public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull List<PaymentMethod> recurringServices, @NonNull List<PaymentMethod> otherServices,
        @NonNull PaymentMethodCallback paymentMethodCallback) {
      this.serviceCallback = paymentMethodCallback;
      this.recurringServices =
          recurringServices != null ? recurringServices : Collections.emptyList();
      this.services = otherServices != null ? otherServices : Collections.emptyList();
      notifyStatus();
    }

    @Override
    public void onRedirectRequired(@NonNull PaymentRequest paymentRequest, @NonNull String s,
        @NonNull UriCallback uriCallback) {

    }

    @Override public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
        @NonNull Collection<InputDetail> inputDetails,
        @NonNull PaymentDetailsCallback paymentDetailsCallback) {
      this.detailsCallback = paymentDetailsCallback;
      this.paymentRequest = paymentRequest;
      notifyStatus();
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

    public List<PaymentMethod> getRecurringServices() {
      return recurringServices;
    }

    public void clearStatus() {
      this.status = null;
    }

    private void notifyStatus() {
      if (status != null) {
        this.status.call(null);
      }
    }
  }
}
