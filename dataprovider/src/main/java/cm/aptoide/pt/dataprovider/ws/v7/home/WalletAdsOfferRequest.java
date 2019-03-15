package cm.aptoide.pt.dataprovider.ws.v7.home;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class WalletAdsOfferRequest extends V7<WalletAdsOfferResponse, BaseBody> {

  protected WalletAdsOfferRequest(BaseBody body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static WalletAdsOfferRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final BaseBody body = new BaseBody();
    return new WalletAdsOfferRequest(body, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<WalletAdsOfferResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.isWalletOfferActive(body, bypassCache);
  }
}
