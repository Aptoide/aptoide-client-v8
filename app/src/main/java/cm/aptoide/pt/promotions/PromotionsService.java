package cm.aptoide.pt.promotions;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.promotions.ClaimPromotionRequest;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class PromotionsService {
  private static final String WRONG_CAPTCHA = "PROMOTION-1";
  private static final String WRONG_ADDRESS = "PROMOTION-2";
  private static final String ALREADY_CLAIMED = "PROMOTION-3";

  private final BodyInterceptor<BaseBody> bodyInterceptorPoolV7;
  private final OkHttpClient okHttpClient;
  private final TokenInvalidator tokenInvalidator;
  private final Converter.Factory converterFactory;
  private final SharedPreferences sharedPreferences;

  //Use ONLY to restore view state
  private String walletAddress;

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
        .getStatus()), mapError(response.getErrors()));
  }

  private ClaimStatusWrapper.Status mapStatus(BaseV7Response.Info.Status status) {
    if (status.equals(BaseV7Response.Info.Status.OK)) {
      return ClaimStatusWrapper.Status.OK;
    } else {
      return ClaimStatusWrapper.Status.FAIL;
    }
  }

  public void saveWalletAddress(String walletAddress) {
    this.walletAddress = walletAddress;
  }

  public String getWalletAddress() {
    return walletAddress;
  }

  private List<ClaimStatusWrapper.Error> mapError(List<BaseV7Response.Error> errors) {
    List<ClaimStatusWrapper.Error> result = new ArrayList<>();
    if (errors != null) {
      for (BaseV7Response.Error error : errors) {
        if (error.getCode()
            .equals(WRONG_CAPTCHA)) {
          result.add(ClaimStatusWrapper.Error.WRONG_CAPTCHA);
        } else if (error.getCode()
            .equals(WRONG_ADDRESS)) {
          result.add(ClaimStatusWrapper.Error.WRONG_ADDRESS);
        } else if (error.getCode()
            .equals(ALREADY_CLAIMED)) {
          result.add(ClaimStatusWrapper.Error.PROMOTION_CLAIMED);
        } else {
          result.add(ClaimStatusWrapper.Error.GENERIC);
        }
      }
    }
    return result;
  }
}
