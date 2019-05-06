package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenAdEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController;
import com.fyber.inneractive.sdk.external.InneractiveFullscreenVideoContentController;
import com.fyber.inneractive.sdk.external.InneractiveMediationDefs;
import com.fyber.inneractive.sdk.external.InneractiveMediationName;
import com.fyber.inneractive.sdk.external.InneractiveUnitController.AdDisplayError;
import com.fyber.inneractive.sdk.external.InneractiveUserConfig;
import com.fyber.inneractive.sdk.external.InneractiveUserConfig.Gender;
import com.fyber.inneractive.sdk.external.VideoContentListener;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

/**
 * Inneractive Mopub interstitial plugin class
 */
public class InneractiveInterstitialCustomEvent extends CustomEventInterstitial {

  // Set your Inneractive's spot ID!
  private static final String SAMPLE_INTERSTITIAL_SPOT_ID = "Set_Your_Inneractive_Spot_Id";
  //for example, inneractive's spot id for testing: "150946"

  // Members
  /**
   * Inneractived interstitial ad
   */
  InneractiveAdSpot mInterstitialSpot;
  /**
   * Context for showing the Ad
   */
  Context mContext;
  /**
   * The plugin fires events into Mopub's callback listener
   */
  private CustomEventInterstitialListener mInterstitialListener;

  /**
   * Called by Mopub in order to start loading an interstitial ad
   *
   * @param context Android context
   * @param listener Mopub's external listener
   * @param localExtras map of local parameters, which were passed by a call to setLocalExtras
   * @param serverExtras map of parameters, as defined in the Mopub console
   */
  @Override protected void loadInterstitial(final Context context,
      final CustomEventInterstitialListener listener, Map<String, Object> localExtras,
      Map<String, String> serverExtras) {

    Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
        InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL + " - loadInterstitial");

    setAutomaticImpressionAndClickTracking(false);

    // Set variables from MoPub console.
    String resultSpotId = null;
    String resultKeywords = null;

    if (serverExtras != null) {
      resultSpotId = serverExtras.get(InneractiveMediationDefs.REMOTE_KEY_SPOT_ID);
      if (!TextUtils.isEmpty(serverExtras.get(InneractiveMediationDefs.REMOTE_KEY_KEYWORDS))) {
        resultKeywords = serverExtras.get(InneractiveMediationDefs.REMOTE_KEY_KEYWORDS);
      }
    }

    //Make sure we have a spotId from the request.
    if (TextUtils.isEmpty(resultSpotId)) {
      resultSpotId = SAMPLE_INTERSTITIAL_SPOT_ID;
      if (TextUtils.isEmpty(resultSpotId)) {
        listener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        return;
      }
    }

    if (!(context instanceof Activity)) {
      listener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
      return;
    }

    mInterstitialListener = listener;
    mContext = context;

    Gender gender = null;
    int age = 0;
    String zipCode = null;
    if (localExtras != null) {

			/* Set keywords variable as defined on MoPub console, you can also define keywords with setlocalExtras in IaMediationActivity class.
      in case the variable is not initialized, the variable will not be in use */
      if (localExtras.containsKey(InneractiveMediationDefs.KEY_KEYWORDS) && TextUtils.isEmpty(
          resultKeywords)) {
        resultKeywords = (String) localExtras.get(InneractiveMediationDefs.KEY_KEYWORDS);
      }
            
			/* Set the age variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use */
      if (localExtras.containsKey(InneractiveMediationDefs.KEY_AGE)) {
        try {
          age = Integer.valueOf(localExtras.get(InneractiveMediationDefs.KEY_AGE)
              .toString());
        } catch (NumberFormatException e) {
          Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
              InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL + " Invalid Age");
        }
      }

			/* Set the gender variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use */
      if (localExtras.containsKey(InneractiveMediationDefs.KEY_GENDER)) {
        String genderStr = localExtras.get(InneractiveMediationDefs.KEY_GENDER)
            .toString();
        if (genderStr.equals(InneractiveMediationDefs.GENDER_MALE)) {
          gender = Gender.MALE;
        } else if (genderStr.equals(InneractiveMediationDefs.GENDER_FEMALE)) {
          gender = (Gender.FEMALE);
        }
      }

