/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithApp;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppVersionsRequest
    extends V7<ListAppVersions, ListAppVersionsRequest.Body> {

  private static final Integer MAX_LIMIT = 10;

  private ListAppVersionsRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor) {
    super(body, BASE_HOST,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static ListAppVersionsRequest of(String packageName, List<String> storeNames,
      HashMapNotNull<String, List<String>> storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    if (storeNames != null && !storeNames.isEmpty()) {
      Body body = new Body(packageName, storeNames, storeCredentials);
      body.setLimit(MAX_LIMIT);
      return new ListAppVersionsRequest(body, bodyInterceptor);
    } else {
      return of(packageName, storeCredentials, bodyInterceptor);
    }
  }

  public static ListAppVersionsRequest of(String packageName,
      HashMapNotNull<String, List<String>> storeCredentials,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    Body body = new Body(packageName);
    body.setStoresAuthMap(storeCredentials);
    body.setLimit(MAX_LIMIT);
    return new ListAppVersionsRequest(body, bodyInterceptor);
  }

  @Override protected Observable<ListAppVersions> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.listAppVersions(body, bypassCache);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBodyWithApp implements Endless {

    private Integer apkId;
    private String apkMd5sum;
    private Integer appId;
    private String lang = Api.LANG;
    @Setter @Getter private Integer limit;
    @Setter @Getter private int offset;
    private Integer packageId;
    private String packageName;
    private String q = Api.Q;
    private List<Long> storeIds;
    private List<String> storeNames;
    @Getter private HashMapNotNull<String, List<String>> storesAuthMap;

    public Body() {
    }

    public Body(String packageName) {
      this.packageName = packageName;
    }

    public Body(String packageName, List<String> storeNames,
        HashMapNotNull<String, List<String>> storesAuthMap) {
      this.packageName = packageName;
      this.storeNames = storeNames;
      setStoresAuthMap(storesAuthMap);
    }

    public void setStoresAuthMap(HashMapNotNull<String, List<String>> storesAuthMap) {
      if (storesAuthMap != null) {
        this.storesAuthMap = storesAuthMap;
        this.storeNames = new LinkedList<>(storesAuthMap.keySet());
      }
    }
  }
}
