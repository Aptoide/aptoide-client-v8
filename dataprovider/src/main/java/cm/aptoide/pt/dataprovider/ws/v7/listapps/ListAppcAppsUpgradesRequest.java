package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithAlphaBetaKey;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest.getInstalledApks;

public class ListAppcAppsUpgradesRequest
    extends V7<ListAppsUpdates, ListAppcAppsUpgradesRequest.Body> {

  private static final int SPLIT_SIZE = 100;
  private final SharedPreferences sharedPreferences;

  private ListAppcAppsUpgradesRequest(Body body, String baseHost,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.sharedPreferences = sharedPreferences;
  }

  public static ListAppcAppsUpgradesRequest of(String clientUniqueId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PackageManager packageManager) {
    return new ListAppcAppsUpgradesRequest(
        new Body(getInstalledApks(packageManager), clientUniqueId, sharedPreferences),
        getHost(sharedPreferences), bodyInterceptor, httpClient, converterFactory, tokenInvalidator,
        sharedPreferences);
  }

  @Override protected Observable<ListAppsUpdates> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return Observable.just(body.getApksData()
        .size())
        .flatMap(bodySize -> {
          if (bodySize > SPLIT_SIZE) {
            List<ListAppsUpdatesRequest.ApksData> apksData = body.getApksData();
            ArrayList<Body> bodies = new ArrayList<>();

            for (int n = 0; n < apksData.size(); n += SPLIT_SIZE) {
              bodies.add(getBody(apksData, n, sharedPreferences));
            }

            return Observable.from(bodies)
                .observeOn(Schedulers.io())
                .map(body -> interfaces.listAppcAppssUpgrades(body, bypassCache))
                .toList()
                .flatMap(requestList -> Observable.merge(requestList))
                .toList()
                .flatMapIterable(responses -> responses)
                .map(response -> response.getList())
                .flatMapIterable(responseList -> responseList)
                .toList()
                .map(listAppUpdates -> {
                  ListAppsUpdates resultListAppsUpdates = new ListAppsUpdates();
                  resultListAppsUpdates.setList(listAppUpdates);
                  return resultListAppsUpdates;
                });
          }
          return interfaces.listAppcAppssUpgrades(body, bypassCache);
        });
  }

  private Body getBody(List<ListAppsUpdatesRequest.ApksData> apksData, int n,
      SharedPreferences sharedPreferences) {
    Body resultBody = new Body(body, sharedPreferences);
    resultBody.setApksData(apksData.subList(n,
        n + SPLIT_SIZE > apksData.size() ? n + apksData.size() % SPLIT_SIZE : n + SPLIT_SIZE));
    return resultBody;
  }

  public static class Body extends BaseBodyWithAlphaBetaKey {

    private List<ListAppsUpdatesRequest.ApksData> apksData;
    private String aaid;
    private String notPackageTags;

    public Body(List<ListAppsUpdatesRequest.ApksData> apksData, String aaid,
        SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.apksData = apksData;
      this.aaid = aaid;
      setSystemAppsUpdates(sharedPreferences);
    }

    public Body(Body body, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.apksData = body.getApksData();
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

    public String getNotPackageTags() {
      return notPackageTags;
    }

    public List<ListAppsUpdatesRequest.ApksData> getApksData() {
      return apksData;
    }

    public void setApksData(List<ListAppsUpdatesRequest.ApksData> apksData) {
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
}
