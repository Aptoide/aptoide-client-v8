package cm.aptoide.pt.aptoidesdk.proxys;

import cm.aptoide.pt.aptoidesdk.ads.Aptoide;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import java.util.List;

/**
 * Created by neuro on 02-11-2016.
 */

public class GetAdsProxy {

  private GetAdsRequest.Location ADS_LOCATION = GetAdsRequest.Location.aptoidesdk;
  private String DEFAULT_KEYWORD = "__NULL__";

  public void getAds(int limit, boolean mature, String aptoideClientUUID) {
    GetAdsRequest.of(ADS_LOCATION, DEFAULT_KEYWORD, limit, aptoideClientUUID,
        DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(Aptoide.getContext()))
        .observe()
        .first();
  }

  public void getAds(int limit, String aptoideClientUUID, List<String> keyword) {
    // TODO: 02-11-2016 neuro
  }

  public void getAds(int limit, boolean mature, String aptoideClientUUID, List<String> keyword) {
    // TODO: 02-11-2016 neuro
  }

  public void getAds(int limit, String aptoideClientUUID) {
    // TODO: 02-11-2016 neuro
  }
}
