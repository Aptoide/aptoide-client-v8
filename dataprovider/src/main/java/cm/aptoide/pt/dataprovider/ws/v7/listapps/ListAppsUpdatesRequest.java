/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithAlphaBetaKey;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
public class ListAppsUpdatesRequest extends V7<ListAppsUpdates, ListAppsUpdatesRequest.Body> {

  private static final int SPLIT_SIZE = 100;
  private final SharedPreferences sharedPreferences;

  private ListAppsUpdatesRequest(Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.sharedPreferences = sharedPreferences;
  }

  public static ListAppsUpdatesRequest of(List<Long> subscribedStoresIds, String clientUniqueId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PackageManager packageManager) {
    return new ListAppsUpdatesRequest(
        new Body(getInstalledApks(packageManager), subscribedStoresIds, clientUniqueId,
            sharedPreferences), getHost(sharedPreferences), bodyInterceptor, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences);
  }

  private static List<PackageInfo> getAllInstalledApps(PackageManager packageManager) {
    return packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
  }

  static List<ApksData> getInstalledApks(PackageManager packageManager) {
    // TODO: 01-08-2016 neuro benchmark this, looks heavy
    List<PackageInfo> allInstalledApps = getAllInstalledApps(packageManager);
    LinkedList<ApksData> apksDatas = new LinkedList<>();

    for (PackageInfo packageInfo : allInstalledApps) {
      boolean isEnabled = true;
      try {
        isEnabled = packageManager.getApplicationInfo(packageInfo.packageName, 0).enabled;
      } catch (PackageManager.NameNotFoundException ex) {
        CrashReport.getInstance()
            .log(ex);
      }

      apksDatas.add(new ApksData(packageInfo.packageName, packageInfo.versionCode,
          AptoideUtils.AlgorithmU.computeSha1WithColon(packageInfo.signatures[0].toByteArray()),
          isEnabled));
    }

    return apksDatas;
  }

  @Override protected Observable<ListAppsUpdates> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return Observable.just(body.getApksData()
        .size())
        .flatMap(bodySize -> {
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
              bodies.add(getBody(apksData, n, sharedPreferences));
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

  private Body getBody(List<ApksData> apksData, int n, SharedPreferences sharedPreferences) {
    Body resultBody = new Body(body, sharedPreferences);
    resultBody.setApksData(apksData.subList(n,
        n + SPLIT_SIZE > apksData.size() ? n + apksData.size() % SPLIT_SIZE : n + SPLIT_SIZE));
    return resultBody;
  }

  public static class Body extends BaseBodyWithAlphaBetaKey {

    private List<ApksData> apksData;
    private String aaid;
    private List<Long> storeIds;
    private String notPackageTags;

    public Body(List<ApksData> apksData, List<Long> storeIds, String aaid,
        SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.apksData = apksData;
      this.storeIds = storeIds;
      this.aaid = aaid;
      setSystemAppsUpdates(sharedPreferences);
    }

    public Body(Body body, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.apksData = body.getApksData();
      this.storeIds = body.getStoreIds();
      this.setQ(body.getQ());
      this.setCountry(body.getCountry());
      this.setAptoideVercode(body.getAptoideVercode());
      this.aaid = body.getAaid();
      this.setAptoideId(body.getAptoideId());
      this.notPackageTags = body.getNotPackageTags();
      this.setAptoideMd5sum(body.getAptoideMd5sum());
      this.setAptoidePackage(body.getAptoidePackage());
      this.setLang(body.getLang());
      this.setCdn(body.getCdn());
      this.setMature(body.isMature());
    }

    public List<Long> getStoreIds() {
      return storeIds;
    }

    public String getNotPackageTags() {
      return notPackageTags;
    }

    public List<ApksData> getApksData() {
      return apksData;
    }

    public void setApksData(List<ApksData> apksData) {
      this.apksData = apksData;
    }

    public String getAaid() {
      return aaid;
    }

    public void setAaid(String aaid) {
      this.aaid = aaid;
    }

    private void setSystemAppsUpdates(SharedPreferences sharedPreferences) {
      if (!ManagerPreferences.getUpdatesSystemAppsKey(sharedPreferences)) {
        this.notPackageTags = "system";
      }
    }
  }

  public static class ApksData {

    @JsonProperty("package") private String packageName;
    @JsonProperty("vercode") private int versionCode;
    private String signature;
    private boolean isEnabled;

    public ApksData(String packageName, int versionCode, String signature, boolean isEnabled) {
      this.packageName = packageName;
      this.versionCode = versionCode;
      this.signature = signature;
      this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
      return isEnabled;
    }

    public String getPackageName() {
      return packageName;
    }

    public int getVersionCode() {
      return versionCode;
    }

    public String getSignature() {
      return signature;
    }
  }
}
