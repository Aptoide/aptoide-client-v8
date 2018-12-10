package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class ClaimPromotionRequest extends V7<BaseV7Response, ClaimPromotionRequest.Body> {

  public ClaimPromotionRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(), httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost() {
    return "http://dev.ws75.aptoide.com/api/7/";
  }

  public static ClaimPromotionRequest of(String walletAddress, String packageName, String captcha,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final BaseBody body = new BaseBody();
    return new ClaimPromotionRequest(new Body(walletAddress, packageName, captcha), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.claimPromotion(body, true);
  }

  public static class Body extends BaseBody {

    private final String walletAddress;
    private final String packageName;
    private final String captcha;

    public Body(String walletAddress, String packageName, String captcha) {

      this.walletAddress = walletAddress;
      this.packageName = packageName;
      this.captcha = captcha;
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
  }
}
