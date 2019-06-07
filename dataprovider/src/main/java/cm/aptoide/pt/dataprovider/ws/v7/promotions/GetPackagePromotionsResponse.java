package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;

public class GetPackagePromotionsResponse
    extends BaseV7EndlessDataListResponse<GetPackagePromotionsResponse.PromotionAppModel> {
  public GetPackagePromotionsResponse() {
  }

  public static class PromotionAppModel {
    private boolean claimed;
    private float appc;
    private String packageName;
    private String promotionId;

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

    public String getPromotionId() {
      return promotionId;
    }

    public void setPromotionId(String promotionId) {
      this.promotionId = promotionId;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
  }
}
