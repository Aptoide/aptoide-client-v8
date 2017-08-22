package cm.aptoide.pt.social.data.viewedposts;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.PostReadRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;

/**
 * Created by trinkes on 09/08/2017.
 */

public class PostReadReporter {
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient okhttp;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public PostReadReporter(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient okhttp,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.bodyInterceptor = bodyInterceptor;
    this.okhttp = okhttp;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Completable postRead(String cardId, String cardType) {
    return PostReadRequest.of(cardId, cardType, bodyInterceptor, okhttp, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe()
        .toCompletable();
  }
}
