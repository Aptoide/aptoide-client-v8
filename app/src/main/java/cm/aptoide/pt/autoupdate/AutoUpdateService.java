package cm.aptoide.pt.autoupdate;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import retrofit2.http.GET;
import rx.Observable;

public class AutoUpdateService {

  private final Service retrofit;
  private boolean loading;

  public AutoUpdateService(Service retrofit) {
    this.retrofit = retrofit;
  }

  public Observable<AutoUpdateViewModel> loadAutoUpdateViewModel() {
    if (loading) {
      return Observable.just(new AutoUpdateViewModel(true));
    }
    return retrofit.getJsonResponse()
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(jsonResponse -> Observable.just(
            new AutoUpdateViewModel(jsonResponse.getVersioncode(), jsonResponse.getUri(),
                jsonResponse.getMd5(), jsonResponse.getMinSdk())))
        .onErrorReturn(throwable -> createErrorAutoUpdateViewModel(throwable));
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
