package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

public class GetPromotionAppsResponse
    extends BaseV7EndlessDataListResponse<GetPromotionAppsResponse.PromotionAppModel> {

  public GetPromotionAppsResponse() {
  }

  public static class PromotionAppModel {
    private boolean claimed;
    private float appc;
    private String promotionDescription;
    private App app;

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

    public App getApp() {
      return app;
    }

    public void setApp(App app) {
      this.app = app;
    }

    public String getPromotionDescription() {
      return promotionDescription;
    }

    public void setPromotionDescription(String description) {
      this.promotionDescription = description;
    }
  }
}
