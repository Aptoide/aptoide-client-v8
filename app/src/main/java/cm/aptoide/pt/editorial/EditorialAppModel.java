package cm.aptoide.pt.editorial;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.List;

public class EditorialAppModel {
  private final EditorialDownloadModel downloadModel;

  private final long id;
  private final String name;
  private final String icon;
  private final float avg;
  private final String packageName;
  private final long size;
  private final String graphic;
  private final Obb obb;
  private final long storeId;
  private final String storeName;
  private final String verName;
  private final int verCode;
  private final String path;
  private final String pathAlt;
  private final String md5sum;
  private final List<Split> splits;
  private final List<String> requiredSplits;
  private final boolean hasAppc;
  private final String rank;

  public EditorialAppModel(EditorialAppModel editorialAppModel,
      EditorialDownloadModel downloadModel) {
    this.downloadModel = downloadModel;
    this.id = editorialAppModel.getId();
    this.name = editorialAppModel.getName();
    this.icon = editorialAppModel.getIcon();
    this.avg = editorialAppModel.getAvg();
    this.packageName = editorialAppModel.getPackageName();
    this.size = editorialAppModel.getSize();
    this.graphic = editorialAppModel.getGraphic();
    this.obb = editorialAppModel.getObb();
    this.storeId = editorialAppModel.getStoreId();
    this.storeName = editorialAppModel.getStoreName();
    this.verName = editorialAppModel.getVerName();
    this.verCode = editorialAppModel.getVerCode();
    this.path = editorialAppModel.getPath();
    this.pathAlt = editorialAppModel.getPathAlt();
    this.md5sum = editorialAppModel.getMd5sum();
    this.splits = editorialAppModel.getSplits();
    this.requiredSplits = editorialAppModel.getRequiredSplits();
    this.hasAppc = editorialAppModel.hasAppc();
    this.rank = editorialAppModel.getRank();
  }

  public EditorialAppModel(long id, String name, String icon, float avg, String packageName,
      long size, String graphic, Obb obb, long storeId, String storeName, String verName,
      int verCode, String path, String pathAlt, String md5sum, List<Split> splits,
      List<String> requiredSplits, boolean hasAppc, String rank) {
    this.downloadModel = null;
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.avg = avg;
    this.packageName = packageName;
    this.size = size;
    this.graphic = graphic;
    this.obb = obb;
    this.storeId = storeId;
    this.storeName = storeName;
    this.verName = verName;
    this.verCode = verCode;
    this.path = path;
    this.pathAlt = pathAlt;
    this.md5sum = md5sum;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.hasAppc = hasAppc;
    this.rank = rank;
  }

  public EditorialDownloadModel getDownloadModel() {
    return downloadModel;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public float getAvg() {
    return avg;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getSize() {
    return size;
  }

  public String getGraphic() {
    return graphic;
  }

  public Obb getObb() {
    return obb;
  }

  public long getStoreId() {
    return storeId;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getVerName() {
    return verName;
  }

  public int getVerCode() {
    return verCode;
  }

  public String getPath() {
    return path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public String getMd5sum() {
    return md5sum;
  }

  public List<Split> getSplits() {
    return splits;
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public boolean hasAppc() {
    return hasAppc;
  }

  public String getRank() {
    return rank;
  }

  public boolean hasSplits() {
    return splits != null && !splits.isEmpty();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EditorialAppModel)) return false;

    EditorialAppModel appModel = (EditorialAppModel) o;

    if (id != appModel.id) return false;
    if (Float.compare(appModel.avg, avg) != 0) return false;
    if (size != appModel.size) return false;
    if (storeId != appModel.storeId) return false;
    if (verCode != appModel.verCode) return false;
    if (hasAppc != appModel.hasAppc) return false;
    if (downloadModel != null ? !downloadModel.equals(appModel.downloadModel)
        : appModel.downloadModel != null) {
      return false;
    }
    if (name != null ? !name.equals(appModel.name) : appModel.name != null) return false;
    if (icon != null ? !icon.equals(appModel.icon) : appModel.icon != null) return false;
    if (packageName != null ? !packageName.equals(appModel.packageName)
        : appModel.packageName != null) {
      return false;
    }
    if (graphic != null ? !graphic.equals(appModel.graphic) : appModel.graphic != null) {
      return false;
    }
    if (obb != null ? !obb.equals(appModel.obb) : appModel.obb != null) return false;
    if (storeName != null ? !storeName.equals(appModel.storeName) : appModel.storeName != null) {
      return false;
    }
    if (verName != null ? !verName.equals(appModel.verName) : appModel.verName != null) {
      return false;
    }
    if (path != null ? !path.equals(appModel.path) : appModel.path != null) return false;
    if (pathAlt != null ? !pathAlt.equals(appModel.pathAlt) : appModel.pathAlt != null) {
      return false;
    }
    if (md5sum != null ? !md5sum.equals(appModel.md5sum) : appModel.md5sum != null) return false;
    if (splits != null ? !splits.equals(appModel.splits) : appModel.splits != null) return false;
    if (requiredSplits != null ? !requiredSplits.equals(appModel.requiredSplits)
        : appModel.requiredSplits != null) {
      return false;
    }
    return rank != null ? rank.equals(appModel.rank) : appModel.rank == null;
  }

  @Override public int hashCode() {
    int result = downloadModel != null ? downloadModel.hashCode() : 0;
    result = 31 * result + (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (icon != null ? icon.hashCode() : 0);
    result = 31 * result + (avg != +0.0f ? Float.floatToIntBits(avg) : 0);
    result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
    result = 31 * result + (int) (size ^ (size >>> 32));
    result = 31 * result + (graphic != null ? graphic.hashCode() : 0);
    result = 31 * result + (obb != null ? obb.hashCode() : 0);
    result = 31 * result + (int) (storeId ^ (storeId >>> 32));
    result = 31 * result + (storeName != null ? storeName.hashCode() : 0);
    result = 31 * result + (verName != null ? verName.hashCode() : 0);
    result = 31 * result + verCode;
    result = 31 * result + (path != null ? path.hashCode() : 0);
    result = 31 * result + (pathAlt != null ? pathAlt.hashCode() : 0);
    result = 31 * result + (md5sum != null ? md5sum.hashCode() : 0);
    result = 31 * result + (splits != null ? splits.hashCode() : 0);
    result = 31 * result + (requiredSplits != null ? requiredSplits.hashCode() : 0);
    result = 31 * result + (hasAppc ? 1 : 0);
    result = 31 * result + (rank != null ? rank.hashCode() : 0);
    return result;
  }
}
