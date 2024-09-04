package cm.aptoide.pt.repository.request;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppCoinsCampaignsRequest;
import cm.aptoide.pt.home.bundles.apps.RewardApp;
import cm.aptoide.pt.install.InstallManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/27/18.
 */

public class RewardAppCoinsAppsRepository {

  private static final int APPCOINS_REWARD_LIMIT = 50;

  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;
  private InstallManager installManager;

  private int total = 0;
  private int next = 0;

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

  public Observable<List<RewardApp>> getFreshAppCoinsRewardAppsFromHomeMore(String tag) {
    return new GetAppCoinsCampaignsRequest(
        new GetAppCoinsCampaignsRequest.Body(0, APPCOINS_REWARD_LIMIT), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences).observe(true)
        .flatMap(response -> map(response.getDataList(), tag));
  }

  public Observable<List<RewardApp>> getNextAppCoinsRewardAppsFromHomeMore(String tag) {
    if (next >= total) {
      return Observable.just(Collections.emptyList());
    }
    return new GetAppCoinsCampaignsRequest(
        new GetAppCoinsCampaignsRequest.Body(next, APPCOINS_REWARD_LIMIT), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, sharedPreferences).observe(false)
        .flatMap(response -> map(response.getDataList(), tag));
  }

  private Observable<List<RewardApp>> map(DataList<AppCoinsCampaign> list, String tag) {
    this.total = list.getTotal();
    this.next = list.getNext();
    List<RewardApp> rewardAppsList = new ArrayList<>();
    for (AppCoinsCampaign campaign : list.getList()) {
      AppCoinsCampaign.CampaignApp app = campaign.getApp();
      if (!installManager.wasAppEverInstalled(app.getPackageName())) {
        rewardAppsList.add(new RewardApp(app.getName(), app.getIcon(), app.getStats()
            .getRating()
            .getAvg(), app.getStats()
            .getPdownloads(), app.getPackageName(), app.getId(), tag, app.getAppcoins() != null,
            app.getAppcoins()
                .getClicks()
                .getClick(), app.getAppcoins()
            .getClicks()
            .getInstall(), mapReward(campaign.getReward()), app.getGraphic()));
      }
    }
    return Observable.just(rewardAppsList);
  }

  private RewardApp.Reward mapReward(AppCoinsCampaign.Reward reward) {
    AppCoinsCampaign.Fiat fiat = reward.getFiat();
    return new RewardApp.Reward(reward.getAppc(),
        new RewardApp.Fiat(fiat.getAmount(), fiat.getCurrency(), fiat.getSymbol()));
  }
}
