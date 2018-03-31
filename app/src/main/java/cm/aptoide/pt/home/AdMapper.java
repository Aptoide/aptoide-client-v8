package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.search.model.SearchAdResult;
import java.util.AbstractMap;
import rx.functions.Func1;

public class AdMapper {
  public AdMapper() {
  }

  Func1<? super AdClick, AbstractMap.SimpleEntry<String, SearchAdResult>> mapAdToSearchAd() {
    return wrappedAd -> {
      if (wrappedAd == null) {
        return new AbstractMap.SimpleEntry<>("", new SearchAdResult());
      }
      String clickUrl = null;
      int networkId = 0;
      GetAdsResponse.Ad ad = wrappedAd.getAd();
      if (ad.getPartner() != null) {
        networkId = ad.getPartner()
            .getInfo()
            .getId();

        clickUrl = ad.getPartner()
            .getData()
            .getClickUrl();
      }
      GetAdsResponse.Data adData = ad.getData();
      GetAdsResponse.Info adInfo = ad.getInfo();
      return new AbstractMap.SimpleEntry<>(wrappedAd.getTag(),
          new SearchAdResult(adInfo.getAdId(), adData.getIcon(), adData.getDownloads(),
              adData.getStars(), adData.getModified()
              .getDate(), adData.getPackageName(), adInfo.getCpcUrl(), adInfo.getCpdUrl(),
              adInfo.getCpiUrl(), clickUrl, adData.getName(), adData.getId(), networkId));
    };
  }
}