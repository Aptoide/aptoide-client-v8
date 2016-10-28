package cm.aptoide.pt.aptoidesdk.ads;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 24-10-2016.
 */
@Accessors(chain = true) public class Ad {
  @Getter final long timestamp;
  final long id;
  final Clicks clicks;
  final Network network;
  final Data data;
  @Getter @Setter String referrer;

  public Ad(long timestamp, long id, Clicks clicks, Network network, Data data) {
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

  public String getName() {
    return data.name;
  }

  public String getIconPath() {
    return data.icon;
  }

  public long getSize() {
    return data.size;
  }

  @AllArgsConstructor static class Network {
    final int id;
    final String clickUrl;
    final String impressionUrl;

    public static Network fromGetAds(GetAdsResponse.Ad ad) {
      int adId = ad.getPartner().getInfo().getId();
      String clickUrl = ad.getPartner().getData().getClickUrl();
      String impressionUrl = ad.getPartner().getData().getImpressionUrl();

      return new Network(adId, clickUrl, impressionUrl);
    }
  }

  @AllArgsConstructor static class Clicks {
    final String cpcUrl;
    final String cpiUrl;
    final String cpdUrl;

    public static Clicks fromGetAds(GetAdsResponse.Ad ad) {
      String cpcUrl = ad.getInfo().getCpcUrl();
      String cpiUrl = ad.getInfo().getCpiUrl();
      String cpdUrl = ad.getInfo().getCpdUrl();

      return new Clicks(cpcUrl, cpiUrl, cpdUrl);
    }
  }

  @AllArgsConstructor static class Data {
    final long appId;
    final String packageName;
    final String name;
    final String icon;
    final long size;

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