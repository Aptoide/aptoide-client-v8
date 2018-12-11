package cm.aptoide.pt.dataprovider.ws.v7.promotions;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.List;

public class GetPromotionAppsResponse extends BaseV7Response {

  private Datalist datalist;

  public GetPromotionAppsResponse() {
  }

  public Datalist getDatalist() {
    return datalist;
  }

  public void setDatalist(Datalist datalist) {
    this.datalist = datalist;
  }

  public static class Datalist {
    private int total;
    private List<PromotionAppModel> list;

    public Datalist() {
    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public List<PromotionAppModel> getList() {
      return list;
    }

    public void setList(List<PromotionAppModel> list) {
      this.list = list;
    }
  }

  public static class PromotionAppModel {
    private boolean claimed;
    private float appc;
    private String description;
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

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }
  }
}
