package cm.aptoide.pt.ads;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CampaignsServiceResponseBody {
  private String next;
  private List<CampaignApp> items;

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public List<CampaignApp> getItems() {
    return items;
  }

  public void setItems(List<CampaignApp> items) {
    this.items = items;
  }

  public static class CampaignApp {
    private String uid;
    private String label;
    private String icon;
    private int downloads;
    @JsonProperty("package") private CampaignPackage packageInfo;
    private Rating rating;
    private CampaignInfo campaign;
    private ApkInfo apk;

    public String getUid() {
      return uid;
    }

    public void setUid(String uid) {
      this.uid = uid;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public String getIcon() {
      return icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public Rating getRating() {
      return rating;
    }

    public void setRating(Rating rating) {
      this.rating = rating;
    }

    public CampaignInfo getCampaign() {
      return campaign;
    }

    public void setCampaign(CampaignInfo campaign) {
      this.campaign = campaign;
    }

    public ApkInfo getApk() {
      return apk;
    }

    public void setApk(ApkInfo apk) {
      this.apk = apk;
    }

    public CampaignPackage getPackageInfo() {
      return packageInfo;
    }

    public void setPackageInfo(CampaignPackage packageInfo) {
      this.packageInfo = packageInfo;
    }

    public int getDownloads() {
      return downloads;
    }

    public void setDownloads(int downloads) {
      this.downloads = downloads;
    }

    public static class CampaignPackage {
      private String name;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }
    }

    public static class Rating {
      private float average;
      private long total;

      public float getAverage() {
        return average;
      }

      public void setAverage(float average) {
        this.average = average;
      }

      public long getTotal() {
        return total;
      }

      public void setTotal(long total) {
        this.total = total;
      }
    }

    public static class CampaignInfo {
      private String appc;
      private CampaignUrls urls;

      public String getAppc() {
        return appc;
      }

      public void setAppc(String appc) {
        this.appc = appc;
      }

      public CampaignUrls getUrls() {
        return urls;
      }

      public void setUrls(CampaignUrls urls) {
        this.urls = urls;
      }

      public static class CampaignUrls {
        private String click;
        private String download;

        public String getClick() {
          return click;
        }

        public void setClick(String click) {
          this.click = click;
        }

        public String getDownload() {
          return download;
        }

        public void setDownload(String download) {
          this.download = download;
        }
      }
    }

    public static class ApkInfo {
      private ApkVersion version;

      public ApkVersion getVersion() {
        return version;
      }

      public void setVersion(ApkVersion version) {
        this.version = version;
      }

      public static class ApkVersion {
        private String name;

        public String getName() {
          return name;
        }

        public void setName(String name) {
          this.name = name;
        }
      }
    }
  }
}
