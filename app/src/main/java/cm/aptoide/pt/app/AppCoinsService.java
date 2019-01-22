package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppCoinsCampaignsRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AppCoinsService {
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences preferences;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;

  public AppCoinsService(OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences preferences, BodyInterceptor<BaseBody> bodyInterceptor,
      Converter.Factory converterFactory) {
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.preferences = preferences;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
  }

  public Single<Boolean> isCampaignValid(String packageName, int versionCode) {
    return new GetAppCoinsCampaignsRequest(
        new GetAppCoinsCampaignsRequest.Body(packageName, versionCode), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, preferences).observe()
        .toSingle()
        .map(listAppCoinsCampaigns -> !listAppCoinsCampaigns.getList()
            .isEmpty());
  }
}
