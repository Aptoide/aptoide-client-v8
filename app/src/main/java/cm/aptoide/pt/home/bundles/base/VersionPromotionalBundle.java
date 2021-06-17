package cm.aptoide.pt.home.bundles.base;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.ApplicationGraphic;

public class VersionPromotionalBundle extends PromotionalBundle {
  private final String versionName;

  public VersionPromotionalBundle(String title, BundleType type, Event event, String tag,
      ApplicationGraphic app, String versionName, DownloadModel downloadModel) {
    super(title, type, event, tag, app, downloadModel);
    this.versionName = versionName;
  }

  public String getVersionName() {
    return versionName;
  }
}
