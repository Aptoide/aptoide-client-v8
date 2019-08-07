package cm.aptoide.pt.app;

import cm.aptoide.pt.app.view.AppCoinsViewModel;

/**
 * This class holds the essential info/state of the AppView.
 * This means that only info that is needed and shared throughout other appview components is here.
 *
 * Self-contained models, such as reviews, should not be here.
 */
public class AppViewModel {

  private AppModel appModel;
  private DownloadModel downloadModel;
  private AppCoinsViewModel appCoinsViewModel;
  private MigrationModel migrationModel;

  public AppViewModel(AppModel appModel, DownloadModel downloadModel,
      AppCoinsViewModel appCoinsViewModel, MigrationModel migrationModel) {
    this.appModel = appModel;
    this.downloadModel = downloadModel;
    this.appCoinsViewModel = appCoinsViewModel;
    this.migrationModel = migrationModel;
  }

  public AppModel getAppModel() {
    return appModel;
  }

  public DownloadModel getDownloadModel() {
    return downloadModel;
  }

  public void setDownloadModel(DownloadModel downloadModel) {
    this.downloadModel = downloadModel;
  }

  public AppCoinsViewModel getAppCoinsViewModel() {
    return appCoinsViewModel;
  }

  public MigrationModel getMigrationModel() {
    return migrationModel;
  }

}
