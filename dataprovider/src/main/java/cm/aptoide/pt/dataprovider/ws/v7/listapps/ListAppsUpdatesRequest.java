/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.pm.PackageInfo;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.CrashReports;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 22-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ListAppsUpdatesRequest
    extends V7<ListAppsUpdates, ListAppsUpdatesRequest.Body> {

  private static final int SPLIT_SIZE = 100;

  private ListAppsUpdatesRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  private ListAppsUpdatesRequest(OkHttpClient httpClient, Converter.Factory converterFactory,
      Body body, String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
  }

  public static ListAppsUpdatesRequest of(List<Long> subscribedStoresIds, String accessToken,
      String email) {
    IdsRepository idsRepository =
        new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
    BaseBodyDecorator decorator = new BaseBodyDecorator(idsRepository);

    return new ListAppsUpdatesRequest((Body) decorator.decorate(
        new Body(getInstalledApks(), subscribedStoresIds, idsRepository.getAdvertisingId()),
        accessToken), BASE_HOST);
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
    ListAppsUpdates resultListAppsUpdates = new ListAppsUpdates();

    if (body.getApksData().size() > SPLIT_SIZE) {

      int latchCount =
          body.getApksData().size() / SPLIT_SIZE + (body.getApksData().size() % SPLIT_SIZE > 0 ? 1
              : 0);
      CountDownLatch countDownLatch = new CountDownLatch(latchCount);

      resultListAppsUpdates.setList(new LinkedList<>());
      List<ApksData> apksData = body.getApksData();

      for (int n = 0; n < apksData.size(); n += SPLIT_SIZE) {
        Body tmpBody = new Body(body).setApksData(apksData.subList(n,
            n + SPLIT_SIZE > apksData.size() ? n + apksData.size() % SPLIT_SIZE : n + SPLIT_SIZE));

        interfaces.listAppsUpdates(tmpBody, bypassCache)
            .subscribeOn(Schedulers.io())
            .subscribe(listAppsUpdates -> {
              resultListAppsUpdates.getList().addAll(listAppsUpdates.getList());
              countDownLatch.countDown();
            }, throwable -> {
              countDownLatch.countDown();
              throwable.printStackTrace();
            });
      }

      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        CrashReports.logException(e);
        e.printStackTrace();
      }

      return Observable.just(resultListAppsUpdates);
    } else {
      return interfaces.listAppsUpdates(body, bypassCache);
    }
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBody {

    @Accessors(chain = true) @Getter @Setter private List<ApksData> apksData;
    @Getter private List<Long> storeIds;
    @Setter @Getter private String aaid;
    @Getter private String notApkTags;
    @Getter private String notPackageTags;

    public Body(List<ApksData> apksData, List<Long> storeIds, String aaid) {
      this.apksData = apksData;
      this.storeIds = storeIds;
      this.aaid = aaid;
      setNotApkTags();
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
      this.notApkTags = body.getNotApkTags();
      this.notPackageTags = body.getNotPackageTags();
    }

    private void setNotApkTags() {
      if (ManagerPreferences.getUpdatesFilterAlphaBetaKey()) {
        this.notApkTags = "alpha,beta";
      }
    }

    private void setSystemAppsUpdates() {
      if (!ManagerPreferences.getUpdatesSystemAppsKey()) {
        this.notPackageTags = "system";
      }
    }
  }

  @AllArgsConstructor public static class ApksData {

    @Getter @JsonProperty("package") private String packageName;
    @Getter private int vercode;
    @Getter private String signature;
  }
}
