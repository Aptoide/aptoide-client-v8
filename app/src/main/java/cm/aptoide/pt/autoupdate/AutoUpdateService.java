package cm.aptoide.pt.autoupdate;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import retrofit2.http.GET;
import rx.Observable;
import rx.Single;

public class AutoUpdateService {

  private final Service retrofit;
  private final String packageName;
  private boolean loading;

  public AutoUpdateService(Service retrofit, String packageName) {
    this.retrofit = retrofit;
    this.packageName = packageName;
  }

  public Single<AutoUpdateViewModel> loadAutoUpdateViewModel() {
    if (loading) {
      return Single.just(new AutoUpdateViewModel(true));
    }
    return retrofit.getJsonResponse()
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
    @GET("latest_version_v9.json") Observable<AutoUpdateJsonResponse> getJsonResponse();
  }
}
