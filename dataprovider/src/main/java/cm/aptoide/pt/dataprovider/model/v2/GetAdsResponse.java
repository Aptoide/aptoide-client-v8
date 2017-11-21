/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.model.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * GetAdsResponse.
 */
public class GetAdsResponse {

  private List<Ad> ads;
  private Options options;

  public GetAdsResponse() {
  }

  public List<Ad> getAds() {
    return this.ads;
  }

  public void setAds(List<Ad> ads) {
    this.ads = ads;
  }

  public Options getOptions() {
    return this.options;
  }

  public void setOptions(Options options) {
    this.options = options;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $ads = this.getAds();
    result = result * PRIME + ($ads == null ? 43 : $ads.hashCode());
    final Object $options = this.getOptions();
    result = result * PRIME + ($options == null ? 43 : $options.hashCode());
    return result;
  }  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GetAdsResponse)) return false;
    final GetAdsResponse other = (GetAdsResponse) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$ads = this.getAds();
    final Object other$ads = other.getAds();
    if (this$ads == null ? other$ads != null : !this$ads.equals(other$ads)) return false;
    final Object this$options = this.getOptions();
    final Object other$options = other.getOptions();
    if (this$options == null ? other$options != null : !this$options.equals(other$options)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof GetAdsResponse;
  }

  public static class Data {

    private long id;
    private String name;
    private String repo;
    @JsonProperty("package") private String packageName;
    private String md5sum;
    private long size;
    private int vercode;
    private String vername;
    private String icon;
    private int downloads;
    private int stars;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date added;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date modified;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date updated;

    public Data() {
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

    public String getRepo() {
      return this.repo;
    }

    public void setRepo(String repo) {
      this.repo = repo;
    }

    public String getPackageName() {
      return this.packageName;
    }

    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }

    public String getMd5sum() {
      return this.md5sum;
    }

    public void setMd5sum(String md5sum) {
      this.md5sum = md5sum;
    }

    public long getSize() {
      return this.size;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public int getVercode() {
      return this.vercode;
    }

    public void setVercode(int vercode) {
      this.vercode = vercode;
    }

    public String getVername() {
      return this.vername;
    }

    public void setVername(String vername) {
      this.vername = vername;
    }

    public String getIcon() {
      return this.icon;
    }

    public void setIcon(String icon) {
      this.icon = icon;
    }

    public int getDownloads() {
      return this.downloads;
    }

    public void setDownloads(int downloads) {
      this.downloads = downloads;
    }

    public int getStars() {
      return this.stars;
    }

    public void setStars(int stars) {
      this.stars = stars;
    }

    public String getDescription() {
      return this.description;
    }

    public void setDescription(String description) {
      this.description = description;
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

    protected boolean canEqual(Object other) {
      return other instanceof Data;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Data)) return false;
      final Data other = (Data) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getId() != other.getId()) return false;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$repo = this.getRepo();
      final Object other$repo = other.getRepo();
      if (this$repo == null ? other$repo != null : !this$repo.equals(other$repo)) return false;
      final Object this$packageName = this.getPackageName();
      final Object other$packageName = other.getPackageName();
      if (this$packageName == null ? other$packageName != null
          : !this$packageName.equals(other$packageName)) {
        return false;
      }
      final Object this$md5sum = this.getMd5sum();
      final Object other$md5sum = other.getMd5sum();
      if (this$md5sum == null ? other$md5sum != null : !this$md5sum.equals(other$md5sum)) {
        return false;
      }
      if (this.getSize() != other.getSize()) return false;
      if (this.getVercode() != other.getVercode()) return false;
      final Object this$vername = this.getVername();
      final Object other$vername = other.getVername();
      if (this$vername == null ? other$vername != null : !this$vername.equals(other$vername)) {
        return false;
      }
      final Object this$icon = this.getIcon();
      final Object other$icon = other.getIcon();
      if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) return false;
      if (this.getDownloads() != other.getDownloads()) return false;
      if (this.getStars() != other.getStars()) return false;
      final Object this$description = this.getDescription();
      final Object other$description = other.getDescription();
      if (this$description == null ? other$description != null
          : !this$description.equals(other$description)) {
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
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $id = this.getId();
      result = result * PRIME + (int) ($id >>> 32 ^ $id);
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $repo = this.getRepo();
      result = result * PRIME + ($repo == null ? 43 : $repo.hashCode());
      final Object $packageName = this.getPackageName();
      result = result * PRIME + ($packageName == null ? 43 : $packageName.hashCode());
      final Object $md5sum = this.getMd5sum();
      result = result * PRIME + ($md5sum == null ? 43 : $md5sum.hashCode());
      final long $size = this.getSize();
      result = result * PRIME + (int) ($size >>> 32 ^ $size);
      result = result * PRIME + this.getVercode();
      final Object $vername = this.getVername();
      result = result * PRIME + ($vername == null ? 43 : $vername.hashCode());
      final Object $icon = this.getIcon();
      result = result * PRIME + ($icon == null ? 43 : $icon.hashCode());
      result = result * PRIME + this.getDownloads();
      result = result * PRIME + this.getStars();
      final Object $description = this.getDescription();
      result = result * PRIME + ($description == null ? 43 : $description.hashCode());
      final Object $added = this.getAdded();
      result = result * PRIME + ($added == null ? 43 : $added.hashCode());
      final Object $modified = this.getModified();
      result = result * PRIME + ($modified == null ? 43 : $modified.hashCode());
      final Object $updated = this.getUpdated();
      result = result * PRIME + ($updated == null ? 43 : $updated.hashCode());
      return result;
    }



    public String toString() {
      return "GetAdsResponse.Data(id="
          + this.getId()
          + ", name="
          + this.getName()
          + ", repo="
          + this.getRepo()
          + ", packageName="
          + this.getPackageName()
          + ", md5sum="
          + this.getMd5sum()
          + ", size="
          + this.getSize()
          + ", vercode="
          + this.getVercode()
          + ", vername="
          + this.getVername()
          + ", icon="
          + this.getIcon()
          + ", downloads="
          + this.getDownloads()
          + ", stars="
          + this.getStars()
          + ", description="
          + this.getDescription()
          + ", added="
          + this.getAdded()
          + ", modified="
          + this.getModified()
          + ", updated="
          + this.getUpdated()
          + ")";
    }
  }

  public static class Ad {

    private Data data;
    private Info info;
    private Partner partner;
    private Partner tracker;

    public Ad() {
    }

    public Data getData() {
      return this.data;
    }

    public void setData(Data data) {
      this.data = data;
    }

    public Info getInfo() {
      return this.info;
    }

    public void setInfo(Info info) {
      this.info = info;
    }

    public Partner getPartner() {
      return this.partner;
    }

    public void setPartner(Partner partner) {
      this.partner = partner;
    }

    public Partner getTracker() {
      return this.tracker;
    }

    public void setTracker(Partner tracker) {
      this.tracker = tracker;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Ad;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Ad)) return false;
      final Ad other = (Ad) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$data = this.getData();
      final Object other$data = other.getData();
      if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
      final Object this$info = this.getInfo();
      final Object other$info = other.getInfo();
      if (this$info == null ? other$info != null : !this$info.equals(other$info)) return false;
      final Object this$partner = this.getPartner();
      final Object other$partner = other.getPartner();
      if (this$partner == null ? other$partner != null : !this$partner.equals(other$partner)) {
        return false;
      }
      final Object this$tracker = this.getTracker();
      final Object other$tracker = other.getTracker();
      if (this$tracker == null ? other$tracker != null : !this$tracker.equals(other$tracker)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $data = this.getData();
      result = result * PRIME + ($data == null ? 43 : $data.hashCode());
      final Object $info = this.getInfo();
      result = result * PRIME + ($info == null ? 43 : $info.hashCode());
      final Object $partner = this.getPartner();
      result = result * PRIME + ($partner == null ? 43 : $partner.hashCode());
      final Object $tracker = this.getTracker();
      result = result * PRIME + ($tracker == null ? 43 : $tracker.hashCode());
      return result;
    }



    public String toString() {
      return "GetAdsResponse.Ad(data="
          + this.getData()
          + ", info="
          + this.getInfo()
          + ", partner="
          + this.getPartner()
          + ", tracker="
          + this.getTracker()
          + ")";
    }
  }  public String toString() {
    return "GetAdsResponse(ads=" + this.getAds() + ", options=" + this.getOptions() + ")";
  }

  public static class Info {

    private long adId;
    private String adType;
    private String cpcUrl;
    private String cpiUrl;
    private String cpdUrl;

    public Info() {
    }

    public long getAdId() {
      return this.adId;
    }

    public void setAdId(long adId) {
      this.adId = adId;
    }

    public String getAdType() {
      return this.adType;
    }

    public void setAdType(String adType) {
      this.adType = adType;
    }

    public String getCpcUrl() {
      return this.cpcUrl;
    }

    public void setCpcUrl(String cpcUrl) {
      this.cpcUrl = cpcUrl;
    }

    public String getCpiUrl() {
      return this.cpiUrl;
    }

    public void setCpiUrl(String cpiUrl) {
      this.cpiUrl = cpiUrl;
    }

    public String getCpdUrl() {
      return this.cpdUrl;
    }

    public void setCpdUrl(String cpdUrl) {
      this.cpdUrl = cpdUrl;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Info;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Info)) return false;
      final Info other = (Info) o;
      if (!other.canEqual((Object) this)) return false;
      if (this.getAdId() != other.getAdId()) return false;
      final Object this$adType = this.getAdType();
      final Object other$adType = other.getAdType();
      if (this$adType == null ? other$adType != null : !this$adType.equals(other$adType)) {
        return false;
      }
      final Object this$cpcUrl = this.getCpcUrl();
      final Object other$cpcUrl = other.getCpcUrl();
      if (this$cpcUrl == null ? other$cpcUrl != null : !this$cpcUrl.equals(other$cpcUrl)) {
        return false;
      }
      final Object this$cpiUrl = this.getCpiUrl();
      final Object other$cpiUrl = other.getCpiUrl();
      if (this$cpiUrl == null ? other$cpiUrl != null : !this$cpiUrl.equals(other$cpiUrl)) {
        return false;
      }
      final Object this$cpdUrl = this.getCpdUrl();
      final Object other$cpdUrl = other.getCpdUrl();
      if (this$cpdUrl == null ? other$cpdUrl != null : !this$cpdUrl.equals(other$cpdUrl)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final long $adId = this.getAdId();
      result = result * PRIME + (int) ($adId >>> 32 ^ $adId);
      final Object $adType = this.getAdType();
      result = result * PRIME + ($adType == null ? 43 : $adType.hashCode());
      final Object $cpcUrl = this.getCpcUrl();
      result = result * PRIME + ($cpcUrl == null ? 43 : $cpcUrl.hashCode());
      final Object $cpiUrl = this.getCpiUrl();
      result = result * PRIME + ($cpiUrl == null ? 43 : $cpiUrl.hashCode());
      final Object $cpdUrl = this.getCpdUrl();
      result = result * PRIME + ($cpdUrl == null ? 43 : $cpdUrl.hashCode());
      return result;
    }



    public String toString() {
      return "GetAdsResponse.Info(adId="
          + this.getAdId()
          + ", adType="
          + this.getAdType()
          + ", cpcUrl="
          + this.getCpcUrl()
          + ", cpiUrl="
          + this.getCpiUrl()
          + ", cpdUrl="
          + this.getCpdUrl()
          + ")";
    }
  }

  public static class Partner {

    private Info info;
    private Data data;

    public Partner() {
    }

    public Info getInfo() {
      return this.info;
    }

    public void setInfo(Info info) {
      this.info = info;
    }

    public Data getData() {
      return this.data;
    }

    public void setData(Data data) {
      this.data = data;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Partner;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Partner)) return false;
      final Partner other = (Partner) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$info = this.getInfo();
      final Object other$info = other.getInfo();
      if (this$info == null ? other$info != null : !this$info.equals(other$info)) return false;
      final Object this$data = this.getData();
      final Object other$data = other.getData();
      if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
      return true;
    }

    public static class Info {

      private int id;
      private String name;

      public Info() {
      }

      public int getId() {
        return this.id;
      }

      public void setId(int id) {
        this.id = id;
      }

      public String getName() {
        return this.name;
      }

      public void setName(String name) {
        this.name = name;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Info;
      }      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Info)) return false;
        final Info other = (Info) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getId();
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
      }



      public String toString() {
        return "GetAdsResponse.Partner.Info(id=" + this.getId() + ", name=" + this.getName() + ")";
      }
    }    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $info = this.getInfo();
      result = result * PRIME + ($info == null ? 43 : $info.hashCode());
      final Object $data = this.getData();
      result = result * PRIME + ($data == null ? 43 : $data.hashCode());
      return result;
    }

    public static class Data {

      private String clickUrl;
      private String impressionUrl;

      public Data() {
      }

      public String getClickUrl() {
        return this.clickUrl;
      }

      public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
      }

      public String getImpressionUrl() {
        return this.impressionUrl;
      }

      public void setImpressionUrl(String impressionUrl) {
        this.impressionUrl = impressionUrl;
      }

      protected boolean canEqual(Object other) {
        return other instanceof Data;
      }      public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Data)) return false;
        final Data other = (Data) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$clickUrl = this.getClickUrl();
        final Object other$clickUrl = other.getClickUrl();
        if (this$clickUrl == null ? other$clickUrl != null
            : !this$clickUrl.equals(other$clickUrl)) {
          return false;
        }
        final Object this$impressionUrl = this.getImpressionUrl();
        final Object other$impressionUrl = other.getImpressionUrl();
        if (this$impressionUrl == null ? other$impressionUrl != null
            : !this$impressionUrl.equals(other$impressionUrl)) {
          return false;
        }
        return true;
      }

      public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $clickUrl = this.getClickUrl();
        result = result * PRIME + ($clickUrl == null ? 43 : $clickUrl.hashCode());
        final Object $impressionUrl = this.getImpressionUrl();
        result = result * PRIME + ($impressionUrl == null ? 43 : $impressionUrl.hashCode());
        return result;
      }



      public String toString() {
        return "GetAdsResponse.Partner.Data(clickUrl="
            + this.getClickUrl()
            + ", impressionUrl="
            + this.getImpressionUrl()
            + ")";
      }
    }

    public String toString() {
      return "GetAdsResponse.Partner(info=" + this.getInfo() + ", data=" + this.getData() + ")";
    }




  }

  public static class Options {

    private Boolean mediation = true;

    public Options() {
    }

    public Boolean getMediation() {
      return this.mediation;
    }

    public void setMediation(Boolean mediation) {
      this.mediation = mediation;
    }

    protected boolean canEqual(Object other) {
      return other instanceof Options;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof Options)) return false;
      final Options other = (Options) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$mediation = this.getMediation();
      final Object other$mediation = other.getMediation();
      if (this$mediation == null ? other$mediation != null
          : !this$mediation.equals(other$mediation)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $mediation = this.getMediation();
      result = result * PRIME + ($mediation == null ? 43 : $mediation.hashCode());
      return result;
    }



    public String toString() {
      return "GetAdsResponse.Options(mediation=" + this.getMediation() + ")";
    }
  }




}
