package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.search.model.SearchAdResult;
import rx.functions.Func1;

public class AdMapper {
  public AdMapper() {
  }

  Func1<? super GetAdsResponse.Ad, SearchAdResult> mapAdToSearchAd() {
    return ad -> {
      String clickUrl = null;
      int networkId = 0;
      if (ad.getPartner() != null) {
        networkId = ad.getPartner()
            .getInfo()
            .getId();

        clickUrl = ad.getPartner()
            .getData()
            .getClickUrl();
      }
      return new SearchAdResult(ad.getInfo()
          .getAdId(), ad.getData()
          .getIcon(), ad.getData()
          .getDownloads(), ad.getData()
          .getStars(), ad.getData()
          .getModified()
          .getDate(), ad.getData()
          .getPackageName(), ad.getInfo()
          .getCpcUrl(), ad.getInfo()
          .getCpdUrl(), ad.getInfo()
          .getCpiUrl(), clickUrl, ad.getData()
          .getName(), ad.getData()
          .getId(), networkId);
    };
  }
}