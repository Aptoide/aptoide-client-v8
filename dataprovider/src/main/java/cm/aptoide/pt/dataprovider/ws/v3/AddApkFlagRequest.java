/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.networkclient.WebService;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created on 21/07/16.
 */
public class AddApkFlagRequest extends V3<GenericResponseV2> {

  protected AddApkFlagRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static AddApkFlagRequest of(String storeName, String appMd5sum, String flag,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    BaseBody args = new BaseBody();

    args.put("repo", storeName);
    args.put("md5sum", appMd5sum);
    args.put("flag", flag);
    args.put("mode", "json");

    return new AddApkFlagRequest(args, bodyInterceptor, httpClient,
        WebService.getDefaultConverter(), tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<GenericResponseV2> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addApkFlag(map, bypassCache);
  }
}
