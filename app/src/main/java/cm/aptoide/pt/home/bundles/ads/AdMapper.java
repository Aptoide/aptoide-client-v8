package cm.aptoide.pt.home.bundles.ads;

import android.util.Pair;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.search.model.SearchAdResult;

public class AdMapper {
  public AdMapper() {
  }

  public Pair<String, SearchAdResult> mapAdToSearchAd(AdClick wrappedAd) {
    if (wrappedAd == null) {
      return new Pair<>("", new SearchAdResult());
    }
    AptoideNativeAd ad = (AptoideNativeAd) wrappedAd.getAd();
    return new Pair<>(wrappedAd.getTag(),
        new SearchAdResult(ad.getAdId(), ad.getIconUrl(), ad.getDownloads(), ad.getStars(),
            ad.getModified(), ad.getPackageName(), ad.getCpcUrl(), ad.getCpdUrl(), ad.getCpiUrl(),
            ad.getClickUrl(), ad.getAdTitle(), ad.getAppId(), ad.getNetworkId(), false, null));
  }
}