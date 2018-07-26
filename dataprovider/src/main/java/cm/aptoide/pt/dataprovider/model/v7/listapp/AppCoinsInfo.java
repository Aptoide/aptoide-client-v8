package cm.aptoide.pt.dataprovider.model.v7.listapp;

public class AppCoinsInfo {
  private boolean advertising;
  private boolean billing;

  public AppCoinsInfo() {
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
