package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 27/02/2017.
 */

public class GetUserRequestFactory {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Resources resources;
  private final WindowManager windowManager;

  public GetUserRequestFactory(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.windowManager = windowManager;
  }

  public GetUserRequest newGetUser(String url) {
    return GetUserRequest.of(url, bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences, resources, windowManager);
  }
}
