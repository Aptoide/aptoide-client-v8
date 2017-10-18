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

  public AppService(StoreCredentialsProvider storeCredentialsProvider,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<List<Application>> getStoreApps(long storeName, boolean bypassCache) {
    return new ListAppsRequest(
        new ListAppsRequest.Body(storeCredentialsProvider.get(storeName), sharedPreferences),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences).observe(
        bypassCache)
        .flatMap(listApps -> mapListApps(listApps));
  }

  private Observable<List<Application>> mapListApps(ListApps listApps) {
    if (listApps.isOk()) {
      List<Application> list = new ArrayList<>();
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
