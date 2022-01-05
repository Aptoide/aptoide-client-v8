package cm.aptoide.pt.home.bundles.base;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.ApplicationGraphic;

public class BonusPromotionalBundle extends PromotionalBundle {
  private final int bonusPercentage;

  public BonusPromotionalBundle(String title, BundleType type, Event event, String tag,
      ApplicationGraphic app, DownloadModel downloadModel, int bonusPercentage) {
    super(title, type, event, tag, app, downloadModel);
    this.bonusPercentage = bonusPercentage;
  }

  public int getBonusPercentage() {
    return bonusPercentage;
  }
}
