package cm.aptoide.pt.promotions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.promotions.ClaimPromotionRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class PromotionsService {

  private final BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private final OkHttpClient okHttpClient;
  private final TokenInvalidator tokenInvalidator;
  private final Converter.Factory converterFactory;
  private final SharedPreferences sharedPreferences;

  public PromotionsService(BodyInterceptor<BaseBody> bodyInterceptorPoolV7,
      OkHttpClient okHttpClient, TokenInvalidator tokenInvalidator,
      Converter.Factory converterFactory, SharedPreferences sharedPreferences) {

    this.bodyInterceptorPoolV7 = bodyInterceptorPoolV7;
    this.okHttpClient = okHttpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.converterFactory = converterFactory;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<ClaimStatusWrapper> claimPromotion(String walletAddress, String packageName,
      String captcha) {
    return ClaimPromotionRequest.of(walletAddress, packageName, captcha, bodyInterceptorPoolV7,
        okHttpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .map(this::map)
        .onErrorReturn(throwable -> {
          if (throwable instanceof AptoideWsV7Exception) {
            return map(((AptoideWsV7Exception) throwable).getBaseResponse());
          } else {
            throw new RuntimeException(throwable);
          }
        })
        .toSingle();
  }

  private ClaimStatusWrapper map(BaseV7Response response) {
    return new ClaimStatusWrapper(mapStatus(response.getInfo()
        .getStatus()), response.getErrors());
  }

  private ClaimStatusWrapper.Status mapStatus(BaseV7Response.Info.Status status) {
    if (status.equals(BaseV7Response.Info.Status.OK)) {
      return ClaimStatusWrapper.Status.ok;
    } else {
      return ClaimStatusWrapper.Status.fail;
    }
  }
}
