/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.Type;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
public class ListAppsRequest extends V7<ListApps, ListAppsRequest.Body> {

  private static final int LINES_PER_REQUEST = 6;
  private String url;

  public ListAppsRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.url = url;
  }

  public ListAppsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_CACHE_HOST
        + "/api/7/";
  }

  public static ListAppsRequest ofAction(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager) {
    V7Url listAppsV7Url = new V7Url(url).remove("listApps");
    if (listAppsV7Url.containsLimit()) {
      return new ListAppsRequest(listAppsV7Url.get(), new Body(storeCredentials, sharedPreferences),
          bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
    } else {
      return new ListAppsRequest(listAppsV7Url.get(), new Body(storeCredentials,
          Type.APPS_GROUP.getPerLineCount(resources, windowManager) * LINES_PER_REQUEST,
          sharedPreferences), bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
          sharedPreferences);
    }
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.listApps(url != null ? url : "", bypassCache ? "no-cache" : null,
        getQueryStringMapper().map(body));
  }

  public enum Sort {
    latest, trending7d, trending60d,
  }

  public static class Body extends BaseBody implements Endless {

    private String storeUser;
    private String storePassSha1;
    private Integer limit;
    private String notApkTags;
    private int offset;
    private Long groupId;
    private Sort sort;
    private Long storeId;

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials,
        SharedPreferences sharedPreferences) {
      super();

      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
      setNotApkTags(sharedPreferences);
    }

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials, int limit,
        SharedPreferences sharedPreferences) {
      super();
      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
      this.limit = limit;
      setNotApkTags(sharedPreferences);
    }

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials, int limit,
        SharedPreferences sharedPreferences, Sort sort) {
      super();
      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
      this.limit = limit;
      this.sort = sort;
      setNotApkTags(sharedPreferences);
    }

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials, Long groupId, int limit,
        SharedPreferences sharedPreferences, Sort sort) {
      super();
      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
      this.groupId = groupId;
      this.limit = limit;
      this.sort = sort;
      setNotApkTags(sharedPreferences);
    }

    public Long getStoreId() {
      return storeId;
    }

    public void setStoreId(long storeId) {
      this.storeId = storeId;
    }

    public String getStoreUser() {
      return storeUser;
    }

    public String getStorePassSha1() {
      return storePassSha1;
    }

    public String getNotApkTags() {
      return notApkTags;
    }

    /**
     * Method to check not Apk Tags on this particular request
     */
    private void setNotApkTags(SharedPreferences sharedPreferences) {
      if (ManagerPreferences.getUpdatesFilterAlphaBetaKey(sharedPreferences)) {
        this.notApkTags = "alpha,beta";
      }
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }

    public Long getGroupId() {
      return groupId;
    }

    public void setGroupId(Long groupId) {
      this.groupId = groupId;
    }

    public Sort getSort() {
      return sort;
    }

    public void setSort(Sort sort) {
      this.sort = sort;
    }
  }
}
