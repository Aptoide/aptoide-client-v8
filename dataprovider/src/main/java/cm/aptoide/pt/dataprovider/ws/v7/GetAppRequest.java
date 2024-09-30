/*
 * Copyright (c) 2016.
 * Modified on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
public class GetAppRequest extends V7<GetApp, GetAppRequest.Body> {

  private GetAppRequest(String baseHost, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_CACHE_HOST
        + "/api/7.20240701/";
  }

  public static GetAppRequest of(String packageName, String storeName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    boolean forceServerRefresh =
        ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences);

    return new GetAppRequest(getHost(sharedPreferences),
        new Body(packageName, storeName, forceServerRefresh, sharedPreferences), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator);
  }

  public static GetAppRequest of(String packageName, BodyInterceptor<BaseBody> bodyInterceptor,
      long appId, OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {

    return new GetAppRequest(getHost(sharedPreferences),
        new Body(appId, packageName, sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  public static GetAppRequest of(String packageName, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    boolean forceServerRefresh =
        ManagerPreferences.getAndResetForceServerRefresh(sharedPreferences);

    return new GetAppRequest(getHost(sharedPreferences),
        new Body(packageName, forceServerRefresh, sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  public static GetAppRequest ofMd5(String md5, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {

    return new GetAppRequest(getHost(sharedPreferences), new Body(sharedPreferences, md5),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator);
  }

  public static GetAppRequest ofUname(String uname, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {

    return new GetAppRequest(getHost(sharedPreferences), new Body(uname, sharedPreferences),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator);
  }

  public static GetAppRequest of(long appId, String storeName,
      BaseRequestWithStore.StoreCredentials storeCredentials, String packageName,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    Body body = new Body(appId, storeName, packageName, sharedPreferences);
    body.setStoreUser(storeCredentials.getUsername());
    body.setStorePassSha1(storeCredentials.getPasswordSha1());

    return new GetAppRequest(getHost(sharedPreferences), body, bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator);
  }

  @Override
  protected Observable<GetApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getApp(bypassCache ? "no-cache" : null,
        getQueryStringMapper().map(body));
  }

  public static class Body extends BaseBodyWithApp {

    private Long appId;
    private String packageName;
    @JsonProperty("package_uname") private String uname;
    @JsonProperty("apk_md5sum") private String md5;
    @JsonProperty("store_name") private String storeName;
    private Node nodes;

    public Body(Long appId, String packageName, SharedPreferences sharedPreferences) {
      this(appId, sharedPreferences);
      this.nodes = new Node(appId, packageName);
    }

    public Body(Long appId, String storeName, String packageName,
        SharedPreferences sharedPreferences) {
      this(appId, sharedPreferences);
      this.storeName = storeName;
      this.nodes = new Node(appId, packageName);
    }

    public Body(String packageName, String storeName, boolean refresh,
        SharedPreferences sharedPreferences) {
      this(packageName, refresh, sharedPreferences);
      this.storeName = storeName;
      this.nodes = new Node();
    }

    public Body(String packageName, Boolean refresh, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.packageName = packageName;
      this.nodes = new Node();
    }

    public Body(String uname, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.uname = uname;
      this.nodes = new Node();
    }

    public Body(SharedPreferences sharedPreferences, String md5) {
      super(sharedPreferences);
      this.md5 = md5;
      this.nodes = new Node();
    }

    public Body(long appId, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.appId = appId;
      this.nodes = new Node();
    }

    public Long getAppId() {
      return appId;
    }

    public String getPackageName() {
      return packageName;
    }

    public String getUname() {
      return uname;
    }

    public String getMd5() {
      return md5;
    }

    public String getStoreName() {
      return storeName;
    }

    public Node getNodes() {
      return nodes;
    }

    private static class Node {

      private Meta meta;
      private Versions versions;
      private Groups groups;

      public Node(Long appId, String packageName) {
        this.meta = new Meta();
        this.meta.setAppId(appId);
        this.versions = new Versions();
        this.versions.setPackageName(packageName);
        this.groups = new Groups();
      }

      public Node(long appId) {
        this(appId, null);
      }

      public Node(String packageName) {
        this(null, packageName);
      }

      public Node() {
        this(null, null);
      }

      public Meta getMeta() {
        return meta;
      }

      public void setMeta(Meta meta) {
        this.meta = meta;
      }

      public Versions getVersions() {
        return versions;
      }

      public void setVersions(Versions versions) {
        this.versions = versions;
      }

      public Groups getGroups() {
        return groups;
      }

      public void setGroups(Groups groups) {
        this.groups = groups;
      }

      private static class Meta {

        @JsonInclude(JsonInclude.Include.NON_NULL) private Long appId;

        public Long getAppId() {
          return appId;
        }

        public void setAppId(Long appId) {
          this.appId = appId;
        }
      }

      private static class Versions {

        @JsonInclude(JsonInclude.Include.NON_NULL) private String packageName;

        public String getPackageName() {
          return packageName;
        }

        public void setPackageName(String packageName) {
          this.packageName = packageName;
        }
      }

      private static class Groups {
      }
    }
  }
}
