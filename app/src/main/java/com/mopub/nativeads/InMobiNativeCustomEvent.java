package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.InMobiNative.NativeAdListener;
import com.inmobi.ads.listeners.NativeAdEventListener;
import com.inmobi.ads.listeners.VideoEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.MoPub;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inmobi.sdk.InMobiSdk.IM_GDPR_CONSENT_AVAILABLE;
import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

public class InMobiNativeCustomEvent extends CustomEventNative {

  public static final String TAG = InMobiNativeCustomEvent.class.getSimpleName();

  private static final String SERVER_EXTRA_ACCOUNT_ID = "accountid";

  private static final String SERVER_EXTRA_PLACEMENT_ID = "placementid";
  private static boolean mIsInMobiSdkInitialized = false;
  private static ConcurrentHashMap<Integer, InMobiNativeAd> STATIC_MAP =
      new ConcurrentHashMap<>(10, 0.9f, 10);
  private InMobiNativeAd inMobiStaticNativeAd;

  @Override protected void loadNativeAd(@NonNull Context context,
      @NonNull CustomEventNativeListener customEventNativeListener,
      @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) {

    String accountId;
    long placementId;
    accountId = serverExtras.get(SERVER_EXTRA_ACCOUNT_ID);
    placementId = Long.parseLong(serverExtras.get(SERVER_EXTRA_PLACEMENT_ID));
    Log.d(TAG, "Server Extras: Account ID:" + accountId + " Placement ID:" + placementId);

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
        InMobiSdk.init(context, accountId);
        mIsInMobiSdkInitialized = true;
      } catch (Exception e) {
        e.printStackTrace();
        mIsInMobiSdkInitialized = false;
        customEventNativeListener.onNativeAdFailed(
            NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
        return;
      }
    }
    InMobiSdk.updateGDPRConsent(gdprJson);

    InMobiSdk.setLogLevel(InMobiSdk.LogLevel.ERROR);

		/*
            Sample for setting up the InMobi SDK Demographic params.
            Publisher need to set the values of params as they want.
         */

		/*InMobiSdk.setAreaCode("areacode");
        InMobiSdk.setEducation(Education.HIGH_SCHOOL_OR_LESS);
		InMobiSdk.setGender(Gender.MALE);
		InMobiSdk.setAge(23);
		InMobiSdk.setPostalCode("postalcode");
		InMobiSdk.setLogLevel(LogLevel.DEBUG);
		InMobiSdk.setLocationWithCityStateCountry("blore", "kar", "india");
		InMobiSdk.setLanguage("ENG");
		InMobiSdk.setInterests("dance");
		InMobiSdk.setYearOfBirth(1980);*/

		/*
            Mandatory Params to set in the code by the publisher to identify the supply source type
         */

    Map<String, String> map = new HashMap<>();
    map.put("tp", "c_mopub");
    map.put("tp-ver", MoPub.SDK_VERSION);

    inMobiStaticNativeAd = new InMobiNativeAd(context, customEventNativeListener, placementId);

