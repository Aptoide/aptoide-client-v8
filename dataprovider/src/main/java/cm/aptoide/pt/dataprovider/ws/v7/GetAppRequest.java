/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.util.Log;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@EqualsAndHashCode(callSuper = true) public class GetAppRequest
    extends V7<GetApp, GetAppRequest.Body> {

  private GetAppRequest(String baseHost, Body body) {
    super(body, baseHost);
  }

  private GetAppRequest(OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost, Body body) {
    super(body, httpClient, converterFactory, baseHost);
  }

  public static GetAppRequest of(String packageName, String storeName, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

    return new GetAppRequest(BASE_HOST,
        (Body) decorator.decorate(new Body(packageName, storeName, forceServerRefresh),
            accessToken));
  }

  public static GetAppRequest of(long appId, String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

    return new GetAppRequest(BASE_HOST,
        (Body) decorator.decorate(new Body(appId, forceServerRefresh), accessToken));
  }

  public static GetAppRequest ofMd5(String md5, String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

    return new GetAppRequest(BASE_HOST,
        (Body) decorator.decorate(new Body(forceServerRefresh, md5), accessToken));
  }

  public static GetAppRequest of(long appId, String storeName,
      BaseRequestWithStore.StoreCredentials storeCredentials, String accessToken,
      String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    boolean forceServerRefresh = ManagerPreferences.getAndResetForceServerRefresh();

    Body body = new Body(appId, forceServerRefresh);
    body.setStoreUser(storeCredentials.getUsername());
    body.setStorePassSha1(storeCredentials.getPasswordSha1());

    return new GetAppRequest(BASE_HOST, (Body) decorator.decorate(body, accessToken));
  }

  public static GetAppRequest ofAction(String url, String accessToken, String aptoideClientUUID) {
    final long appId = getAppIdFromUrl(url);
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new GetAppRequest(BASE_HOST,
        (Body) decorator.decorate(new Body(appId), accessToken));
  }

  private static long getAppIdFromUrl(String url) {
    try {
      // find first index of "appId", split on "=" and the app id is the next index (1) of
      // the resulting array, excluding the remaining "trash"
      // example: http://ws75.aptoide.com/api/7/getApp/appId=15168558
      // example: http://ws75.aptoide.com/api/7/getApp/appId=15168558/other=stuff/in=here
      String tmp = url.substring(url.indexOf("app_id")).split("=")[1];
      int lastIdx = tmp.lastIndexOf('/');
      return Long.parseLong(tmp.substring(0, lastIdx > 0 ? lastIdx : tmp.length() ));
    }catch (Exception e) {
      Log.e(GetAppRequest.class.getName(), e.getMessage());
    }
    return 12765245; // -> Aptoide Uploader app id
  }

  @Override
  protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getApp(body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithApp {

    @Getter private Long appId;
    @Getter private String packageName;
    @Getter private boolean refresh;
    @Getter @JsonProperty("apk_md5sum") private String md5;
    @Getter @JsonProperty("store_name") private String storeName;

    public Body(long appId) {
      this(appId, false);
    }

    public Body(Long appId, Boolean refresh) {
      this.appId = appId;
      this.refresh = refresh;
    }

    public Body(String packageName, String storeName, boolean refresh) {
      this.packageName = packageName;
      this.refresh = refresh;
      this.storeName = storeName;
    }

    public Body(String packageName, Boolean refresh) {
      this.packageName = packageName;
      this.refresh = refresh;
    }

    public Body(Boolean refresh, String md5) {
      this.md5 = md5;
      this.refresh = refresh;
    }
  }
}
