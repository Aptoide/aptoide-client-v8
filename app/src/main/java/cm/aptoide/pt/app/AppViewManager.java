package cm.aptoide.pt.app;

import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.DetailedApp;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AppViewManager {

  private final UpdatesManager updatesManager;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final AppCenter appCenter;
  private final ReviewsManager reviewsManager;
  private final AdsManager adsManager;
  private PreferencesManager preferencesManager;

  public AppViewManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, PreferencesManager preferencesManager) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.preferencesManager = preferencesManager;
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModel(long appId, String packageName) {
    return appCenter.getDetailedApp(appId, packageName)
        .map(app -> new DetailedAppViewModel(app));
  }

  public Single<ReviewsViewModel> getReviewsViewModel(String storeName, String packageName,
      int maxReviews, String languagesFilterSort, DetailedApp detailedApp) {
    return reviewsManager.loadReviews(storeName, packageName, maxReviews, languagesFilterSort,
        detailedApp)
        .map(reviews -> new ReviewsViewModel(reviews));
  }

  public Single<AdsViewModel> getSimilarApps(String packageName, String storeName,
      List<String> keyWords) {
    List<MinimalAd> similarApps = new ArrayList<>();
    return getAd(packageName, storeName).doOnSuccess(ad -> {
      similarApps.add(ad);
      adsManager.loadSuggestedApps(packageName, keyWords)
          .doOnSuccess(ads -> similarApps.addAll(ads));
    })
        .map(listBuilt -> new AdsViewModel(similarApps));
  }

  private Single<MinimalAd> getAd(String packageName, String storeName) {
    return adsManager.loadAd(packageName, storeName);
  }

  public void increaseInstallClick() {
    preferencesManager.setNotLoggedInInstallClicks();
  }

  public boolean showRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void saveRootInstallWarning(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }
}
