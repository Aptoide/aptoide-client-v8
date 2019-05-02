package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.MoPub;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inmobi.sdk.InMobiSdk.IM_GDPR_CONSENT_AVAILABLE;

public class InMobiInterstitialCustomEvent extends CustomEventInterstitial {

  private static final String TAG = InMobiInterstitialCustomEvent.class.getSimpleName();
  private static boolean mIsInMobiSdkInitialized = false;
  private CustomEventInterstitialListener mInterstitialListener;
  private JSONObject serverParams;
  private String accountId = "";
  private long placementId = -1;
  private InMobiInterstitial iMInterstitial;

  @Override protected void loadInterstitial(Context context,
      CustomEventInterstitialListener interstitialListener, Map<String, Object> localExtras,
      Map<String, String> serverExtras) {

    mInterstitialListener = interstitialListener;
    InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

    Activity activity;
    if (context != null && context instanceof Activity) {
      activity = (Activity) context;
    } else {
      Log.w(TAG, "Context not an Activity. Returning error!");
      mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
      return;
    }

    try {
      serverParams = new JSONObject(serverExtras);
    } catch (Exception e) {
      Log.e(TAG, "Could not parse server parameters");
      e.printStackTrace();
    }

    try {
      accountId = serverParams.getString("accountid");
      placementId = serverParams.getLong("placementid");
    } catch (JSONException e1) {
      e1.printStackTrace();
    }

    final JSONObject gdprJson = new JSONObject();
    if (InMobiGDPR.isConsentUpdated()) {
      try {
        gdprJson.put(IM_GDPR_CONSENT_AVAILABLE, InMobiGDPR.getConsent());
        gdprJson.put("gdpr", InMobiGDPR.isGDPR());
      } catch (JSONException e) {
        Log.d(TAG, "Unable to set GDPR consent object");
        Log.e(TAG, e.getMessage());
      }
    }

    if (!mIsInMobiSdkInitialized) {
      try {
        InMobiSdk.init(context, accountId, gdprJson);
        mIsInMobiSdkInitialized = true;
      } catch (Exception e) {
        e.printStackTrace();
        mIsInMobiSdkInitialized = false;
        mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
        return;
      }
    }

    /*
     * You may also pass the Placement ID by
     * specifying Custom Event Data in MoPub's web interface.
     */

    iMInterstitial =
        new InMobiInterstitial(activity, placementId, new InterstitialAdEventListener() {
          @Override public void onAdLoadSucceeded(InMobiInterstitial inMobiInterstitial) {
            super.onAdLoadSucceeded(inMobiInterstitial);
            Log.d(TAG, "InMobi interstitial ad loaded successfully.");
            if (mInterstitialListener != null) {
              mInterstitialListener.onInterstitialLoaded();
            }
          }

          @Override public void onAdLoadFailed(InMobiInterstitial inMobiInterstitial,
              InMobiAdRequestStatus inMobiAdRequestStatus) {
            super.onAdLoadFailed(inMobiInterstitial, inMobiAdRequestStatus);
            Log.d(TAG, "InMobi interstitial ad failed to load.");
            if (mInterstitialListener != null) {

              if (inMobiAdRequestStatus.getStatusCode() == StatusCode.INTERNAL_ERROR) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
              } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_INVALID) {
                mInterstitialListener.onInterstitialFailed(
                    MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
              } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NETWORK_UNREACHABLE) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
              } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NO_FILL) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
              } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_TIMED_OUT) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_TIMEOUT);
              } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.SERVER_ERROR) {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.SERVER_ERROR);
              } else {
                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
              }
            }
          }

          @Override public void onAdReceived(InMobiInterstitial inMobiInterstitial) {
            super.onAdReceived(inMobiInterstitial);
            Log.d(TAG, "InMobi Adserver responded with an Ad");
          }

          @Override public void onAdClicked(InMobiInterstitial inMobiInterstitial,
              Map<Object, Object> params) {
            super.onAdClicked(inMobiInterstitial, params);
            Log.d(TAG, "InMobi interstitial interaction happening.");
            if (mInterstitialListener != null) {
              mInterstitialListener.onInterstitialClicked();
            }
          }

          @Override public void onAdWillDisplay(InMobiInterstitial inMobiInterstitial) {
            super.onAdWillDisplay(inMobiInterstitial);
            Log.d(TAG, "Interstitial ad will display.");
          }

          @Override public void onAdDisplayed(InMobiInterstitial inMobiInterstitial) {
            super.onAdDisplayed(inMobiInterstitial);
            Log.d(TAG, "InMobi interstitial show on screen.");
            if (mInterstitialListener != null) {
              mInterstitialListener.onInterstitialShown();
            }
          }

          @Override public void onAdDisplayFailed(InMobiInterstitial inMobiInterstitial) {
            super.onAdDisplayFailed(inMobiInterstitial);
            Log.d(TAG, "Interstitial ad failed to display.");
          }

          @Override public void onAdDismissed(InMobiInterstitial inMobiInterstitial) {
            super.onAdDismissed(inMobiInterstitial);
          }

          @Override public void onUserLeftApplication(InMobiInterstitial inMobiInterstitial) {
            super.onUserLeftApplication(inMobiInterstitial);
          }

          @Override public void onRewardsUnlocked(InMobiInterstitial inMobiInterstitial,
              Map<Object, Object> rewards) {
            Log.d(TAG, "InMobi interstitial onRewardsUnlocked.");

            if (null != rewards) {
              Iterator<Object> iterator = rewards.keySet()
                  .iterator();
              while (iterator.hasNext()) {
                String key = iterator.next()
                    .toString();
                String value = rewards.get(key)
                    .toString();
                Log.d("Rewards: ", key + ":" + value);
              }
            }
          }
        });
		/*
		Sample for setting up the InMobi SDK Demographic params.
        Publisher need to set the values of params as they want.

		InMobiSdk.setAreaCode("areacode");
		InMobiSdk.setEducation(Education.HIGH_SCHOOL_OR_LESS);
		InMobiSdk.setGender(Gender.MALE);
		InMobiSdk.setIncome(1000);
		InMobiSdk.setAge(23);
		InMobiSdk.setPostalCode("postalcode");
		InMobiSdk.setLogLevel(LogLevel.DEBUG);
		InMobiSdk.setLocationWithCityStateCountry("blore", "kar", "india");
		InMobiSdk.setLanguage("ENG");
		InMobiSdk.setInterests("dance");
		InMobiSdk.setYearOfBirth(1980);*/
    Map<String, String> map = new HashMap<>();
    map.put("tp", "c_mopub");
    map.put("tp-ver", MoPub.SDK_VERSION);
    iMInterstitial.setExtras(map);
    iMInterstitial.load();
  }

  /*
   * Abstract methods from CustomEventInterstitial
   */

  @Override public void showInterstitial() {
    if (iMInterstitial != null && iMInterstitial.isReady()) {
      iMInterstitial.show();
    }
  }

  @Override public void onInvalidate() {
  }
}
