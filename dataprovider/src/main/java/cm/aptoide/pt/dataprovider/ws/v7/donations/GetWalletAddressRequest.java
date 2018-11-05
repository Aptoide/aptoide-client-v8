package cm.aptoide.pt.dataprovider.ws.v7.donations;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetWalletAddressRequest extends V7<GetWalletAddressResponse, BaseBody> {

  private String packageName;

  protected GetWalletAddressRequest(String packageName, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(new BaseBody(), baseHost, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.packageName = packageName;
  }

  public static GetWalletAddressRequest of(String packageName, SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator) {

    return new GetWalletAddressRequest(packageName, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_READ_V7_HOST
        + "/api/7/";
  }

  @Override
  protected Observable<GetWalletAddressResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getWallet(packageName);
  }
}
