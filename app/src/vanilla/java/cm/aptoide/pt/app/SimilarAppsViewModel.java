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
  private final AppsList.Error error;

  public SimilarAppsViewModel(MinimalAd ad, List<Application> recommendedApps, boolean loading,
      AppsList.Error error) {
    this.ad = ad;
    this.recommendedApps = recommendedApps;
    this.loading = loading;
    this.error = error;
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

  public AppsList.Error getError() {
    return error;
  }

  public boolean hasError() {
    return (error != null);
  }
}
