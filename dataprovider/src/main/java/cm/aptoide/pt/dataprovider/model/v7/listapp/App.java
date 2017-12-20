/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.listapp;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by neuro on 22-04-2016.
 */
public class App {

  private long id;
  private String name;
  @JsonProperty("package") private String packageName;
  private long size;
  private String icon;
  private String graphic;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date added;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date modified;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date updated;
  private String uptype;
  private Store store;
  private File file;
  private Stats stats;
  private Obb obb;

  public App() {
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPackageName() {
    return this.packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public long getSize() {
    return this.size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getGraphic() {
    return this.graphic;
  }

  public void setGraphic(String graphic) {
    this.graphic = graphic;
  }

  public Date getAdded() {
    return this.added;
  }

  public void setAdded(Date added) {
    this.added = added;
  }

  public Date getModified() {
    return this.modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Date getUpdated() {
    return this.updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public String getUptype() {
    return this.uptype;
  }

  public void setUptype(String uptype) {
    this.uptype = uptype;
  }

  public Store getStore() {
    return this.store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public File getFile() {
    return this.file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Stats getStats() {
    return this.stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public Obb getObb() {
    return this.obb;
  }

  public void setObb(Obb obb) {
    this.obb = obb;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final long $id = this.getId();
    result = result * PRIME + (int) ($id >>> 32 ^ $id);
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $packageName = this.getPackageName();
    result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
    final long $size = this.getSize();
    result = result * PRIME + (int) ($size >>> 32 ^ $size);
    final Object $icon = this.getIcon();
    result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
    final Object $graphic = this.getGraphic();
    result = result * PRIME + ($graphic == null ? 43 : $graphic.hashCode());
    final Object $added = this.getAdded();
    result = result * PRIME + ($added == null ? 43 : $added.hashCode());
    final Object $modified = this.getModified();
    result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
    final Object $updated = this.getUpdated();
    result = result * PRIME + ($updated == null ? 43 : $updated.hashCode());
    final Object $uptype = this.getUptype();
    result = result * PRIME + ($uptype == null ? 43 : $uptype.hashCode());
    final Object $store = this.getStore();
    result = result * PRIME + ($store == null ? 43 : $store.hashCode());
    final Object $file = this.getFile();
    result = result * PRIME + ($file == null ? 43 : $file.hashCode());
    final Object $stats = this.getStats();
    result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
    final Object $obb = this.getObb();
    result = result * PRIME + ($obb == null ? 43 : $obb.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof App;
  }

  public static class Stats {

    private int apps;         // used on Store items
    private int subscribers;  // used both on App items and Store items
    private int downloads;    // used on listApps, Store items and listAppsVersions
    private int pdownloads;    // used on listApps, Store items and listAppsVersions
    private Rating rating;       // used on App items and listAppsVersions

    public Stats() {
    }

    public int getApps() {
      return this.apps;
    }

    public void setApps(int apps) {
      this.apps = apps;
    }

    public int getSubscribers() {
      return this.subscribers;
    }

    public void setSubscribers(int subscribers) {
      this.subscribers = subscribers;
    }

    public int getDownloads() {
      return this.downloads;
    }

    public void setDownloads(int downloads) {
      this.downloads = downloads;
    }

    public int getPdownloads() {
      return this.pdownloads;
    }

    public void setPdownloads(int pdownloads) {
      this.pdownloads = pdownloads;
    }

    public Rating getRating() {
      return this.rating;
    }

    public void setRating(Rating rating) {
      this.rating = rating;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Stats;
    }

    public static class Rating {

      private float avg;
      private int total;

      public Rating() {
      }

      public float getAvg() {
        return this.avg;
      }

      public void setAvg(float avg) {
        this.avg = avg;
      }

      public int getTotal() {
        return this.total;
      }

      public void setTotal(int total) {
        this.total = total;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Rating;
      }

      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Rating)) return false;
        final Rating other = (Rating) o;
        if (!other.canEqual(this)) return false;
        if (Float.compare(this.getAvg(), other.getAvg()) != 0) return false;
        return this.getTotal() == other.getTotal();
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Float.floatToIntBits(this.getAvg());
        result = result * PRIME + this.getTotal();
        return result;
      }

      public String toString() {
        return "App.Stats.Rating(avg=" + this.getAvg() + ", total=" + this.getTotal() + ")";
      }
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Stats)) return false;
      final Stats other = (Stats) o;
      if (!other.canEqual(this)) return false;
      if (this.getApps() != other.getApps()) return false;
      if (this.getSubscribers() != other.getSubscribers()) return false;
      if (this.getDownloads() != other.getDownloads()) return false;
      if (this.getPdownloads() != other.getPdownloads()) return false;
      final Object this$rating = this.getRating();
      final Object other$rating = other.getRating();
      return this$rating == null ? other$rating == null : this$rating.equals(other$rating);
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      result = result * PRIME + this.getApps();
      result = result * PRIME + this.getSubscribers();
      result = result * PRIME + this.getDownloads();
      result = result * PRIME + this.getPdownloads();
      final Object $rating = this.getRating();
      result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
      return result;
    }

    public String toString() {
      return "App.Stats(apps="
          + this.getApps()
          + ", subscribers="
          + this.getSubscribers()
          + ", downloads="
          + this.getDownloads()
          + ", pdownloads="
          + this.getPdownloads()
          + ", rating="
          + this.getRating()
          + ")";
    }
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof App)) return false;
    final App other = (App) o;
    if (!other.canEqual(this)) return false;
    if (this.getId() != other.getId()) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$packageName = this.getPackageName();
    final Object other$packageName = other.getPackageName();
    if (this$packageName == null ? other$packageName != null
        : !this$packageName.equals(other$packageName)) {
      return false;
    }
    if (this.getSize() != other.getSize()) return false;
    final Object this$icon = this.getIcon();
    final Object other$icon = other.getIcon();
    if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
    final Object this$graphic = this.getGraphic();
    final Object other$graphic = other.getGraphic();
    if (this$graphic == null ? other$graphic != null : !this$graphic.equals(other$graphic)) {
      return false;
    }
    final Object this$added = this.getAdded();
    final Object other$added = other.getAdded();
    if (this$added == null ? other$added != null : !this$added.equals(other$added)) return false;
    final Object this$modified = this.getModified();
    final Object other$modified = other.getModified();
    if (this$modified == null ? other$modified != null : !this$modified.equals(other$modified)) {
      return false;
    }
    final Object this$updated = this.getUpdated();
    final Object other$updated = other.getUpdated();
    if (this$updated == null ? other$updated != null : !this$updated.equals(other$updated)) {
      return false;
    }
    final Object this$uptype = this.getUptype();
    final Object other$uptype = other.getUptype();
    if (this$uptype == null ? other$uptype != null : !this$uptype.equals(other$uptype)) {
      return false;
    }
    final Object this$store = this.getStore();
    final Object other$store = other.getStore();
    if (this$store == null ? other$store != null : !this$store.equals(other$store)) return false;
    final Object this$file = this.getFile();
    final Object other$file = other.getFile();
    if (this$file == null ? other$file != null : !this$file.equals(other$file)) return false;
    final Object this$stats = this.getStats();
    final Object other$stats = other.getStats();
    if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
    final Object this$obb = this.getObb();
    final Object other$obb = other.getObb();
    return this$obb == null ? other$obb == null : this$obb.equals(other$obb);
  }

  public String toString() {
    return "App(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", packageName="
        + this.getPackageName()
        + ", size="
        + this.getSize()
        + ", icon="
        + this.getIcon()
        + ", graphic="
        + this.getGraphic()
        + ", added="
        + this.getAdded()
        + ", modified="
        + this.getModified()
        + ", updated="
        + this.getUpdated()
        + ", uptype="
        + this.getUptype()
        + ", store="
        + this.getStore()
        + ", file="
        + this.getFile()
        + ", stats="
        + this.getStats()
        + ", obb="
        + this.getObb()
        + ")";
  }
}
