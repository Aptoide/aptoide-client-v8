package cm.aptoide.pt.ads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.applovin.nativeAds.AppLovinNativeAd;
import com.applovin.nativeAds.AppLovinNativeAdLoadListener;
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinPostbackListener;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.mopub.common.MoPub;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.nativeads.CustomEventNative;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.StaticNativeAd;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;

/**
 * AppLovin SDK native adapter for MoPub.
 * <p>
 */

public class AppLovinCustomEventNative extends CustomEventNative
    implements AppLovinNativeAdLoadListener {

  private static final boolean LOGGING_ENABLED = true;
  private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

  private AppLovinSdk sdk;
  private CustomEventNativeListener nativeListener;
  private Context context;

  //
  // MoPub Custom Event Methods
  //

  private static void log(final int priority, final String message) {
    if (LOGGING_ENABLED) {
      Log.println(priority, "AppLovinNative", message);
    }
  }

  //
  // Native Ad Load Listener
  //

  private static NativeErrorCode toMoPubErrorCode(final int applovinErrorCode) {
    if (applovinErrorCode == AppLovinErrorCodes.NO_FILL) {
      return NativeErrorCode.NETWORK_NO_FILL;
    } else if (applovinErrorCode == AppLovinErrorCodes.UNSPECIFIED_ERROR) {
      return NativeErrorCode.NETWORK_INVALID_STATE;
    } else if (applovinErrorCode == AppLovinErrorCodes.NO_NETWORK) {
      return NativeErrorCode.CONNECTION_ERROR;
    } else if (applovinErrorCode == AppLovinErrorCodes.FETCH_AD_TIMEOUT) {
      return NativeErrorCode.NETWORK_TIMEOUT;
    } else if (applovinErrorCode == AppLovinErrorCodes.UNABLE_TO_PREPARE_NATIVE_AD) {
      return NativeErrorCode.INVALID_RESPONSE;
    } else {
      return NativeErrorCode.UNSPECIFIED;
    }
  }

  /**
   * Performs the given runnable on the main thread.
   */
  private static void runOnUiThread(final Runnable runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      runnable.run();
    } else {
      UI_HANDLER.post(runnable);
    }
  }

  /**
   * Retrieves the appropriate instance of AppLovin's SDK from the SDK key given in the server
   * parameters, or Android Manifest.
   */
  private static AppLovinSdk retrieveSdk(final Map<String, String> serverExtras,
      final Context context) {
    final String sdkKey = serverExtras != null ? serverExtras.get("sdk_key") : null;
    final AppLovinSdk sdk;

    if (!TextUtils.isEmpty(sdkKey)) {
      sdk = AppLovinSdk.getInstance(sdkKey, new AppLovinSdkSettings(), context);
    } else {
      sdk = AppLovinSdk.getInstance(context);
    }

    return sdk;
  }

  @Override public void loadNativeAd(final Context context,
      final CustomEventNativeListener customEventNativeListener,
      final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
    log(DEBUG, "Requesting AppLovin native ad with server extras: " + serverExtras);

    // Pass the user consent from the MoPub SDK as per GDPR
    PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();
    if (personalInfoManager != null && personalInfoManager.gdprApplies()) {
      boolean canCollectPersonalInfo = personalInfoManager.canCollectPersonalInformation();
      AppLovinPrivacySettings.setHasUserConsent(canCollectPersonalInfo, context);
    }

    this.context = context;
    this.nativeListener = customEventNativeListener;

    sdk = retrieveSdk(serverExtras, context);
    sdk.setPluginVersion("MoPub-3.0.0");

    sdk.getNativeAdService()
        .loadNativeAds(1, this);
  }

  //
  // Utility Methods
  //

  @Override public void onNativeAdsLoaded(final List nativeAds) {
    final AppLovinNativeAd nativeAd = (AppLovinNativeAd) nativeAds.get(0);

    log(DEBUG, "Native ad did load ad: " + nativeAd.getAdId());

    final List<String> imageUrls = new ArrayList<>(2);

    if (nativeAd.getIconUrl() != null) imageUrls.add(nativeAd.getIconUrl());
    if (nativeAd.getImageUrl() != null) imageUrls.add(nativeAd.getImageUrl());

    // Please note: If/when we add support for videos, we must use AppLovin SDK's built-in precaching mechanism

    runOnUiThread(new Runnable() {
      @Override public void run() {
        NativeImageHelper.preCacheImages(context, imageUrls, new NativeImageHelper.ImageListener() {
          @Override public void onImagesCached() {
            handleNativeAdFinishedCaching(nativeAd);
          }

          @Override public void onImagesFailedToCache(NativeErrorCode nativeErrorCode) {
            handleNativeAdFinishedCaching(nativeAd);
          }
        });
      }
    });
  }

  @Override public void onNativeAdsFailedToLoad(final int errorCode) {
    log(ERROR, "Native ad video failed to load with error: " + errorCode);
    nativeListener.onNativeAdFailed(toMoPubErrorCode(errorCode));
  }

  private void handleNativeAdFinishedCaching(final AppLovinNativeAd nativeAd) {
    log(DEBUG, "Native ad done precaching");

    final AppLovinMopubNativeAd appLovinMopubNativeAd =
        new AppLovinMopubNativeAd(nativeAd, context);
    nativeListener.onNativeAdLoaded(appLovinMopubNativeAd);
  }

  private class AppLovinMopubNativeAd extends StaticNativeAd {
    private final AppLovinNativeAd parentNativeAd;
    private final Context parentContext;
    private View parentView;

    AppLovinMopubNativeAd(final AppLovinNativeAd nativeAd, final Context context) {
      parentNativeAd = nativeAd;
      parentContext = context;

      setTitle(nativeAd.getTitle());
      setText(nativeAd.getDescriptionText());
      setIconImageUrl(nativeAd.getIconUrl());
      setMainImageUrl(nativeAd.getImageUrl());
      setCallToAction(nativeAd.getCtaText());
      setStarRating((double) nativeAd.getStarRating());
      setClickDestinationUrl(nativeAd.getClickUrl());
    }

    @Override public void prepare(@NonNull final View view) {
      // PLEASE NOTE: Use the code below if you would like AppLovin to handle the ad clicks for you:
            /*
            final View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    parentNativeAd.launchClickTarget( parentContext );
                    notifyAdClicked();
                }
            };
            parentView = view;
            parentView.setOnClickListener( onClickListener );
            // If you need to make subviews of the view clickable (e.g. CTA button), apply the click listener to them:
            parentView.findViewById( R.id.ID_OF_SUBVIEW ).setOnClickListener( onClickListener );
            */

      // As of AppLovin SDK >=7.1.0, impression tracking convenience methods have been added to AppLovinNativeAd
      parentNativeAd.trackImpression(new AppLovinPostbackListener() {
        @Override public void onPostbackSuccess(String url) {
          log(DEBUG, "Native ad impression successfully executed.");
          notifyAdImpressed();
        }

        @Override public void onPostbackFailure(String url, int errorCode) {
          log(ERROR, "Native ad impression failed to execute.");
        }
      });
    }

    @Override public void clear(@NonNull final View view) {
      parentView = null;
    }

    @Override public void destroy() {
      AppLovinCustomEventNative.this.nativeListener = null;
    }
  }
}
