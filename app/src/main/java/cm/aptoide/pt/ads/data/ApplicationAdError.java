package cm.aptoide.pt.ads.data;

import cm.aptoide.pt.view.app.AppsList;
import com.appnext.core.AppnextError;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class ApplicationAdError {
  private final AppnextError nativeAdError;
  private final AppsList.Error minimalAdError;

  public ApplicationAdError(AppnextError nativeAdError) {
    this.minimalAdError = null;
    this.nativeAdError = nativeAdError;
  }

  public ApplicationAdError(AppsList.Error minimalAdError) {
    this.minimalAdError = minimalAdError;
    this.nativeAdError = null;
  }

  public boolean hasError() {
    return nativeAdError != null || minimalAdError != null;
  }

  public String getErrorMessage() {
    if (nativeAdError != null) return nativeAdError.getErrorMessage();
    if (minimalAdError != null) return minimalAdError.name();
    return null;
  }
}
