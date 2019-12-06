package cm.aptoide.pt.ads;

import android.os.Bundle;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.logger.Logger;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.uxcam.UXCam;
import io.rakam.api.Rakam;
import org.json.JSONException;
import org.json.JSONObject;

public class MoPubAnalytics {

  private static final String ADS_STATUS_USER_PROPERTY = "ads";

  public void setMoPubAbTestGroup(boolean isControlGroup) {
    Bundle bundle = new Bundle();
    bundle.putString("ASV-1377-MoPub-Ads", isControlGroup ? "a_without_mopub" : "b_with_mopub");
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty("ASV-1377-MoPub-Ads",
        isControlGroup ? "a_without_mopub" : "b_with_mopub");
  }

  public void setAdsVisibilityUserProperty(
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    Bundle bundle = new Bundle();
    String ads = mapToAdsVisibility(offerResponseStatus).getType();
    bundle.putString(ADS_STATUS_USER_PROPERTY, ads);
    AppEventsLogger.updateUserProperties(bundle, response -> Logger.getInstance()
        .d("Facebook Analytics: ", response.toString()));
    FlurryAgent.addSessionProperty(ADS_STATUS_USER_PROPERTY, ads);

    if (BuildConfig.FLAVOR_mode.equals("dev")) {
      String rakamAds = mapAdsVisibilityToRakamValues(offerResponseStatus);
      Rakam.getInstance()
          .setSuperProperties(createRakamSuperProperties(rakamAds));
      if (!BuildConfig.DEBUG) {
        UXCam.setUserProperty(ADS_STATUS_USER_PROPERTY, rakamAds);
      }
    }
  }

  private String mapAdsVisibilityToRakamValues(WalletAdsOfferManager.OfferResponseStatus status) {
    switch (status) {
      case NO_ADS:
        return "no_ads";
      case ADS_HIDE:
        return "ads_block_by_offer";
      case ADS_SHOW:
        return "with_ads";
      default:
        throw new IllegalStateException("Invalid OfferResponseStatus");
    }
  }

  private JSONObject createRakamSuperProperties(String ads) {
    JSONObject superProperties = new JSONObject();
    try {
      superProperties.put(ADS_STATUS_USER_PROPERTY, ads);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return superProperties;
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
