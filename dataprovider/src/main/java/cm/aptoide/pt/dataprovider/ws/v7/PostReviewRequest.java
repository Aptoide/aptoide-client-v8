/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created on 20/07/16.
 */
public class PostReviewRequest extends V7<BaseV7Response, PostReviewRequest.Body> {

  protected PostReviewRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static PostReviewRequest of(String storeName, String packageName, String title,
      String textBody, Integer rating, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, boolean appInstalled,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final Body body = new Body(storeName, packageName, title, textBody, rating, appInstalled);
    return new PostReviewRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static PostReviewRequest of(String packageName, String title, String textBody,
      Integer rating, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, boolean appInstalled, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final Body body = new Body(packageName, title, textBody, rating, appInstalled);
    return new PostReviewRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postReview(body, true);
  }

  public static class Body extends BaseBody {

    private final boolean appInstalled;
    private String storeName;
    private String packageName;
    private String title;
    private String body;
    private Integer rating;

    public Body(String packageName, String title, String body, Integer rating,
        boolean appInstalled) {
      this.packageName = packageName;
      this.title = title;
      this.body = body;
      this.rating = rating;
      this.appInstalled = appInstalled;
    }

    public Body(String storeName, String packageName, String title, String body, Integer rating,
        boolean appInstalled) {
      this.storeName = storeName;
      this.packageName = packageName;
      this.title = title;
      this.body = body;
      this.rating = rating;
      this.appInstalled = appInstalled;
    }

    public String getStoreName() {
      return storeName;
    }

    public void setStoreName(String storeName) {
      this.storeName = storeName;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getBody() {
      return body;
    }

    public void setBody(String body) {
      this.body = body;
    }

    public Integer getRating() {
      return rating;
    }

    public void setRating(Integer rating) {
      this.rating = rating;
    }

    public boolean isAppInstalled() {
      return appInstalled;
    }
  }
}
