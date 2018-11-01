package cm.aptoide.pt.app;

import cm.aptoide.pt.ads.model.ApplicationAd;
import cm.aptoide.pt.ads.model.ApplicationAdError;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class SimilarAppsViewModel {

  private final ApplicationAd ad;
  private final List<Application> recommendedApps;
  private final boolean loading;
  private final AppsList.Error recommendedAppsError;
  private final ApplicationAdError adError;

  public SimilarAppsViewModel(ApplicationAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error recommendedAppsError, ApplicationAdError adResultError) {
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

  public ApplicationAd getAd() {
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

  public ApplicationAdError getAdError() {
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
