package cm.aptoide.pt.dataprovider.model.v7.listapp;

public class Appc {
  private Ads ads;
  private boolean iab;

  public Appc() {
  }

  public void setAds(Ads ads) {
    this.ads = ads;
  }

  public void setIab(boolean iab) {
    this.iab = iab;
  }

  private class Ads {
    private String reward;

    public Ads(String reward) {
      this.reward = reward;
    }

    public void setReward(String reward) {
      this.reward = reward;
    }
  }
}
