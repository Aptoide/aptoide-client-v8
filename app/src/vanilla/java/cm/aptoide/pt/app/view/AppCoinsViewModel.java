package cm.aptoide.pt.app.view;

public class AppCoinsViewModel {
  private final boolean loading;
  private final boolean hasBilling;
  private final boolean hasAdvertising;

  public AppCoinsViewModel(boolean loading, boolean hasBilling, boolean hasAdvertising) {
    this.loading = loading;
    this.hasBilling = hasBilling;
    this.hasAdvertising = hasAdvertising;
  }

  public AppCoinsViewModel() {
    this.loading = false;
    this.hasBilling = false;
    this.hasAdvertising = false;
  }

  public boolean isLoading() {
    return loading;
  }

  public boolean hasBilling() {
    return hasBilling;
  }

  public boolean hasAdvertising() {
    return hasAdvertising;
  }
}
