package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class ClaimPromotionRequest extends V7<BaseV7Response, ClaimPromotionRequest.Body> {

  public ClaimPromotionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static ClaimPromotionRequest of(String walletAddress, String packageName, String captcha,
      String promotionId, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    return new ClaimPromotionRequest(new Body(walletAddress, packageName, captcha, promotionId),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.claimPromotion(body, true);
  }

  public static class Body extends BaseBody {

    private final String walletAddress;
    private final String packageName;
    private final String captcha;
    private final String promotionId;

    public Body(String walletAddress, String packageName, String captcha, String promotionId) {

      this.walletAddress = walletAddress;
      this.packageName = packageName;
      this.captcha = captcha;
      this.promotionId = promotionId;
    }

    public String getWalletAddress() {
      return walletAddress;
    }

    public String getPackageName() {
      return packageName;
    }

    public String getCaptcha() {
      return captcha;
    }

    public String getPromotionId() {
      return promotionId;
    }
  }
}
