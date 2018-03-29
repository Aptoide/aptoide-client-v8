package cm.aptoide.pt.home;

import cm.aptoide.pt.search.model.SearchAdResult;
import java.util.AbstractMap;
import rx.functions.Func1;

public class AdMapper {
  public AdMapper() {
  }

  Func1<? super AdClick, AbstractMap.SimpleEntry<String, SearchAdResult>> mapAdToSearchAd() {
    return ad -> {
      if (ad == null) {
        return new AbstractMap.SimpleEntry<>("", new SearchAdResult());
      }
      String clickUrl = null;
      int networkId = 0;
      if (ad.getAd()
          .getPartner() != null) {
        networkId = ad.getAd()
            .getPartner()
            .getInfo()
            .getId();

        clickUrl = ad.getAd()
            .getPartner()
            .getData()
            .getClickUrl();
      }
      return new AbstractMap.SimpleEntry<>(ad.getTag(), new SearchAdResult(ad.getAd()
          .getInfo()
          .getAdId(), ad.getAd()
          .getData()
          .getIcon(), ad.getAd()
          .getData()
          .getDownloads(), ad.getAd()
          .getData()
          .getStars(), ad.getAd()
          .getData()
          .getModified()
          .getDate(), ad.getAd()
          .getData()
          .getPackageName(), ad.getAd()
          .getInfo()
          .getCpcUrl(), ad.getAd()
          .getInfo()
          .getCpdUrl(), ad.getAd()
          .getInfo()
          .getCpiUrl(), clickUrl, ad.getAd()
          .getData()
          .getName(), ad.getAd()
          .getData()
          .getId(), networkId));
    };
  }
}