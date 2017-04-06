/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by sithengineer on 20/07/16.
 */
public class PostReviewRequest extends V7<BaseV7Response, PostReviewRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected PostReviewRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static PostReviewRequest of(String storeName, String packageName, String title,
      String textBody, Integer rating, BodyInterceptor<BaseBody> bodyInterceptor) {
    final Body body = new Body(storeName, packageName, title, textBody, rating);
    return new PostReviewRequest(body, bodyInterceptor);
  }

  public static PostReviewRequest of(String packageName, String title, String textBody,
      Integer rating, BodyInterceptor<BaseBody> bodyInterceptor) {
    final Body body = new Body(packageName, title, textBody, rating);
    return new PostReviewRequest(body, bodyInterceptor);
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

    public Body(String packageName, String title, String body, Integer rating) {
      this.packageName = packageName;
      this.title = title;
      this.body = body;
      this.rating = rating;
    }

    public Body(String storeName, String packageName, String title, String body, Integer rating) {
      this.storeName = storeName;
      this.packageName = packageName;
      this.title = title;
      this.body = body;
      this.rating = rating;
    }
  }
}
