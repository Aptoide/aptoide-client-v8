package cm.aptoide.pt.impressions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.MarkAsReadRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

public class ImpressionService {
  private final BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private final OkHttpClient okHttpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final Converter.Factory converterFactory;

  public ImpressionService(BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Converter.Factory converterFactory) {
    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
    this.okHttpClient = okHttpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.converterFactory = converterFactory;
  }

  public Completable markAsRead(String id, boolean dismiss) {
    return new MarkAsReadRequest(new MarkAsReadRequest.Body(id, dismiss), okHttpClient,
        converterFactory, bodyInterceptorPoolV7, tokenInvalidator, sharedPreferences).observe()
        .toCompletable()
        .onErrorComplete();
  }
}
