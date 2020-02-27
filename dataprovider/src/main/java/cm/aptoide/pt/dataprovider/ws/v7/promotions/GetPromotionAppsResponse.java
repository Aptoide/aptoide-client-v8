package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import cm.aptoide.pt.dataprovider.model.v7.AppCoinsCampaign;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;

public class GetPromotionAppsResponse
    extends BaseV7EndlessDataListResponse<GetPromotionAppsResponse.PromotionAppModel> {

  public GetPromotionAppsResponse() {
  }

  public static class PromotionAppModel {
    private boolean claimed;
    private float appc;
    private String promotionDescription;
    private AppCoinsCampaign.Fiat fiat;
    private GetAppMeta.App app;

    public PromotionAppModel() {
    }

    public boolean isClaimed() {
      return claimed;
    }

    public void setClaimed(boolean claimed) {
      this.claimed = claimed;
    }

    public float getAppc() {
      return appc;
    }

    public void setAppc(float appc) {
      this.appc = appc;
    }

    public GetAppMeta.App getApp() {
      return app;
    }

    public void setApp(GetAppMeta.App app) {
      this.app = app;
    }

    public String getPromotionDescription() {
      return promotionDescription;
    }

    public void setPromotionDescription(String description) {
      this.promotionDescription = description;
    }

    public AppCoinsCampaign.Fiat getFiat() {
      return fiat;
    }

    public void setFiat(AppCoinsCampaign.Fiat fiat) {
      this.fiat = fiat;
    }
  }
}
