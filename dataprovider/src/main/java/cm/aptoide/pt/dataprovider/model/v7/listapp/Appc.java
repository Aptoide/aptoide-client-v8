package cm.aptoide.pt.dataprovider.model.v7.listapp;

public class Appc {
  private Ads ads;
  private boolean iab;

  public Appc() {
  }

  public Ads getAds() {
    return ads;
  }

  public void setAds(Ads ads) {
    this.ads = ads;
  }

  public boolean hasIab() {
    return iab;
  }

  public void setIab(boolean iab) {
    this.iab = iab;
  }

  public boolean hasAds() {
    return ads != null;
  }

  public class Ads {
    private String reward;

    public Ads(String reward) {
      this.reward = reward;
    }

    public String getReward() {
      return reward;
    }

    public void setReward(String reward) {
      this.reward = reward;
    }
  }
}
