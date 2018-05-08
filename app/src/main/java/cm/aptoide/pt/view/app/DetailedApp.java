package cm.aptoide.pt.view.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by D01 on 04/05/18.
 */

public class DetailedApp {

  private long id;
  private String name;
  @JsonProperty("package") private String packageName;
  private long size;
  private String icon;
  private String graphic;
  private String added;
  private String modified;
  private GetAppMeta.GetAppMetaFile file;
  private GetAppMeta.Developer developer;
  private Store store;
  private GetAppMeta.Media media;
  private GetAppMeta.Stats stats;
  private Obb obb;
  private GetAppMeta.Pay pay;

  public DetailedApp(long id, String name, String packageName, long size, String icon,
      String graphic, String added, String modified, GetAppMeta.GetAppMetaFile file,
      GetAppMeta.Developer developer, Store store, GetAppMeta.Media media, GetAppMeta.Stats stats,
      Obb obb, GetAppMeta.Pay pay) {

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
}
