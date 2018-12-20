package cm.aptoide.pt.autoupdate;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Single;

public class AutoUpdateService {

  private final Service retrofit;
  private final String packageName;
  private final String autoUpdateStoreName;
  private boolean loading;

  public AutoUpdateService(Service retrofit, String packageName, String autoUpdateStoreName) {
    this.retrofit = retrofit;
    this.packageName = packageName;
    this.autoUpdateStoreName = autoUpdateStoreName;
  }

  public Single<AutoUpdateViewModel> loadAutoUpdateViewModel() {
    if (loading) {
      return Single.just(new AutoUpdateViewModel(true));
    }
    return retrofit.getJsonResponse(autoUpdateStoreName)
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(jsonResponse -> Observable.just(
            new AutoUpdateViewModel((int) jsonResponse.getVersioncode(), jsonResponse.getUri(),
                jsonResponse.getMd5(), jsonResponse.getMinSdk(), packageName)))
        .onErrorReturn(throwable -> createErrorAutoUpdateViewModel(throwable))
        .toSingle();
  }

  private AutoUpdateViewModel createErrorAutoUpdateViewModel(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new AutoUpdateViewModel(AutoUpdateViewModel.Error.NETWORK);
    } else {
      return new AutoUpdateViewModel(AutoUpdateViewModel.Error.GENERIC);
    }
  }

  public interface Service {
    @GET("latest_version_{storeName}.json") Observable<AutoUpdateJsonResponse> getJsonResponse(
        @Path(value = "storeName") String storeName);
  }
}
