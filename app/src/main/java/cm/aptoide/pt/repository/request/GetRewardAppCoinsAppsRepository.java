package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.AppCoinsRewardApp;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppCoinsAdsRequest;
import cm.aptoide.pt.home.RewardApp;
import cm.aptoide.pt.view.app.Application;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class GetRewardAppCoinsAppsRepository {

  private static final int APPCOINS_REWARD_LIMIT = 30;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  public GetRewardAppCoinsAppsRepository(OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  public Observable<List<Application>> getAppCoinsRewardAppsFromHomeMore(boolean refresh) {
    return new GetAppCoinsAdsRequest(new GetAppCoinsAdsRequest.Body(0, APPCOINS_REWARD_LIMIT),
        httpClient, converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences).observe(
        refresh)
        .flatMap(response -> mapToRewardApp(response.getDataList()
            .getList()));
  }

  private Observable<List<Application>> mapToRewardApp(List<AppCoinsRewardApp> list) {
    List<Application> rewardAppsList = new ArrayList<>();
    for (AppCoinsRewardApp appCoinsRewardApp : list) {
      rewardAppsList.add(new RewardApp(appCoinsRewardApp.getName(), appCoinsRewardApp.getIcon(),
          appCoinsRewardApp.getStats()
              .getRating()
              .getAvg(), appCoinsRewardApp.getStats()
          .getPdownloads(), appCoinsRewardApp.getPackageName(), appCoinsRewardApp.getId(), "",
          appCoinsRewardApp.getAppcoins()
              .getReward()));
    }
    return Observable.just(rewardAppsList);
  }
}
