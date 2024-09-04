package cm.aptoide.pt.app;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.ListAppCoinsCampaigns;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppCoinsCampaignsRequest;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class AppCoinsService {
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences preferences;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final Converter.Factory converterFactory;

  public AppCoinsService(OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences preferences, BodyInterceptor<BaseBody> bodyInterceptor,
      Converter.Factory converterFactory) {
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.preferences = preferences;
    this.bodyInterceptor = bodyInterceptor;
    this.converterFactory = converterFactory;
  }

  public Single<AppCoinsAdvertisingModel> getValidCampaign(String packageName, int versionCode) {
    return new GetAppCoinsCampaignsRequest(
        new GetAppCoinsCampaignsRequest.Body(packageName, versionCode), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, preferences).observe()
        .toSingle()
        .map(listAppCoinsCampaigns -> mapAdvertising(listAppCoinsCampaigns));
  }

  private AppCoinsAdvertisingModel mapAdvertising(ListAppCoinsCampaigns listAppCoinsCampaigns) {
    List<AppCoinsCampaign> list = listAppCoinsCampaigns.getDataList()
        .getList();
    if (list.isEmpty()) {
      return new AppCoinsAdvertisingModel();
    } else {
      double appcReward = list.get(0)
          .getReward()
          .getAppc();

      double fiatReward = list.get(0)
          .getReward()
          .getFiat()
          .getAmount();

      String fiatCurrency = list.get(0)
          .getReward()
          .getFiat()
          .getSymbol();

      double appcBudget = list.get(0)
          .getBudget() != null ? list.get(0)
          .getBudget()
          .getAppc() : -1.0;

      String endDate = list.get(0)
          .getEndDate() != null ? list.get(0)
          .getEndDate() : "";

      return new AppCoinsAdvertisingModel(appcReward, true, fiatReward, fiatCurrency, appcBudget,
          endDate);
    }
  }
}
