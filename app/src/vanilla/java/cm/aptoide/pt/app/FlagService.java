package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.dataprovider.ws.v3.AddApkFlagRequest;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import okhttp3.OkHttpClient;
import rx.Single;

/**
 * Created by D01 on 08/05/18.
 */

public class FlagService {

  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient okHttpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public FlagService(BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient okHttpClient,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {

    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.okHttpClient = okHttpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Single<GenericResponseV2> loadAddApkFlagRequest(String storeName, String md5,
      String flag) {
    return AddApkFlagRequest.of(storeName, md5, flag, bodyInterceptorV3, okHttpClient,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle();
  }
}
