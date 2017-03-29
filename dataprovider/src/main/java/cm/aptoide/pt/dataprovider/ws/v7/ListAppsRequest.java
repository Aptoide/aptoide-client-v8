/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

/**
 * Created by neuro on 27-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsRequest
    extends V7<ListApps, ListAppsRequest.Body> {

  private static final int LINES_PER_REQUEST = 6;
  private String url;

  private ListAppsRequest(String url, Body body, BodyInterceptor bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
    this.url = url;
  }

  public static ListAppsRequest ofAction(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, BodyInterceptor bodyInterceptor) {
    V7Url listAppsV7Url = new V7Url(url).remove("listApps");
    if (listAppsV7Url.containsLimit()) {
      return new ListAppsRequest(listAppsV7Url.get(), new Body(storeCredentials), bodyInterceptor);
    } else {
      return new ListAppsRequest(listAppsV7Url.get(),
          new Body(storeCredentials, Type.APPS_GROUP.getPerLineCount() * LINES_PER_REQUEST),
          bodyInterceptor);
    }
  }

  @Override
  protected Observable<ListApps> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.listApps(url != null ? url : "", body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody
      implements Endless {

    @Getter private String storeUser;
    @Getter private String storePassSha1;
    @Getter private Integer limit;
    @Getter @Setter private int offset;
    @Getter @Setter private Integer groupId;
    @Getter private String notApkTags;

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials) {
      super();

      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
      setNotApkTags();
    }

    public Body(BaseRequestWithStore.StoreCredentials storeCredentials, int limit) {
      super();
      this.storeUser = storeCredentials.getUsername();
      this.storePassSha1 = storeCredentials.getPasswordSha1();
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
