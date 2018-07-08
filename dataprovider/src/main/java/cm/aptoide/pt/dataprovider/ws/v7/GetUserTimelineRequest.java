/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
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

  GetUserTimelineRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  GetUserTimelineRequest(String url, Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
    this.url = url;
  }

  public static GetUserTimelineRequest of(String url, Integer limit, int offset,
      List<String> packages, String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    GetUserTimelineRequest getAppRequest = new GetUserTimelineRequest(url,
        (Body) decorator.decorate(new Body(limit, offset, packages), accessToken), BASE_HOST);
    return getAppRequest;
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

    public Body(Integer limit, Integer offset, List<String> packageNames) {
      this.limit = limit;
      this.offset = offset;
      this.packageNames = packageNames;
    }
  }
}
