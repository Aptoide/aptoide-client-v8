/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.pm.PackageInfo;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithAlphaBetaKey;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsUpdatesRequest
    extends V7<ListAppsUpdates, ListAppsUpdatesRequest.Body> {

  private static final String TAG = ListAppsUpdatesRequest.class.getName();

  private static final int SPLIT_SIZE = 100;

  private ListAppsUpdatesRequest(Body body, String baseHost, BodyInterceptor bodyInterceptor) {
    super(body, baseHost,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static ListAppsUpdatesRequest of(List<Long> subscribedStoresIds, String aptoideClientUUID,
      BodyInterceptor bodyInterceptor) {
    return new ListAppsUpdatesRequest(
        new Body(getInstalledApks(), subscribedStoresIds, aptoideClientUUID), BASE_HOST,
        bodyInterceptor);
  }

  private static List<ApksData> getInstalledApks() {
    // TODO: 01-08-2016 neuro benchmark this, looks heavy
    List<PackageInfo> allInstalledApps = AptoideUtils.SystemU.getAllInstalledApps();
    LinkedList<ApksData> apksDatas = new LinkedList<>();

    for (PackageInfo packageInfo : allInstalledApps) {
      apksDatas.add(new ApksData(packageInfo.packageName, packageInfo.versionCode,
          AptoideUtils.AlgorithmU.computeSha1WithColon(packageInfo.signatures[0].toByteArray())));
    }

    return apksDatas;
  }

  @Override protected Observable<ListAppsUpdates> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return Observable.just(body.getApksData().size()).flatMap(bodySize -> {
      if (bodySize > SPLIT_SIZE) {
        //
        // we need to split the request in several requests due to having too much apps
        //
        // necessary steps to take
        //
        // 1) create necessary request bodies
        // 2) create requests with each body
        // 3) do parallel requests
        // 4) wait for all requests
        // 5) merge all the request results in one object
        //

        List<ApksData> apksData = body.getApksData();
        ArrayList<Body> bodies = new ArrayList<>();

        for (int n = 0; n < apksData.size(); n += SPLIT_SIZE) {
          bodies.add(getBody(apksData, n));
        }

        return Observable.from(bodies)
            // switch to I/O scheduler
            .observeOn(Schedulers.io())
            // map bodies to request with bodies
            //.map(body -> fetchDataUsingBodyWithRetry(interfaces, body, bypassCache, 3))
            .map(body -> interfaces.listAppsUpdates(body, bypassCache))
            // wait for all requests to be ready and return a list of requests
            .toList()
            // subscribe to all observables (list of observables<request>) at the same time using merge
            .flatMap(requestList -> Observable.merge(requestList))
            // wait for all responses
            .toList()
            // flat the list of [list of updates] into a list of updates
            .flatMapIterable(responses -> responses)
            // get the inner list of updates
            .map(response -> response.getList())
            // iterate over each update
            .flatMapIterable(responseList -> responseList)
            // create list of updates
            .toList()
            // return the resulting list of updates into a single object
            .map(listAppUpdates -> {
              ListAppsUpdates resultListAppsUpdates = new ListAppsUpdates();
              resultListAppsUpdates.setList(listAppUpdates);
              return resultListAppsUpdates;
            });
      }
      return interfaces.listAppsUpdates(body, bypassCache);
    });
  }

  private Body getBody(List<ApksData> apksData, int n) {
    return new Body(body).setApksData(apksData.subList(n,
        n + SPLIT_SIZE > apksData.size() ? n + apksData.size() % SPLIT_SIZE : n + SPLIT_SIZE));
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithAlphaBetaKey {

    @Accessors(chain = true) @Getter @Setter private List<ApksData> apksData;
    @Getter private List<Long> storeIds;
    @Setter @Getter private String aaid;
    @Getter private String notPackageTags;

    public Body(List<ApksData> apksData, List<Long> storeIds, String aaid) {
      this.apksData = apksData;
      this.storeIds = storeIds;
      this.aaid = aaid;
      setSystemAppsUpdates();
    }

    public Body(Body body) {
      this.apksData = body.getApksData();
      this.storeIds = body.getStoreIds();
      this.setQ(body.getQ());
      this.setCountry(body.getCountry());
      this.setAptoideVercode(body.getAptoideVercode());
      this.aaid = body.getAaid();
      this.setAptoideId(body.getAptoideId());
      this.notPackageTags = body.getNotPackageTags();
    }

    private void setSystemAppsUpdates() {
      if (!ManagerPreferences.getUpdatesSystemAppsKey()) {
        this.notPackageTags = "system";
      }
    }
  }

  public static class ApksData {

    @Getter @JsonProperty("package") private String packageName;
    @Getter private int vercode;
    @Getter private String signature;

    public ApksData(String packageName, int vercode, String signature) {
      this.packageName = packageName;
      this.vercode = vercode;
      this.signature = signature;
    }
  }
}
