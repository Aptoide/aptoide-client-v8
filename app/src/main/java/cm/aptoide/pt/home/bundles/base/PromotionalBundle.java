package cm.aptoide.pt.home.bundles.base;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.ApplicationGraphic;
import java.util.Collections;
import java.util.List;

public class PromotionalBundle implements HomeBundle {
  private final String title;
  private final BundleType type;
  private final Event event;
  private final String tag;
  private final ApplicationGraphic app;
  private final DownloadModel downloadModel;
  private final int bonusPercentage;

  public PromotionalBundle(String title, BundleType type, Event event, String tag,
      ApplicationGraphic app, DownloadModel downloadModel, int bonusPercentage) {
    this.title = title;
    this.type = type;
    this.event = event;
    this.tag = tag;
    this.app = app;
    this.downloadModel = downloadModel;
    this.bonusPercentage = bonusPercentage;
  }

  @Override public String getTitle() {
    return this.title;
  }

  @Override public List<?> getContent() {
    return app != null ? Collections.emptyList() : null;
  }

  @Override public BundleType getType() {
    return this.type;
  }

  @Override public Event getEvent() {
    return this.event;
  }

  @Override public String getTag() {
    return this.tag;
  }

  public ApplicationGraphic getApp() {
    return app;
  }

  public int getBonusPercentage() {
    return bonusPercentage;
  }

  public DownloadModel getDownloadModel() {
    return downloadModel;
  }
}
