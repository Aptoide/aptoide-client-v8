package cm.aptoide.pt.search.view;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.search.SearchManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.Deserializers;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 11/9/17.
 */

public class TrendingManager {
  private static final int SUGGESTION_COUNT = 5;
  private final BaseRequestWithStore.StoreCredentials storeCredentials;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public TrendingManager(BaseRequestWithStore.StoreCredentials storeCredentials, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences){
    this.storeCredentials = storeCredentials;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<List<String>> getTrendingSuggestions(){
    //return ListAppsRequest.ofTrending(V7.getHost(sharedPreferences), SUGGESTION_COUNT, storeCredentials, bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
    //    .observe(true)
    //    .flatMap(response -> Observable.just(response.getDataList().getList()))
    //    .map(data -> Mapper.map(data));
    List<String> test = new ArrayList<>();
    test.add("Facebook");
    test.add("Twitter");
    test.add("Google");
    test.add("Hill Climb Racing");
    test.add("Aptoide");
    return Observable.just(test);
  }

  public static class Mapper{
    public static List<String> map(List<App> apps){
      List<String> result = new ArrayList<>();

      for(int i=0; i<apps.size(); i++){
        result.add(apps.get(i).getName());
      }
      return result;
    }
  }
}
