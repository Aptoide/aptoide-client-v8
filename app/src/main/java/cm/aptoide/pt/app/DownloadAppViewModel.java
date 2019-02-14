package cm.aptoide.pt.app;

import cm.aptoide.pt.app.view.AppCoinsViewModel;

/**
 * Created by D01 on 13/08/2018.
 */

public class DownloadAppViewModel {

  private final DownloadModel downloadModel;
  private SimilarAppsViewModel similarAppsViewModel;
  private SimilarAppsViewModel similarAppcAppsViewModel;
  private AppCoinsViewModel appCoinsViewModel;

  public DownloadAppViewModel(DownloadModel downloadModel,
      SimilarAppsViewModel similarAppsViewModel, SimilarAppsViewModel similarAppcAppsViewModel,
      AppCoinsViewModel appCoinsViewModel) {

    this.downloadModel = downloadModel;
    this.similarAppsViewModel = similarAppsViewModel;
    this.appCoinsViewModel = appCoinsViewModel;
    this.similarAppcAppsViewModel = similarAppcAppsViewModel;
  }

  public AppCoinsViewModel getAppCoinsViewModel() {
    if (appCoinsViewModel == null) {
      appCoinsViewModel = new AppCoinsViewModel();
    }
    return appCoinsViewModel;
  }

  public SimilarAppsViewModel getSimilarAppsViewModel() {
    if (similarAppsViewModel == null) {
      similarAppsViewModel = new SimilarAppsViewModel();
    }
    return similarAppsViewModel;
  }

  public DownloadModel getDownloadModel() {
    return downloadModel;
  }

  public SimilarAppsViewModel getSimilarAppcAppsViewModel() {
    if (similarAppcAppsViewModel == null) {
      similarAppcAppsViewModel = new SimilarAppsViewModel();
    }
    return similarAppcAppsViewModel;
  }
}
