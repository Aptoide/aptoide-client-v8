/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Returns the user timeline. A series of cards with information related to application releases,
 * news, application
 * updates and so on.
 */
public class GetUserTimelineRequest extends V7<GetUserTimeline, GetUserTimelineRequest.Body> {

  private String url = "http://192.168.1.137:5000/getTimeline";

  GetUserTimelineRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    //this.url = url;
  }

  public static GetUserTimelineRequest of(String url, Integer limit, int offset,
      List<String> packages, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, String cardId, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    return new GetUserTimelineRequest(url,
        new Body(limit, offset, packages, cardId, sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<GetUserTimeline> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getUserTimeline(url, body, bypassCache);
  }

  public static class Body extends BaseBodyWithAlphaBetaKey implements Endless {

    private int offset;
    private Integer limit;
    private List<String> packageNames;
    private String cardUid;

    public Body(Integer limit, Integer offset, List<String> packageNames, String cardId,
        SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.limit = limit;
      this.offset = offset;
      this.packageNames = packageNames;
      this.cardUid = cardId;
    }

    public List<String> getPackageNames() {
      return packageNames;
    }

    public String getCardUid() {
      return cardUid;
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
  }
}
