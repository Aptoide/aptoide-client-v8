package cm.aptoide.pt.autoupdate;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;

public class AutoUpdateService {

  private final Service service;
  private final String packageName;
  private final String autoUpdateStoreName;
  private boolean loading;

  public AutoUpdateService(Service service, String packageName, String autoUpdateStoreName) {
    this.service = service;
    this.packageName = packageName;
    this.autoUpdateStoreName = autoUpdateStoreName;
  }

  public Single<AutoUpdateModel> loadAutoUpdateModel() {
    if (loading) {
      return Single.just(new AutoUpdateModel(true));
    }
    return service.getJsonResponse(autoUpdateStoreName)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(jsonResponse -> Observable.just(
            new AutoUpdateModel(jsonResponse.getVersioncode(), jsonResponse.getUri(),
                jsonResponse.getMd5(), jsonResponse.getMinSdk(), packageName, false)))
        .onErrorReturn(throwable -> createErrorAutoUpdateModel(throwable))
        .toSingle();
  }

  private AutoUpdateModel createErrorAutoUpdateModel(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new AutoUpdateModel(AutoUpdateModel.Error.NETWORK);
    } else {
      return new AutoUpdateModel(AutoUpdateModel.Error.GENERIC);
    }
  }

  public interface Service {
    @GET("latest_version_{storeName}.json") Observable<AutoUpdateJsonResponse> getJsonResponse(
        @Path(value = "storeName") String storeName);
  }
}
