/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by sithengineer on 29/07/16.
 */
public class SetReviewRatingRequest extends V7<BaseV7Response, SetReviewRatingRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  protected SetReviewRatingRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static SetReviewRatingRequest of(long reviewId, boolean helpful,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final Body body = new Body(reviewId, helpful ? "up" : "down");
    return new SetReviewRatingRequest(body, bodyInterceptor);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReviewVote(body, true);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private long review_id;
    private String vote;

    public Body(long reviewId, String vote) {

      this.review_id = reviewId;
      this.vote = vote;
    }
  }
}
