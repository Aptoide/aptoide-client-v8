package cm.aptoide.pt.ads;

import java.util.List;

public class CampaignsServiceResponse {
  private boolean hasError;
  private String next;
  private List<Campaign> campaigns;

  public CampaignsServiceResponse(String next, List<Campaign> campaigns) {
    this.next = next;
    this.campaigns = campaigns;
    this.hasError = false;
  }

  public CampaignsServiceResponse() {
    this.hasError = true;
    this.next = null;
    this.campaigns = null;
  }

  public boolean hasError() {
    return hasError;
  }

  public List<Campaign> getCampaigns() {
    return campaigns;
  }

  public String getNext() {
    return next;
  }

  public static class Campaign {
    private String uid;
    private String label;
    private String icon;
    private int downloads;
    private String packageName;
    private float averageRating;
    private String appc;
    private String clickUrl;
    private String downloadUrl;
    private String versionName;

    public Campaign(String uid, String label, String icon, int downloads, String packageName,
        float averageRating, String appc, String clickUrl, String downloadUrl, String versionName) {
      this.uid = uid;
      this.label = label;
      this.icon = icon;
      this.downloads = downloads;
      this.packageName = packageName;
      this.averageRating = averageRating;
      this.appc = appc;
      this.clickUrl = clickUrl;
      this.downloadUrl = downloadUrl;
      this.versionName = versionName;
    }

    public String getUid() {
      return uid;
    }

    public String getLabel() {
      return label;
    }

    public String getIcon() {
      return icon;
    }

    public String getPackageName() {
      return packageName;
    }

    public float getAverageRating() {
      return averageRating;
    }

    public String getAppc() {
      return appc;
    }

    public String getClickUrl() {
      return clickUrl;
    }

    public String getDownloadUrl() {
      return downloadUrl;
    }

    public String getVersionName() {
      return versionName;
    }

    public int getDownloads() {
      return downloads;
    }
  }
}
