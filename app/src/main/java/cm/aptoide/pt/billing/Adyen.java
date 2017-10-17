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
import com.adyen.core.models.paymentdetails.InputDetail;
import com.jakewharton.rxrelay.BehaviorRelay;
import java.util.Collection;
import java.util.List;
import rx.Observable;

public class Adyen {

  private final Context context;
  private final BehaviorRelay<String> tokenRelay;

  private PaymentRequest paymentRequest;

  public Adyen(Context context, BehaviorRelay<String> tokenRelay) {
    this.context = context;
    this.tokenRelay = tokenRelay;
  }

  public void generateToken() {
    if (paymentRequest != null) {
      paymentRequest.cancel();
    }

    paymentRequest = new PaymentRequest(context, new PaymentRequestListener() {
      @Override public void onPaymentDataRequested(@NonNull PaymentRequest paymentRequest,
          @NonNull String token, @NonNull PaymentDataCallback paymentDataCallback) {
        tokenRelay.call(token);
      }

      @Override public void onPaymentResult(@NonNull PaymentRequest paymentRequest,
          @NonNull PaymentRequestResult paymentRequestResult) {
        if (paymentRequestResult.getError() != null) {
          tokenRelay.call(null);
        }
      }
    }, new PaymentRequestDetailsListener() {
      @Override public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
          @NonNull List<PaymentMethod> list, @NonNull List<PaymentMethod> list1,
          @NonNull PaymentMethodCallback paymentMethodCallback) {

      }

      @Override
      public void onRedirectRequired(@NonNull PaymentRequest paymentRequest, @NonNull String s,
          @NonNull UriCallback uriCallback) {

      }

      @Override public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
          @NonNull Collection<InputDetail> collection,
          @NonNull PaymentDetailsCallback paymentDetailsCallback) {

      }
    });
    paymentRequest.start();
  }

  public Observable<String> getToken() {
    return tokenRelay;
  }
}
