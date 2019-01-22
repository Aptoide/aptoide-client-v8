package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.File;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class AppCoinsCampaign {
  private String id;
  private String reward;
  private CampaignApp app;

  public AppCoinsCampaign() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getReward() {
    return reward;
  }

  public void setReward(String reward) {
    this.reward = reward;
  }

  public CampaignApp getApp() {
    return app;
  }

  public void setApp(CampaignApp app) {
    this.app = app;
  }

  public static class CampaignApp {
    private long id;
    private String name;
    @JsonProperty("package") private String packageName;
    private String uname;
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
    private boolean hasVersions;
    private Obb obb;
    private AppCoinsCampaignInfo appcoins;

    public CampaignApp() {
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackageName() {
      return packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getUname() {
      return uname;
    }

    public void setUname(String uname) {
      this.uname = uname;
    }

    public long getSize() {
      return size;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public String getGraphic() {
      return graphic;
    }

    public void setGraphic(String graphic) {
      this.graphic = graphic;
    }

    public Date getAdded() {
      return added;
    }

    public void setAdded(Date added) {
      this.added = added;
    }

    public Date getModified() {
      return modified;
    }

    public void setModified(Date modified) {
      this.modified = modified;
    }

    public Date getUpdated() {
      return updated;
    }

    public void setUpdated(Date updated) {
      this.updated = updated;
    }

    public String getUptype() {
      return uptype;
    }

    public void setUptype(String uptype) {
      this.uptype = uptype;
    }

    public Store getStore() {
      return store;
    }

    public void setStore(Store store) {
      this.store = store;
    }

    public File getFile() {
      return file;
    }

    public void setFile(File file) {
      this.file = file;
    }

    public boolean isHasVersions() {
      return hasVersions;
    }

    public void setHasVersions(boolean hasVersions) {
      this.hasVersions = hasVersions;
    }

    public Obb getObb() {
      return obb;
    }

    public void setObb(Obb obb) {
      this.obb = obb;
    }

    public Stats getStats() {
      return stats;
    }

    public void setStats(Stats stats) {
      this.stats = stats;
    }

    public AppCoinsCampaignInfo getAppcoins() {
      return appcoins;
    }

    public void setAppcoins(AppCoinsCampaignInfo appcoins) {
      this.appcoins = appcoins;
    }

    public static class Stats {
      private int downloads;
      private int pdownloads;
      private Rating rating;
      private Rating prating;

      public Stats() {
      }

      public int getDownloads() {
        return downloads;
      }

      public void setDownloads(int downloads) {
        this.downloads = downloads;
      }

      public int getPdownloads() {
        return pdownloads;
      }

      public void setPdownloads(int pdownloads) {
        this.pdownloads = pdownloads;
      }

      public Rating getRating() {
        return rating;
      }

      public void setRating(Rating rating) {
        this.rating = rating;
      }

      public Rating getPrating() {
        return prating;
      }

      public void setPrating(Rating prating) {
        this.prating = prating;
      }

      public static class Rating {
        private float avg;
        private int total;

        public Rating() {
        }

        public float getAvg() {
          return avg;
        }

        public void setAvg(float avg) {
          this.avg = avg;
        }

        public int getTotal() {
          return total;
        }

        public void setTotal(int total) {
          this.total = total;
        }
      }
    }

    public static class AppCoinsCampaignInfo {
      private boolean advertising;
      private ClickUrls clicks;

      public AppCoinsCampaignInfo() {
      }

      public boolean hasAdvertising() {
        return advertising;
      }

      public void setAdvertising(boolean advertising) {
        this.advertising = advertising;
      }

      public ClickUrls getClicks() {
        return clicks;
      }

      public void setClicks(ClickUrls clicks) {
        this.clicks = clicks;
      }

      public static class ClickUrls {
        private String download;
        private String click;

        public String getDownload() {
          return download;
        }

        public void setDownload(String download) {
          this.download = download;
        }

        public String getClick() {
          return click;
        }

        public void setClick(String click) {
          this.click = click;
        }
      }
    }
  }
}
