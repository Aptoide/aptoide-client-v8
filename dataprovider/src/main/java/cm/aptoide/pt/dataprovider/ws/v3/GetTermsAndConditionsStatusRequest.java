package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.TermsAndConditionsResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by franciscocalado on 06/09/2018.
 */

public class GetTermsAndConditionsStatusRequest extends V3<TermsAndConditionsResponse> {

  private GetTermsAndConditionsStatusRequest(BaseBody baseBody, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static GetTermsAndConditionsStatusRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      Converter.Factory converterFactory, OkHttpClient httpClient,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final BaseBody body = new BaseBody();

    return new GetTermsAndConditionsStatusRequest(body, httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<TermsAndConditionsResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getTermsAndConditionsStatus(map, bypassCache);
  }
}
