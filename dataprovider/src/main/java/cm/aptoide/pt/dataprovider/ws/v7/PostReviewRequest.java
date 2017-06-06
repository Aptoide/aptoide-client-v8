/*
 * Copyright (c) 2016.
 * Modified on 25/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created on 20/07/16.
 */
public class PostReviewRequest extends V7<BaseV7Response, PostReviewRequest.Body> {

  private static final String BASE_HOST = (ToolboxManager.isToolboxEnableHttpScheme() ? "http"
      : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected PostReviewRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor);
  }

  public static PostReviewRequest of(String storeName, String packageName, String title,
      String textBody, Integer rating, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, boolean appInstalled) {
    final Body body = new Body(storeName, packageName, title, textBody, rating, appInstalled);
    return new PostReviewRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  public static PostReviewRequest of(String packageName, String title, String textBody,
      Integer rating, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, boolean appInstalled) {
    final Body body = new Body(packageName, title, textBody, rating, appInstalled);
    return new PostReviewRequest(body, bodyInterceptor, httpClient, converterFactory);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.postReview(body, true);
  }

  @Data @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    private String storeName;
    private String packageName;
    private String title;
    private String body;
    private Integer rating;
    private final boolean appInstalled;

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
  }
}
