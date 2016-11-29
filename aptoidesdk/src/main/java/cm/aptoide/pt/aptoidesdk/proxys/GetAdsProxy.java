package cm.aptoide.pt.aptoidesdk.proxys;

import cm.aptoide.pt.aptoidesdk.ads.RxAptoide;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 02-11-2016.
 */

public class GetAdsProxy {

  private final boolean DEFAULT_MATURE = false;
  private GetAdsRequest.Location ADS_LOCATION = GetAdsRequest.Location.homepage;
  private String DEFAULT_KEYWORD = "__NULL__";

  public Observable<GetAdsResponse> getAds(int limit, boolean mature, String aptoideClientUUID) {
    return GetAdsRequest.of(ADS_LOCATION, DEFAULT_KEYWORD, limit, aptoideClientUUID,
        isGooglePlayServicesAvailable(), RxAptoide.getOemid(), mature).observe();
  }

  public Observable<GetAdsResponse> getAds(int limit, String aptoideClientUUID,
      List<String> keywords) {
    return GetAdsRequest.of(ADS_LOCATION,
        AptoideUtils.StringU.join(keywords, ",") + "," + "__null__", limit, aptoideClientUUID,
        isGooglePlayServicesAvailable(), RxAptoide.getOemid(), DEFAULT_MATURE).observe();
  }

  public Observable<GetAdsResponse> getAds(int limit, boolean mature, String aptoideClientUUID,
      List<String> keywords) {
    return GetAdsRequest.of(ADS_LOCATION,
        AptoideUtils.StringU.join(keywords, ",") + "," + "__null__", limit, aptoideClientUUID,
        isGooglePlayServicesAvailable(), RxAptoide.getOemid(), mature).observe();
  }

  public Observable<GetAdsResponse> getAds(int limit, String aptoideClientUUID) {
    return GetAdsRequest.of(ADS_LOCATION, DEFAULT_KEYWORD, limit, aptoideClientUUID,
        isGooglePlayServicesAvailable(), RxAptoide.getOemid(), DEFAULT_MATURE).observe();
  }

  public Observable<GetAdsResponse> getAds(String packageName, String aptoideClientUUID) {
    return GetAdsRequest.ofAppviewOrganic(packageName, null, aptoideClientUUID,
        isGooglePlayServicesAvailable(), RxAptoide.getOemid(), DEFAULT_MATURE)
        .setLocation(ADS_LOCATION)
        .observe();
  }

  private boolean isGooglePlayServicesAvailable() {
    return DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(RxAptoide.getContext());
  }
}
