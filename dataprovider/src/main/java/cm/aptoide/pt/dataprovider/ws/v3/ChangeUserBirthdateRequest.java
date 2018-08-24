package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by D01 on 21/08/2018.
 */

public class ChangeUserBirthdateRequest extends V3<BaseV3Response> {

  private ChangeUserBirthdateRequest(BaseBody baseBody, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static ChangeUserBirthdateRequest of(String birthdate,
      BodyInterceptor<BaseBody> bodyInterceptor, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final BaseBody body = new BaseBody();
    body.put("birthdate", birthdate);
    body.put("mode", "json");

    return new ChangeUserBirthdateRequest(body, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences);
  }

  @Override
  protected Observable<BaseV3Response> loadDataFromNetwork(Service service, boolean bypassCache) {
    return service.changeUserBirthdate(map, bypassCache);
  }
}
