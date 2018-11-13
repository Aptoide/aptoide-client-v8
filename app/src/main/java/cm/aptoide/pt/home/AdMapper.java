package cm.aptoide.pt.home;

import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.search.model.SearchAdResult;
import java.util.AbstractMap;
import rx.functions.Func1;

public class AdMapper {
  public AdMapper() {
  }

  public Func1<? super AdClick, AbstractMap.SimpleEntry<String, SearchAdResult>> mapAdToSearchAd() {
    return wrappedAd -> {
      if (wrappedAd == null) {
        return new AbstractMap.SimpleEntry<>("", new SearchAdResult());
      }
      AptoideNativeAd ad = (AptoideNativeAd) wrappedAd.getAd();
      return new AbstractMap.SimpleEntry<>(wrappedAd.getTag(),
          new SearchAdResult(ad.getAdId(), ad.getIconUrl(), ad.getDownloads(), ad.getStars(),
              ad.getModified(), ad.getPackageName(), ad.getCpcUrl(), ad.getCpdUrl(), ad.getCpiUrl(),
              ad.getClickUrl(), ad.getAdTitle(), ad.getAppId(), ad.getNetworkId()));
    };
  }
}