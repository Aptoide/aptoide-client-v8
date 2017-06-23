/*
 * Copyright (c) 2016.
 * Modified on 29/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created on 29/07/16.
 */
public class SetReviewRatingRequest extends V7<BaseV7Response, SetReviewRatingRequest.Body> {

  protected SetReviewRatingRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
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

  public static SetReviewRatingRequest of(long reviewId, boolean helpful,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final Body body = new Body(reviewId, helpful ? "up" : "down");
    return new SetReviewRatingRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setReviewVote(body, true);
  }

  public static class Body extends BaseBody {

    private long review_id;
    private String vote;

    public Body(long reviewId, String vote) {

      this.review_id = reviewId;
      this.vote = vote;
    }

    public long getReview_id() {
      return review_id;
    }

    public void setReview_id(long review_id) {
      this.review_id = review_id;
    }

    public String getVote() {
      return vote;
    }

    public void setVote(String vote) {
      this.vote = vote;
    }
  }
}
