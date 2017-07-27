/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.utils.AptoideUtils;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created on 21/07/16.
 */
public class GetApkInfoRequest extends V3<PaidApp> {

  protected GetApkInfoRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static GetApkInfoRequest of(long appId, boolean sponsored, String storeName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources) {
    BaseBody args = new BaseBody();
    args.put("identif", "id:" + appId);
    args.put("repo", storeName);
    args.put("mode", "json");

    if (sponsored) {
      args.put("adview", "1");
    }
    addOptions(args, resources);
    return new GetApkInfoRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  private static void addOptions(BaseBody args, Resources resources) {
    BaseBody options = new BaseBody();
    options.put("cmtlimit", "5");
    options.put("payinfo", "true");
    options.put("lang", AptoideUtils.SystemU.getCountryCode(resources));

    StringBuilder optionsBuilder = new StringBuilder();
    optionsBuilder.append("(");
    for (String optionKey : options.keySet()) {
      optionsBuilder.append(optionKey);
      optionsBuilder.append("=");
      optionsBuilder.append(options.get(optionKey));
      optionsBuilder.append(";");
    }
    optionsBuilder.append(")");
    args.put("options", optionsBuilder.toString());
  }

  @Override
  protected Observable<PaidApp> loadDataFromNetwork(Service service, boolean bypassCache) {
    return service.getApkInfo(map, bypassCache);
  }
}
