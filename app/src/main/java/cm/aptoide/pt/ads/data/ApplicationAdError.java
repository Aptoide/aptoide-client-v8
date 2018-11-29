package cm.aptoide.pt.ads.data;

import cm.aptoide.pt.view.app.AppsList;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class ApplicationAdError {
  private final AppsList.Error minimalAdError;

  public ApplicationAdError(AppsList.Error minimalAdError) {
    this.minimalAdError = minimalAdError;
  }

  public boolean hasError() {
    return minimalAdError != null;
  }

  public String getErrorMessage() {
    if (minimalAdError != null) return minimalAdError.name();
    return null;
  }
}
