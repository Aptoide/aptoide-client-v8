/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsRequest
    extends BaseRequestWithStore<ListApps, ListAppsRequest.Body> {

  private static final int LINES_PER_REQUEST = 6;
  private String url;

  private ListAppsRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  private ListAppsRequest(Body body) {
    super(body, BASE_HOST);
  }

  public static ListAppsRequest ofAction(String url, StoreCredentials storeCredentials,
      String accessToken, String aptoideClientUUID) {
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);

    V7Url listAppsV7Url = new V7Url(url).remove("listApps");
    if (listAppsV7Url.containsLimit()) {
      return new ListAppsRequest(listAppsV7Url.get(),
          (Body) decorator.decorate(new Body(storeCredentials), accessToken), BASE_HOST);
    } else {
      return new ListAppsRequest(listAppsV7Url.get(), (Body) decorator.decorate(
          new Body(storeCredentials, Type.APPS_GROUP.getPerLineCount() * LINES_PER_REQUEST),
          accessToken), BASE_HOST);
    }
  }

  public static ListAppsRequest of(int groupId) {
    return new ListAppsRequest(new Body(groupId));
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.listApps(url != null ? url : "", body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore
      implements Endless {

    @Getter private Integer limit;
    @Getter @Setter private int offset;
    @Getter @Setter private Integer groupId;
    @Getter private String notApkTags;

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
      setNotApkTags();
    }

    public Body(StoreCredentials storeCredentials, int limit) {
      super(storeCredentials);
      this.limit = limit;
      setNotApkTags();
    }

    public Body(int groupId) {
      this.groupId = groupId;
      setNotApkTags();
    }

    /**
     * Method to check not Apk Tags on this particular request
     */
    private void setNotApkTags() {
      if (ManagerPreferences.getUpdatesFilterAlphaBetaKey()) {
        this.notApkTags = "alpha,beta";
      }
    }
  }
}
