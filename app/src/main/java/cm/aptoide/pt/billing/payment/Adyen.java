package cm.aptoide.pt.billing.payment;

import android.content.Context;
import com.adyen.core.PaymentRequest;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import java.nio.charset.Charset;
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

  private Observable<AdyenPaymentStatus> paymentRequestStatus;
  private PaymentRequest paymentRequest;
  private OnSubscribePaymentRequest.PaymentDetails paymentDetails;
  private OnSubscribePaymentRequest.PaymentData paymentData;

  public Adyen(Context context, Charset dataCharset, Scheduler scheduler) {
    this.context = context;
    this.dataCharset = dataCharset;
    this.scheduler = scheduler;
  }

  public Single<String> createPaymentRequest() {
    cancelPreviousPaymentRequest();
    return paymentRequestStatus.flatMap(status -> {
      if (status.getToken() != null) {
        return Observable.just(status.getToken());
      }
      return Observable.empty();
    })
        .first()
        .toSingle();
  }

  private void cancelPreviousPaymentRequest() {

    if (paymentRequest != null) {
      paymentRequest.cancel();
    }

    paymentData = new OnSubscribePaymentRequest.PaymentData();
    paymentDetails = new OnSubscribePaymentRequest.PaymentDetails(Collections.emptyList(),
        Collections.emptyList());
    paymentRequest = new PaymentRequest(context, paymentData, paymentDetails);
    paymentRequestStatus = Observable.create(
        new OnSubscribePaymentRequest(paymentData, paymentDetails, paymentRequest))
        .publish(status -> status)
        .subscribeOn(scheduler);
    paymentRequest.start();
  }

  public Completable createCreditCardPayment(String session) {
    return Completable.defer(() -> {
      if (paymentData.getDataCallback() == null) {
        return Completable.error(
            new IllegalStateException("Could not obtain credit card payment service"));
      }
      paymentData.getDataCallback()
          .completionWithPaymentData(session.getBytes(dataCharset));
      return Completable.complete();
    });
  }

  public Completable finishPayment(CreditCardPaymentDetails details) {
    return Completable.defer(() -> {
      if (paymentDetails.getDetailsCallback() == null) {
        return Completable.error(new IllegalStateException(
            "Not possible to finish payment with details no callback available."));
      }
      paymentDetails.getDetailsCallback()
          .completionWithPaymentDetails(details);
      return Completable.complete();
    });
  }

  public Single<PaymentRequestResult> getPaymentResult() {
    return paymentRequestStatus.filter(status -> status.getResult() != null)
        .map(status -> status.getResult())
        .first()
        .toSingle();
  }

  public Single<PaymentRequest> getCreditCardPayment() {
    return paymentRequestStatus.flatMap(status -> {
      if (status.getPaymentRequest() != null) {
        return Observable.just(status.getPaymentRequest());
      }

      return completeWithRecurringPayment(status, status.getRecurringServices()).switchIfEmpty(
          completeWithPaymentType(status, status.getServices(), PaymentMethod.Type.CARD))
          .ignoreElements();
    })
        .first()
        .toSingle();
  }

  private Observable<PaymentRequest> completeWithPaymentType(AdyenPaymentStatus status,
      List<PaymentMethod> services, String paymentType) {
    return Observable.from(services)
        .filter(service -> paymentType.equals(service.getType()))
        .take(1)
        .flatMap(service -> {
          status.getServiceCallback()
              .completionWithPaymentMethod(service);
          return Observable.just(null);
        });
  }

  private Observable<PaymentRequest> completeWithRecurringPayment(AdyenPaymentStatus status,
      List<PaymentMethod> services) {
    return Observable.from(services)
        .take(1)
        .flatMap(service -> {
          status.getServiceCallback()
              .completionWithPaymentMethod(service);
          return Observable.just(null);
        });
  }
}
