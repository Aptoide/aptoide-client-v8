package cm.aptoide.pt.app;

import com.appnext.core.AppnextError;
import com.appnext.nativeads.NativeAd;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class SimilarAppsViewModel {

  private final NativeAd ad;
  private final List<Application> recommendedApps;
  private final boolean loading;
  private final AppsList.Error recommendedAppsError;
  private final AppnextError adError;

  public SimilarAppsViewModel(NativeAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error recommendedAppsError, AppnextError adResultError) {
    this.ad = ad;
    this.recommendedApps = recommendedApps;
    this.loading = loading;
    this.recommendedAppsError = recommendedAppsError;
    this.adError = adResultError;
  }

  public SimilarAppsViewModel() {
    this.ad = null;
    this.recommendedApps = Collections.emptyList();
    this.loading = false;
    this.recommendedAppsError = null;
    this.adError = null;
  }

  public NativeAd getAd() {
    return ad;
  }

  public List<Application> getRecommendedApps() {
    return recommendedApps;
  }

  public boolean isLoading() {
    return loading;
  }

  public AppsList.Error getRecommendedAppsError() {
    return recommendedAppsError;
  }

  public boolean hasSimilarApps() {
    return !hasRecommendedAppsError() && (!recommendedApps.isEmpty() || ad != null);
  }

  public boolean hasError() {
    return (recommendedAppsError != null || adError != null);
  }

  public AppnextError getAdError() {
    return adError;
  }

  public boolean hasAd() {
    return (ad != null);
  }

  public boolean hasRecommendedAppsError() {
    return (recommendedAppsError != null);
  }

  public boolean hasAdError() {
    return (adError != null);
  }

}
