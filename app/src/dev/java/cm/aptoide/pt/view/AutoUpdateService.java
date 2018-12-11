package cm.aptoide.pt.view;

import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import retrofit2.http.GET;
import rx.Observable;
import rx.Single;

public class AutoUpdateService {

  private final Service retrofit;
  private boolean loading;

  public AutoUpdateService(Service retrofit) {
    this.retrofit = retrofit;
  }

  public Single<AutoUpdateModel> loadAutoUpdateModel() {
    if (loading) {
      return Single.just(new AutoUpdateModel(true));
    }
    return retrofit.getJsonResponse()
        .doOnSubscribe(() -> loading = true)
        .doOnUnsubscribe(() -> loading = false)
        .doOnTerminate(() -> loading = false)
        .flatMap(jsonResponse -> Observable.just(
            new AutoUpdateModel(jsonResponse.getVersioncode(), jsonResponse.getUri(),
                jsonResponse.getMd5(), jsonResponse.getMinSdk())))
        .toSingle()
        .onErrorReturn(throwable -> createErrorEditorialModel(throwable));
  }

  private AutoUpdateModel createErrorEditorialModel(Throwable throwable) {
    if (throwable instanceof NoNetworkConnectionException) {
      return new AutoUpdateModel(AutoUpdateModel.Error.NETWORK);
    } else {
      return new AutoUpdateModel(AutoUpdateModel.Error.GENERIC);
    }
  }

  public interface Service {
    @GET("latest_version_v9.json") Observable<AutoUpdateJsonResponse> getJsonResponse();
  }
}
