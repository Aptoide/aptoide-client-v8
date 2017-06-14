package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 06/12/2016.
 */
public class LikeCardRequest extends V7<BaseV7Response, BaseBody> {

  private static final String BASE_HOST = (ToolboxManager.isToolboxEnableHttpScheme() ? "http"
      : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";
  private final String cardId;
  private final int rating;

  public LikeCardRequest(BaseBody body, String cardId, int rating,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.cardId = cardId;
    this.rating = rating;
  }

  public static LikeCardRequest of(String timelineCardId, String cardType, String ownerHash,
      int rating, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator) {
    final BaseBody body = new BaseBody();
    return new LikeCardRequest(body, timelineCardId, rating, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReview(body, cardId, body.getAccessToken(), String.valueOf(rating), true);
  }
}