			/* Set zipCode variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use */
      if (localExtras.containsKey(InneractiveMediationDefs.KEY_ZIPCODE)) {
        zipCode = (String) localExtras.get(InneractiveMediationDefs.KEY_ZIPCODE);
      }
    }

    if (mInterstitialSpot != null) {
      mInterstitialSpot.destroy();
    }

    mInterstitialSpot = InneractiveAdSpotManager.get()
        .createSpot();
    // Set your mediation name
    mInterstitialSpot.setMediationName(InneractiveMediationName.MOPUB);

    InneractiveFullscreenUnitController fullscreenUnitController =
        new InneractiveFullscreenUnitController();
    mInterstitialSpot.addUnitController(fullscreenUnitController);

    InneractiveAdRequest request = new InneractiveAdRequest(resultSpotId);
    request.setUserParams(new InneractiveUserConfig().setGender(gender)
        .setZipCode(zipCode)
        .setAge(age));
    if (resultKeywords != null && resultKeywords.length() > 0) {
      request.setKeywords(resultKeywords.toString());
    }

    // Load ad
    mInterstitialSpot.setRequestListener(new InneractiveAdSpot.RequestListener() {

      /**
       * Called by Inneractive when an interstitial is ready for display
       * @param adSpot Spot object
       */
      @Override public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
        Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
            InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL
                + " - inneractiveInterstitialLoaded");
        mInterstitialListener.onInterstitialLoaded();
      }

      /**
       * Called by Inneractive an interstitial fails loading
       * @param adSpot Spot object
       * @param errorCode the failure's error.
       */
      @Override public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot,
          InneractiveErrorCode errorCode) {
        Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
            "Failed loading interstitial! with error: " + errorCode);
        if (errorCode == InneractiveErrorCode.CONNECTION_ERROR) {
          mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_CONNECTION);
        } else if (errorCode == InneractiveErrorCode.CONNECTION_TIMEOUT) {
          mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_TIMEOUT);
        } else if (errorCode == InneractiveErrorCode.NO_FILL) {
          mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
        } else {
          mInterstitialListener.onInterstitialFailed(MoPubErrorCode.SERVER_ERROR);
        }
      }
    });

    mInterstitialSpot.requestAd(request);
  }

  /**
   * Called by the Mopub infra-structure in order for the plugin to start showing Inneractive's
   * interstitial
   */
  @Override protected void showInterstitial() {
    Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
        InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL + " - showInterstitial");
    // check if the ad is ready
    if (mInterstitialSpot != null && mInterstitialSpot.isReady()) {

      InneractiveFullscreenUnitController fullscreenUnitController =
          (InneractiveFullscreenUnitController) mInterstitialSpot.getSelectedUnitController();
      fullscreenUnitController.setEventsListener(new InneractiveFullscreenAdEventsListener() {

        /**
         * Called by Inneractive when an interstitial ad activity is shown
         * @param adSpot Spot object
         */
        @Override public void onAdImpression(InneractiveAdSpot adSpot) {
          mInterstitialListener.onInterstitialShown();
          mInterstitialListener.onInterstitialImpression();
          Log.i(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL, "onAdImpression");
        }

        /**
         * Called by Inneractive when an interstitial ad is clicked
         * @param adSpot Spot object
         */
        @Override public void onAdClicked(InneractiveAdSpot adSpot) {
          mInterstitialListener.onInterstitialClicked();
          Log.i(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL, "onAdClicked");
        }

        /**
         * Called by Inneractive when an interstitial ad opened an external application
         * @param adSpot Spot object
         */
        @Override public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
          Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
              "inneractiveAdWillOpenExternalApp");
          // Don't call the onLeaveApplication() API since it causes a false Click event on MoPub
        }

        /**
         * Called when an ad has entered an error state, this will only happen when the ad is being shown
         * @param adSpot the relevant ad spot
         */
        @Override public void onAdEnteredErrorState(InneractiveAdSpot adSpot,
            AdDisplayError error) {
          Log.i(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
              "onAdEnteredErrorState - " + error.getMessage());
        }

        /**
         * Called by Inneractive when Inneractive's internal browser, which was opened by this interstitial, was closed
         * @param adSpot Spot object
         */
        @Override public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
          Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
              "inneractiveInternalBrowserDismissed");
        }

        /**
         * Called by Inneractive when an interstitial ad activity is closed
         * @param adSpot Spot object
         */
        @Override public void onAdDismissed(InneractiveAdSpot adSpot) {
          mInterstitialListener.onInterstitialDismissed();
          Log.i(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL, "onAdDismissed");
        }
      });

      // Add video content controller, for controlling video ads
      InneractiveFullscreenVideoContentController videoContentController =
          new InneractiveFullscreenVideoContentController();
      videoContentController.setEventsListener(new VideoContentListener() {
        @Override public void onProgress(int totalDurationInMsec, int positionInMsec) {
          Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
              "Interstitial: Got video content progress: total time = "
                  + totalDurationInMsec
                  + " position = "
                  + positionInMsec);
        }

        /**
         * Called by inneractive when an Intersititial video ad was played to the end
         * <br>Can be used for incentive flow
         * <br>Note: This event does not indicate that the interstitial was closed
         */
        @Override public void onCompleted() {
          Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
              "Interstitial: Got video content completed event");
        }

        @Override public void onPlayerError() {
          Log.d(InneractiveMediationDefs.IA_LOG_FOR_ADMOB_INTERSTITIAL,
              "Interstitial: Got video content play error event");
        }
      });

      // Now add the content controller to the unit controller
      fullscreenUnitController.addContentController(videoContentController);

      fullscreenUnitController.show((Activity) mContext);
    } else {
      Log.d(InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL,
          "The Interstitial ad is not ready yet.");
    }
  }

  /**
   * Called by Mopub when an ad should be destroyed
   * <br>Destroy the underline Inneractive ad
   */
  @Override protected void onInvalidate() {
    Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
        InneractiveMediationDefs.IA_LOG_FOR_MOPUB_INTERSTITIAL + " - onInvalidate");
    // We do the cleanup on the event of loadInterstitial
  }
}
