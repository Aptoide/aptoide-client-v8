package cm.aptoide.pt.view.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by trinkes on 18/10/2017.
 */

public class AppService {
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final int initialOffset;
  private int limit;
  private boolean loading;
  private int offset;

  public AppService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int initialOffset, int limit) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.initialOffset = initialOffset;
    this.limit = limit;
  }

  public Single<List<Application>> loadNextApps(long storeId, boolean bypassCache) {
    if (loading) {
      return Single.error(new AlreadyLoadingException());
    }
    ListAppsRequest.Body body =
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), limit, sharedPreferences);
    body.setOffset(offset);
    body.setStoreId(storeId);
    return new ListAppsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences).observe(bypassCache)
        .doOnSubscribe(() -> loading = true)
        .doOnNext(listApps -> loading = false)
        .flatMap(listApps -> mapListApps(listApps))
        .toSingle();
  }

  private Observable<List<Application>> mapListApps(ListApps listApps) {
    if (listApps.isOk()) {
      List<Application> list = new ArrayList<>();
      offset = listApps.getDataList()
          .getNext();
      for (App app : listApps.getDataList()
          .getList()) {
        list.add(new Application(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getDownloads(), app.getPackageName(), app.getId()));
      }
      return Observable.just(list);
    } else {
      return Observable.error(new IllegalStateException("Could not obtain timeline from server."));
    }
  }

  public Single<List<Application>> loadFreshApps(long storeId) {
    offset = initialOffset;
    return loadNextApps(storeId, true);
  }

  public Single<List<Application>> loadNextApps(long storeId) {
    return loadNextApps(storeId, false);
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
