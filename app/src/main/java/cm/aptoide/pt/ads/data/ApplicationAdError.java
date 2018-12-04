package cm.aptoide.pt.ads.data;

import cm.aptoide.pt.view.app.AppsList;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class ApplicationAdError {
  private final AppsList.Error minimalAdError;
  private final boolean hasOtherNetworkError;

  public ApplicationAdError(AppsList.Error minimalAdError) {
    this.minimalAdError = minimalAdError;
    this.hasOtherNetworkError = false;
  }

  public ApplicationAdError() {
    this.minimalAdError = null;
    this.hasOtherNetworkError = true;
  }

  public boolean hasError() {
    return minimalAdError != null || hasOtherNetworkError;
  }

  public String getErrorMessage() {
    if (minimalAdError != null) return minimalAdError.name();
    return null;
  }
}
