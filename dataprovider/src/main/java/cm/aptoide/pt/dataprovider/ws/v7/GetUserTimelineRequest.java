/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Returns the user timeline. A series of cards with information related to application releases,
 * news, application
 * updates and so on.
 */
public class GetUserTimelineRequest extends V7<GetUserTimeline, GetUserTimelineRequest.Body> {

  private String url;

  GetUserTimelineRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static GetUserTimelineRequest of(String url, Integer limit, int offset,
      List<String> packages, BodyInterceptor<BaseBody> bodyInterceptor) {

    GetUserTimelineRequest getAppRequest =
        new GetUserTimelineRequest(url, new Body(limit, offset, packages), bodyInterceptor);
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
