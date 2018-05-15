package cm.aptoide.pt.view.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;

/**
 * Created by D01 on 04/05/18.
 */

public class DetailedApp {

  private final long id;
  private final String name;
  private final String packageName;
  private final long size;
  private final String icon;
  private final String graphic;
  private final String added;
  private final String modified;
  private final GetAppMeta.GetAppMetaFile file;
  private final GetAppMeta.Developer developer;
  private final Store store;
  private final GetAppMeta.Media media;
  private final GetAppMeta.Stats stats;
  private final Obb obb;
  private final GetAppMeta.Pay pay;
  private final String wUrls;

  public DetailedApp(long id, String name, String packageName, long size, String icon,
      String graphic, String added, String modified, GetAppMeta.GetAppMetaFile file,
      GetAppMeta.Developer developer, Store store, GetAppMeta.Media media, GetAppMeta.Stats stats,
      Obb obb, GetAppMeta.Pay pay, String wUrls) {

    this.id = id;
    this.name = name;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.added = added;
    this.modified = modified;
    this.file = file;
    this.developer = developer;
    this.store = store;
    this.media = media;
    this.stats = stats;
    this.obb = obb;
    this.pay = pay;
    this.wUrls = wUrls;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getSize() {
    return size;
  }

  public String getIcon() {
    return icon;
  }

  public String getGraphic() {
    return graphic;
  }

  public String getAdded() {
    return added;
  }

  public String getModified() {
    return modified;
  }

  public Store getStore() {
    return store;
  }

  public GetAppMeta.Developer getDeveloper() {
    return developer;
  }

  public GetAppMeta.Media getMedia() {
    return media;
  }

  public GetAppMeta.Stats getStats() {
    return stats;
  }

  public Obb getObb() {
    return obb;
  }

  public GetAppMeta.Pay getPay() {
    return pay;
  }

  public GetAppMeta.GetAppMetaFile getFile() {
    return file;
  }

  public String getwUrls() {
    return wUrls;
  }
}
