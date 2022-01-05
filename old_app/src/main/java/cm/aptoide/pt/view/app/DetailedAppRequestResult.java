package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 10/05/2018.
 */

public class DetailedAppRequestResult {
  private final DetailedApp detailedApp;
  private final boolean loading;
  private final Error error;

  public DetailedAppRequestResult(Error error) {
    this.detailedApp = null;
    this.loading = false;
    this.error = error;
  }

  public DetailedAppRequestResult(DetailedApp detailedApp) {
    this.detailedApp = detailedApp;
    this.loading = false;
    this.error = null;
  }

  public DetailedAppRequestResult(boolean loading) {
    this.detailedApp = null;
    this.loading = loading;
    this.error = null;
  }

  public DetailedApp getDetailedApp() {
    return detailedApp;
  }

  public boolean isLoading() {
    return loading;
  }

  public boolean hasError() {
    return (error != null);
  }

  public Error getError() {
    return error;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
