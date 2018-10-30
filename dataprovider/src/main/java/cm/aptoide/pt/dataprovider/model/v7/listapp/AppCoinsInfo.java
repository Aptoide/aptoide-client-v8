package cm.aptoide.pt.dataprovider.model.v7.listapp;

import java.util.List;

public class AppCoinsInfo {
  private boolean advertising;
  private boolean billing;
  private List<String> flags;

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

  public List<String> getFlags() {
    return flags;
  }

  public void setFlags(List<String> flags) {
    this.flags = flags;
  }
}
