package cm.aptoide.pt.ads;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;

public class MoPubAnalytics {

  public void setMoPubAbTestGroup(boolean isControlGroup) {
    Bundle bundle = new Bundle();
    bundle.putString("ASV-1377-MoPub-Ads", isControlGroup ? "a_without_mopub" : "b_with_mopub");
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
  }

  public void setAdsVisibilityUserProperty(
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    Bundle bundle = new Bundle();
    bundle.putString("ads", mapToAdsVisibility(offerResponseStatus).getType());
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
  }

  private AdsVisibility mapToAdsVisibility(WalletAdsOfferManager.OfferResponseStatus status) {
    switch (status) {
      case NO_ADS:
        return AdsVisibility.CONTROL;
      case ADS_HIDE:
        return AdsVisibility.ADS_BLOCKED_BY_OFFER;
      case ADS_SHOW:
        return AdsVisibility.HAS_ADS;
      default:
        throw new IllegalStateException("Invalid OfferResponseStatus");
    }
  }

  private enum AdsVisibility {
    ADS_BLOCKED_BY_OFFER("ads blocked by offer"), CONTROL("control"), HAS_ADS("has ads");

    private final String type;

    AdsVisibility(String type) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }
}
