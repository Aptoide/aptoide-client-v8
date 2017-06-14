/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Returns the user timeline. A series of cards with information related to application releases,
 * news, application
 * updates and so on.
 */
public class GetUserTimelineRequest extends V7<GetUserTimeline, GetUserTimelineRequest.Body> {

  private String url;

  GetUserTimelineRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator) {
    super(body, BASE_HOST, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.url = url;
  }

  public static GetUserTimelineRequest of(String url, Integer limit, int offset,
      List<String> packages, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, String cardId, TokenInvalidator tokenInvalidator) {

    return new GetUserTimelineRequest(url, new Body(limit, offset, packages, cardId),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator);
  }

  @Override protected Observable<GetUserTimeline> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getUserTimeline(url, body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithAlphaBetaKey
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    @Getter private List<String> packageNames;
    @Getter private String cardUid;

    public Body(Integer limit, Integer offset, List<String> packageNames, String cardId) {
      this.limit = limit;
      this.offset = offset;
      this.packageNames = packageNames;
      this.cardUid = cardId;
    }
  }
}
