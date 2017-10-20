package cm.aptoide.pt.view.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Observable;

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
  private final PublishRelay<List<Application>> appsPublisher;
  private final int limit;
  private boolean loading;
  private int initialOffset;

  public AppService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, PublishRelay<List<Application>> appsPublisher,
      int initialOffset, int limit) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.appsPublisher = appsPublisher;
    this.initialOffset = initialOffset;
    this.limit = limit;
  }

  public Observable<List<Application>> getStoreApps() {
    return appsPublisher;
  }

  public Completable loadNextApps(long storeId, boolean bypassCache) {
    if (loading) {
      return Completable.complete();
    }
    ListAppsRequest.Body body =
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeId), limit, sharedPreferences);
    body.setOffset(initialOffset);
    body.setStoreId(storeId);
    return new ListAppsRequest(body, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences).observe(bypassCache)
        .doOnSubscribe(() -> loading = true)
        .doOnNext(listApps -> loading = false)
        .flatMap(listApps -> mapListApps(listApps))
        .doOnNext(listApps -> appsPublisher.call(listApps))
        .toCompletable();
  }

  private Observable<List<Application>> mapListApps(ListApps listApps) {
    if (listApps.isOk()) {
      List<Application> list = new ArrayList<>();
      initialOffset = listApps.getDataList()
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
}
