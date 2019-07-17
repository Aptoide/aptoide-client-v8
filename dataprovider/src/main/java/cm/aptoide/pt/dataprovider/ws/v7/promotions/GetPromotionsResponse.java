package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;

public class GetPromotionsResponse
    extends BaseV7EndlessDataListResponse<GetPromotionsResponse.PromotionModel> {

  public GetPromotionsResponse() {
  }

  public static class PromotionModel {
    private String title;
    private String promotionId;
    private String type;
    private String background;

    public PromotionModel() {
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getPromotionId() {
      return promotionId;
    }

    public void setPromotionId(String promotionId) {
      this.promotionId = promotionId;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getBackground() {
      return background;
    }

    public void setBackground(String background) {
      this.background = background;
    }
  }
}
