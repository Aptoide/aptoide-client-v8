package cm.aptoide.pt.app;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.AppsList;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class SimilarAppsViewModel {

  private final MinimalAd ad;
  private final List<Application> recommendedApps;
  private final boolean loading;
  private final AppsList.Error recommendedAppsError;
  private final AppsList.Error adResultError;

  public SimilarAppsViewModel(MinimalAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error recommendedAppsError, AppsList.Error adResultError) {
    this.ad = ad;
    this.recommendedApps = recommendedApps;
    this.loading = loading;
    this.recommendedAppsError = recommendedAppsError;
    this.adResultError = adResultError;
  }

  public MinimalAd getAd() {
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

  public boolean hasError() {
    return (recommendedAppsError != null || adResultError != null);
  }

  public AppsList.Error getAdResultError() {
    return adResultError;
  }

  public AppsList.Error getError() {
    if (getRecommendedAppsError() != null) {
      return getRecommendedAppsError();
    } else {
      return getAdResultError();
    }
  }
}
