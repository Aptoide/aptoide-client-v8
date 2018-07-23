package cm.aptoide.pt.dataprovider.model.v7.listapp;

public class AppCoinsInfo {
  private String reward;
  private boolean advertising;
  private boolean billing;

  public AppCoinsInfo() {
  }

  public String getReward() {
    return reward;
  }

  public void setReward(String reward) {
    this.reward = reward;
  }

  public boolean hasAdvertising() {
    return advertising;
  }

  public void setAdvertising(boolean advertising) {
    this.advertising = advertising;
  }

  public boolean hasBilling() {
    return billing;
  }

  public void setBilling(boolean billing) {
    this.billing = billing;
  }
}
