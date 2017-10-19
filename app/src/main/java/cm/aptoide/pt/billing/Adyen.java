package cm.aptoide.pt.billing;

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
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.InputDetail;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import rx.Observable;
import rx.Single;

public class Adyen {

  private final Context context;
  private final BehaviorRelay<String> tokenRelay;
  private final BehaviorRelay<List<PaymentMethod>> servicesRelay;
  private final BehaviorRelay<PaymentRequest> inputRelay;
  private final BehaviorRelay<PaymentRequestResult> resultRelay;
  private final Charset dataCharset;

  private PaymentRequest paymentRequest;
  private PaymentMethodCallback serviceCallback;
  private PaymentDataCallback dataCallback;
  private PaymentDetailsCallback detailsCallback;

  public Adyen(Context context, BehaviorRelay<String> tokenRelay,
      BehaviorRelay<List<PaymentMethod>> servicesRelay, BehaviorRelay<PaymentRequest> inputRelay,
      BehaviorRelay<PaymentRequestResult> resultRelay, Charset dataCharset) {
    this.context = context;
    this.tokenRelay = tokenRelay;
    this.servicesRelay = servicesRelay;
    this.inputRelay = inputRelay;
    this.resultRelay = resultRelay;
    this.dataCharset = dataCharset;
  }

  public Single<String> getToken() {
    generateToken();
    return tokenRelay.first()
        .toSingle();
  }

  public Single<PaymentRequest> getPaymentRequest(String session) {
    generateCreditCardInput(session);
    return servicesRelay.first()
        .flatMap(services -> Observable.from(services)
            .filter(service -> PaymentMethod.Type.CARD.equals(service.getType()))
            .switchIfEmpty(Observable.error(
                new IllegalStateException("No credit card payment provided by Adyen"))))
        .doOnNext(service -> completionWithPaymentMethod(service))
        .flatMap(__ -> getInput())
        .first()
        .toSingle();
  }

  public Observable<PaymentRequestResult> getPaymentResult(CreditCardPaymentDetails details) {
    generatePaymentResult(details);
    return resultRelay;
  }

  private void completionWithPaymentMethod(PaymentMethod service) {
    serviceCallback.completionWithPaymentMethod(service);
  }

  private Observable<PaymentRequest> getInput() {
    return inputRelay;
  }

  private void generatePaymentResult(CreditCardPaymentDetails details) {
    detailsCallback.completionWithPaymentDetails(details);
  }

  private void generateToken() {
    if (paymentRequest != null) {
      paymentRequest.cancel();
    }

    paymentRequest = new PaymentRequest(context, new PaymentRequestListener() {
      @Override public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest,
          @NonNull String token, @NonNull PaymentDataCallback paymentDataCallback) {
        tokenRelay.call(token);
        dataCallback = paymentDataCallback;
      }

      @Override public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
          @NonNull PaymentRequestResult paymentRequestResult) {
        resultRelay.call(paymentRequestResult);
      }
    }, new PaymentRequestDetailsListener() {
      @Override public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
          @NonNull List<PaymentMethod> recurringServices,
          @NonNull List<PaymentMethod> otherServices,
          @NonNull PaymentMethodCallback paymentMethodCallback) {
        serviceCallback = paymentMethodCallback;
        recurringServices.addAll(otherServices);
        servicesRelay.call(recurringServices);
      }

      @Override
      public void onRedirectRequired(@NonNull PaymentRequest paymentRequest, @NonNull String s,
          @NonNull UriCallback uriCallback) {

      }

      @Override public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
          @NonNull Collection<InputDetail> inputDetails,
          @NonNull PaymentDetailsCallback paymentDetailsCallback) {
        inputRelay.call(paymentRequest);
        detailsCallback = paymentDetailsCallback;
      }
    });
    paymentRequest.start();
  }

  private void generateCreditCardInput(String session) {
    dataCallback.completionWithPaymentData(session.getBytes(dataCharset));
  }
}
