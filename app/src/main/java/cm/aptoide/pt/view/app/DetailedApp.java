package cm.aptoide.pt.view.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import java.util.List;

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
  private final boolean isGoodApp;
  private final Malware malware;
  private final AppFlags appFlags;
  private final List<String> tags;
  private final List<String> usedFeatures;
  private final List<String> usedPermissions;
  private final long fileSize;
  private final String md5;
  private final String md5Sum;
  private final String path;
  private final String pathAlt;
  private final int verCode;
  private final String verName;
  private final GetAppMeta.Developer developer;
  private final Store store;
  private final GetAppMeta.Media media;
  private final GetAppMeta.Stats stats;
  private final Obb obb;
  private final GetAppMeta.Pay pay;
  private final String wUrls;

  public DetailedApp(long id, String name, String packageName, long size, String icon,
      String graphic, String added, String modified, boolean isGoodApp, Malware malware,
      AppFlags appFlags, List<String> tags, List<String> usedFeatures, List<String> usedPermissions,
      long fileSize, String md5, String md5Sum, String path, String pathAlt, int verCode,
      String verName, GetAppMeta.Developer developer, Store store, GetAppMeta.Media media,
      GetAppMeta.Stats stats, Obb obb, GetAppMeta.Pay pay, String wUrls) {

    this.id = id;
    this.name = name;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.added = added;
    this.modified = modified;
    this.isGoodApp = isGoodApp;
    this.malware = malware;
    this.appFlags = appFlags;
    this.tags = tags;
    this.usedFeatures = usedFeatures;
    this.usedPermissions = usedPermissions;
    this.fileSize = fileSize;
    this.md5 = md5;
    this.md5Sum = md5Sum;
    this.path = path;
    this.pathAlt = pathAlt;
    this.verCode = verCode;
    this.verName = verName;
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

  public String getwUrls() {
    return wUrls;
  }

  public AppFlags getAppFlags() {
    return appFlags;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<String> getUsedFeatures() {
    return usedFeatures;
  }

  public List<String> getUsedPermissions() {
    return usedPermissions;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getMd5() {
    return md5;
  }

  public String getMd5Sum() {
    return md5Sum;
  }

  public String getPath() {
    return path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public int getVerCode() {
    return verCode;
  }

  public String getVerName() {
    return verName;
  }

  public boolean isGoodApp() {
    return isGoodApp;
  }

  public Malware getMalware() {
    return malware;
  }
}