    inMobiStaticNativeAd.setExtras(map);
    inMobiStaticNativeAd.loadAd();
    STATIC_MAP.putIfAbsent(inMobiStaticNativeAd.hashCode(), inMobiStaticNativeAd);
  }

  public static class InMobiNativeAd extends BaseNativeAd implements NativeAdListener {

    private static final String TAG = "InMobiNativeAd";
    private final CustomEventNativeListener mCustomEventNativeListener;
    private final NativeClickHandler mNativeClickHandler;
    private final InMobiNative mInMobiNative;
    private final Context mContext;
    private boolean mIsImpressionRecorded = false;
    private boolean mIsClickRecorded = false;
    private NativeAdEventListener nativeAdEventListener = new NativeAdEventListener() {
      @Override public void onAdLoadSucceeded(final InMobiNative inMobiNative) {
        super.onAdLoadSucceeded(inMobiNative);
        Log.i(TAG, "InMobi Native Ad loaded successfully");

        final List<String> imageUrls = new ArrayList<>();
        final String iconImageUrl = getAdIconUrl();
        if (iconImageUrl != null) {
          imageUrls.add(iconImageUrl);
        }
        preCacheImages(mContext, imageUrls, new NativeImageHelper.ImageListener() {
          @Override public void onImagesCached() {
            mCustomEventNativeListener.onNativeAdLoaded(InMobiNativeAd.this);
          }

          @Override public void onImagesFailedToCache(NativeErrorCode errorCode) {
            mCustomEventNativeListener.onNativeAdFailed(errorCode);
          }
        });
        STATIC_MAP.remove(this.hashCode());
      }

      @Override public void onAdLoadFailed(final InMobiNative inMobiNative,
          final InMobiAdRequestStatus inMobiAdRequestStatus) {
        super.onAdLoadFailed(inMobiNative, inMobiAdRequestStatus);
        String errorMessage = "Failed to load Native Strand:";
        switch (inMobiAdRequestStatus.getStatusCode()) {
          case INTERNAL_ERROR:
            errorMessage += "INTERNAL_ERROR";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
            break;

          case REQUEST_INVALID:
            errorMessage += "INVALID_REQUEST";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_REQUEST);
            break;

          case NETWORK_UNREACHABLE:
            errorMessage += "NETWORK_UNREACHABLE";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.CONNECTION_ERROR);
            break;

          case NO_FILL:
            errorMessage += "NO_FILL";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
            break;

          case REQUEST_PENDING:
            errorMessage += "REQUEST_PENDING";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
            break;

          case REQUEST_TIMED_OUT:
            errorMessage += "REQUEST_TIMED_OUT";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_TIMEOUT);
            break;

          case SERVER_ERROR:
            errorMessage += "SERVER_ERROR";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.SERVER_ERROR_RESPONSE_CODE);
            break;

          case AD_ACTIVE:
            errorMessage += "AD_ACTIVE";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
            break;

          case EARLY_REFRESH_REQUEST:
            errorMessage += "EARLY_REFRESH_REQUEST";
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
            break;

          default:
            errorMessage = "UNKNOWN_ERROR" + inMobiAdRequestStatus.getStatusCode();
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
            break;
        }
        Log.w(TAG, errorMessage);
        STATIC_MAP.remove(this.hashCode());
        destroy();
      }

      @Override public void onAdFullScreenDismissed(final InMobiNative inMobiNative) {
        super.onAdFullScreenDismissed(inMobiNative);
        Log.d(TAG, "InMobi Native ad onAdFullScreenDismissed");
      }

      @Override public void onAdFullScreenWillDisplay(final InMobiNative inMobiNative) {
        super.onAdFullScreenWillDisplay(inMobiNative);
        Log.d(TAG, "InMobi Native ad onAdFullScreenWillDisplay");
      }

      @Override public void onAdFullScreenDisplayed(final InMobiNative inMobiNative) {
        super.onAdFullScreenDisplayed(inMobiNative);
        Log.d(TAG, "InMobi Native ad onAdFullScreenDisplayed");
      }

      @Override public void onUserWillLeaveApplication(final InMobiNative inMobiNative) {
        super.onUserWillLeaveApplication(inMobiNative);
        Log.d(TAG, "InMobi Native ad onUserWillLeaveApplication");
      }

      @Override public void onAdImpressed(final InMobiNative inMobiNative) {
        super.onAdImpressed(inMobiNative);
        Log.d(TAG, "InMobi Native ad is displayed");
        if (!mIsImpressionRecorded) {
          mIsImpressionRecorded = true;
          notifyAdImpressed();
        }
      }

      @Override public void onAdClicked(final InMobiNative inMobiNative) {
        super.onAdClicked(inMobiNative);
        Log.d(TAG, "InMobi Native ad is clicked");
        if (!mIsClickRecorded) {
          notifyAdClicked();
          mIsClickRecorded = true;
        }
      }

      @Override public void onAdStatusChanged(final InMobiNative inMobiNative) {
        super.onAdStatusChanged(inMobiNative);
        Log.d(TAG, "InMobi Native ad onAdStatusChanged");
      }
    };

    InMobiNativeAd(@NonNull final Context context,
        @NonNull final CustomEventNativeListener customEventNativeListener, long placementId) {
      mContext = context;
      mNativeClickHandler = new NativeClickHandler(context);
      mCustomEventNativeListener = customEventNativeListener;
      if (context instanceof Activity) {
        mInMobiNative = new InMobiNative((Activity) context, placementId, nativeAdEventListener);
      } else {
        mInMobiNative = new InMobiNative(context, placementId, nativeAdEventListener);
      }
    }

    void setExtras(Map<String, String> map) {
      mInMobiNative.setExtras(map);
    }

    void loadAd() {
      mInMobiNative.setVideoEventListener(new VideoEventListener() {
        @Override public void onVideoCompleted(final InMobiNative inMobiNative) {
          super.onVideoCompleted(inMobiNative);
          Log.d(TAG, "InMobi Native Video completed");
        }

        @Override public void onVideoSkipped(final InMobiNative inMobiNative) {
          super.onVideoSkipped(inMobiNative);
          Log.d(TAG, "InMobi Native Video skipped");
        }

        @Override
        public void onAudioStateChanged(final InMobiNative inMobiNative, final boolean b) {
          super.onAudioStateChanged(inMobiNative, b);
          Log.d(TAG, "InMobi Native Video onAudioStateChanged");
        }
      });
      mInMobiNative.load();
    }

    /**
     * Returns the String corresponding to the ad's title.
     */
    final public String getAdTitle() {
      return mInMobiNative.getAdTitle();
    }

    /**
     * Returns the String corresponding to the ad's description text. May be null.
     */
    final public String getAdDescription() {
      return mInMobiNative.getAdDescription();
    }

    /**
     * Returns the String url corresponding to the ad's icon image. May be null.
     */
    final public String getAdIconUrl() {
      return mInMobiNative.getAdIconUrl();
    }

    /**
     * Returns the Call To Action String (i.e. "Install" or "Learn More") associated with this ad.
     */
    final public String getAdCtaText() {
      return mInMobiNative.getAdCtaText();
    }

    final public Float getAdRating() {
      return mInMobiNative.getAdRating();
    }

    final public View getPrimaryAdView(ViewGroup parent) {
      int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, parent.getWidth(),
          mContext.getResources()
              .getDisplayMetrics());
      return mInMobiNative.getPrimaryViewOfWidth(parent.getContext(), null, parent, width);
    }

    final public void onCtaClick() {
      mInMobiNative.reportAdClickAndOpenLandingPage();
    }

    @Override public void prepare(@NonNull View view) {
    }

    @Override public void clear(@NonNull View view) {
      mNativeClickHandler.clearOnClickListener(view);
    }

    @Override public void destroy() {
      mInMobiNative.destroy();
    }

    @Override public void onAdLoadSucceeded(@NonNull InMobiNative inMobiNative) {
      Log.i(TAG, "InMobi Native Ad loaded successfully");

      final List<String> imageUrls = new ArrayList<>();
      final String iconImageUrl = getAdIconUrl();
      if (iconImageUrl != null) {
        imageUrls.add(iconImageUrl);
      }
      preCacheImages(mContext, imageUrls, new NativeImageHelper.ImageListener() {
        @Override public void onImagesCached() {
          mCustomEventNativeListener.onNativeAdLoaded(InMobiNativeAd.this);
        }

        @Override public void onImagesFailedToCache(NativeErrorCode errorCode) {
          mCustomEventNativeListener.onNativeAdFailed(errorCode);
        }
      });
      STATIC_MAP.remove(this.hashCode());
    }

    @Override public void onAdLoadFailed(@NonNull InMobiNative InMobiNative,
        @NonNull InMobiAdRequestStatus requestStatus) {
      String errorMessage = "Failed to load Native Strand:";
      switch (requestStatus.getStatusCode()) {
        case INTERNAL_ERROR:
          errorMessage += "INTERNAL_ERROR";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
          break;

        case REQUEST_INVALID:
          errorMessage += "INVALID_REQUEST";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_REQUEST);
          break;

        case NETWORK_UNREACHABLE:
          errorMessage += "NETWORK_UNREACHABLE";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.CONNECTION_ERROR);
          break;

        case NO_FILL:
          errorMessage += "NO_FILL";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
          break;

        case REQUEST_PENDING:
          errorMessage += "REQUEST_PENDING";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          break;

        case REQUEST_TIMED_OUT:
          errorMessage += "REQUEST_TIMED_OUT";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_TIMEOUT);
          break;

        case SERVER_ERROR:
          errorMessage += "SERVER_ERROR";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.SERVER_ERROR_RESPONSE_CODE);
          break;

        case AD_ACTIVE:
          errorMessage += "AD_ACTIVE";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          break;

        case EARLY_REFRESH_REQUEST:
          errorMessage += "EARLY_REFRESH_REQUEST";
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          break;

        default:
          errorMessage = "UNKNOWN_ERROR" + requestStatus.getStatusCode();
          mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          break;
      }
      Log.w(TAG, errorMessage);
      STATIC_MAP.remove(this.hashCode());
      destroy();
    }

    @Override public void onAdFullScreenDismissed(InMobiNative inMobiNative) {

    }

    @Override public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {

    }

    @Override public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {

    }

    @Override public void onUserWillLeaveApplication(InMobiNative inMobiNative) {

    }

    @Override public void onAdImpressed(@NonNull InMobiNative InMobiNative) {
      if (!mIsImpressionRecorded) {
        mIsImpressionRecorded = true;
        notifyAdImpressed();
      }
    }

    @Override public void onAdClicked(@NonNull InMobiNative InMobiNative) {
      if (!mIsClickRecorded) {
        notifyAdClicked();
        mIsClickRecorded = true;
      }
    }

    @Override public void onMediaPlaybackComplete(@NonNull InMobiNative inMobiNative) {

    }

    @Override public void onAdStatusChanged(@NonNull InMobiNative inMobiNative) {

    }

    @Override public void onUserSkippedMedia(@NonNull InMobiNative inMobiNative) {

    }
  }
}
