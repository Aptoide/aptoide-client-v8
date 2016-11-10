package cm.aptoide.pt.aptoidesdk.ads;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 24-10-2016.
 */
@EqualsAndHashCode @Accessors(chain = true) public class Ad {
  final long timestamp;
  final long id;
  final Clicks clicks;
  final Network network;
  final Data data;
  String referrer;

  @JsonCreator public Ad(@JsonProperty("timestamp") long timestamp, @JsonProperty("id") long id,
      @JsonProperty("clicks") Clicks clicks, @JsonProperty("network") Network network,
      @JsonProperty("data") Data data) {
    this.timestamp = timestamp;
    this.id = id;
    this.clicks = clicks;
    this.network = network;
    this.data = data;
  }

  static Ad from(GetAdsResponse.Ad ad) {
    long adId = ad.getInfo().getAdId();

    Clicks clicks = Clicks.fromGetAds(ad);
    Network network = Network.fromGetAds(ad);
    Data data = Data.fromGetAds(ad);

    return new Ad(System.currentTimeMillis(), adId, clicks, network, data);
  }

  @JsonIgnore public String getName() {
    return data.name;
  }

  @JsonIgnore public String getIconPath() {
    return data.icon;
  }

  @JsonIgnore public long getSize() {
    return data.size;
  }

  @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @lombok.Data @Accessors(chain = true)
  static class Network {
    int id;
    String clickUrl;
    String impressionUrl;

    public static Network fromGetAds(GetAdsResponse.Ad ad) {
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

    @JsonCreator
    public Data(@JsonProperty("appId") long appId, @JsonProperty("packageName") String packageName,
        @JsonProperty("name") String name, @JsonProperty("icon") String icon,
        @JsonProperty("size") long size) {
      this.appId = appId;
      this.packageName = packageName;
      this.name = name;
      this.icon = icon;
      this.size = size;
    }

    public static Data fromGetAds(GetAdsResponse.Ad ad) {
      GetAdsResponse.Data data = ad.getData();
      long id = data.getId();
      String packageName = data.getPackageName();
      String name = data.getName();
      String icon = data.getIcon();
      long size = data.getSize();

      return new Data(id, packageName, name, icon, size);
    }
  }
}