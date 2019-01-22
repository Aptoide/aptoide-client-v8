package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppCoinsCampaignsRequest;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.app.Application;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class RewardAppCoinsAppsRepository {

  private static final int APPCOINS_REWARD_LIMIT = 30;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private InstallManager installManager;

  public RewardAppCoinsAppsRepository(OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, InstallManager installManager) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.installManager = installManager;
  }

  public Observable<List<Application>> getAppCoinsRewardAppsFromHomeMore(boolean refresh,
      String tag) {
    return new GetAppCoinsCampaignsRequest(
        new GetAppCoinsCampaignsRequest.Body(0, APPCOINS_REWARD_LIMIT), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences).observe(refresh)
        .flatMap(response -> map(response.getList(), tag));
  }

  private Observable<List<Application>> map(List<AppCoinsCampaign> list, String tag) {
    List<Application> rewardAppsList = new ArrayList<>();
    for (AppCoinsCampaign campaign : list) {
      App app = campaign.getApp();
      if (!installManager.wasAppEverInstalled(app.getPackageName())) {
        rewardAppsList.add(new Application(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), tag,
            app.getAppcoins() != null && app.getAppcoins()
                .hasBilling(), app.getAppcoins() != null && app.getAppcoins()
            .hasAdvertising()));
      }
    }
    return Observable.just(rewardAppsList);
  }
}
