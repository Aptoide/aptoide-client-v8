package cm.aptoide.pt.aptoidesdk.ads;

import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.Aptoide;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 24-10-2016.
 */
@EqualsAndHashCode class AptoideAd implements Ad {
  final long timestamp;
  final long id;
  final Clicks clicks;
  final Network network;
  /**
   * The appId of this ad.
   * <br><br><b>Attention!</b><br>
   * <b>Do not use in conjunction with getApp, use {@link Aptoide#getApp(Ad)} instead.</b>
   */
  @Getter final long appId;
  /**
   * The package name of this ad.
   */
  @Getter final String packageName;
  /**
   * The name of this ad.
   */
  @Getter final String name;
  /**
   * The path ot the icon of this ad.
   */
  @Getter final String iconPath;
  /**
   * The size of this ad.
   */
  @Getter final long size;
  /**
   * The version code of this ad.
   */
  @Getter final int vercode;
  /**
   * The version name of this ad.
   */
  @Getter final String vername;
  /**
   * The description of this ad.
   */
  @Getter final String description;

  String referrer;

  @JsonCreator
  public AptoideAd(@JsonProperty("timestamp") long timestamp, @JsonProperty("id") long id,
      @JsonProperty("clicks") Clicks clicks, @JsonProperty("network") Network network,
      @JsonProperty("appId") long appId, @JsonProperty("packageName") String packageName,
      @JsonProperty("name") String name, @JsonProperty("iconPath") String iconPath,
      @JsonProperty("size") long size, @JsonProperty("vercode") int vercode,
      @JsonProperty("vername") String vername, @JsonProperty("description") String description) {

    this.timestamp = timestamp;
    this.id = id;
    this.clicks = clicks;
    this.network = network;

    this.appId = appId;
    this.packageName = packageName;
    this.name = name;
    this.iconPath = iconPath;
    this.size = size;

    this.vercode = vercode;
    this.vername = vername;
    this.description = description;
  }

  static AptoideAd from(GetAdsResponse.Ad ad) {
    long adId = ad.getInfo().getAdId();

    Clicks clicks = Clicks.fromGetAds(ad);
    Network network = Network.fromGetAds(ad);

    GetAdsResponse.Data data = ad.getData();
    long appId = data.getId();
    String packageName = data.getPackageName();
    String name = data.getName();
    String iconPath = data.getIcon();
    long size = data.getSize();

    int vercode = data.getVercode();
    String vername = data.getVername();

    String description = data.getDescription();

    return new AptoideAd(System.currentTimeMillis(), adId, clicks, network, appId, packageName,
        name, iconPath, size, vercode, vername, description);
  }

  @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @lombok.Data @Accessors(chain = true)
  static class Network {
    int id;
    String clickUrl;
    String impressionUrl;

    public static Network fromGetAds(GetAdsResponse.Ad ad) {

      if (ad.getPartner() == null) {
        return null;
      }

      int adId = ad.getPartner().getInfo().getId();
      String clickUrl = ad.getPartner().getData().getClickUrl();
      String impressionUrl = ad.getPartner().getData().getImpressionUrl();

      return new Network(adId, clickUrl, impressionUrl);
    }
  }

  @EqualsAndHashCode static class Clicks {
    final String cpcUrl;
    final String cpiUrl;
    final String cpdUrl;

    @JsonCreator
    public Clicks(@JsonProperty("cpcUrl") String cpcUrl, @JsonProperty("cpiUrl") String cpiUrl,
        @JsonProperty("cpdUrl") String cpdUrl) {
      this.cpcUrl = cpcUrl;
      this.cpiUrl = cpiUrl;
      this.cpdUrl = cpdUrl;
    }

    public static Clicks fromGetAds(GetAdsResponse.Ad ad) {
      String cpcUrl = ad.getInfo().getCpcUrl();
      String cpiUrl = ad.getInfo().getCpiUrl();
      String cpdUrl = ad.getInfo().getCpdUrl();

      return new Clicks(cpcUrl, cpiUrl, cpdUrl);
    }
  }

  @EqualsAndHashCode static class Data {
    final long appId;
    final String packageName;
    final String name;
    final String icon;
    final long size;
    final String description;

    @JsonCreator
    public Data(@JsonProperty("appId") long appId, @JsonProperty("packageName") String packageName,
        @JsonProperty("name") String name, @JsonProperty("icon") String icon,
        @JsonProperty("size") long size, @JsonProperty("description") String description) {
      this.appId = appId;
      this.packageName = packageName;
      this.name = name;
      this.icon = icon;
      this.size = size;
      this.description = description;
    }

    public static Data fromGetAds(GetAdsResponse.Ad ad) {
      GetAdsResponse.Data data = ad.getData();
      long id = data.getId();
      String packageName = data.getPackageName();
      String name = data.getName();
      String icon = data.getIcon();
      long size = data.getSize();
      String description = data.getDescription();

      return new Data(id, packageName, name, icon, size, description);
    }
  }
}