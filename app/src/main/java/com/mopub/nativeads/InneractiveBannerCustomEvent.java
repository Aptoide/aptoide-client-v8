package com.mopub.nativeads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;
import com.fyber.inneractive.sdk.external.InneractiveMediationDefs;
import com.fyber.inneractive.sdk.external.InneractiveMediationName;
import com.fyber.inneractive.sdk.external.InneractiveUnitController.AdDisplayError;
import com.fyber.inneractive.sdk.external.InneractiveUserConfig;
import com.fyber.inneractive.sdk.external.InneractiveUserConfig.Gender;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import java.util.Map;

/**
 * Inneractive Mopub banner plugin class
 */
public class InneractiveBannerCustomEvent extends CustomEventBanner {

  // Set your Inneractive's spot ID!
  private static String SAMPLE_BANNER_SPOT_ID = "Set_Your_Inneractive_Spot_Id";
  //for example, inneractive's spot id for testing: "150942"

  // Members
  /**
   * Mopub's callback listener
   */
  CustomEventBannerListener customEventListener;
  /**
   * The Spot object for the banner
   */
  InneractiveAdSpot mBannerSpot;

  /**
   * Called by the Mopub infra-structure when Mopub requests a banner from Inneractive
   *
   * @param context Android context
   * @param customEventBannerListener callback interface
   * @param localExtras map of local parameters, which were passed by a call to setLocalExtras
   * @param serverExtras map of parameters, as defined in the Mopub console
   */
  @Override protected void loadBanner(final Context context,
      final CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras,
      Map<String, String> serverExtras) {

    Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
        InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - loadBanner");

    setAutomaticImpressionAndClickTracking(false);

    customEventListener = customEventBannerListener;
    // Set variables from MoPub console.
    String resultSpotId = null;
    String resultKeywords = null;

    if (serverExtras != null) {
      resultSpotId = serverExtras.get(InneractiveMediationDefs.REMOTE_KEY_SPOT_ID);
      resultKeywords = serverExtras.get(InneractiveMediationDefs.REMOTE_KEY_KEYWORDS);
    }

    //Make sure we have a spotId from the request.
    if (TextUtils.isEmpty(resultSpotId)) {
      resultSpotId = SAMPLE_BANNER_SPOT_ID;
      if (TextUtils.isEmpty(resultSpotId)) {
        customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        return;
      }
    }

    Gender gender = null;
    int age = 0;
    String zipCode = null;
    if (localExtras != null) {
      /* Set keywords variable as defined on MoPub console, you can also define keywords with setlocalExtras in IaMediationActivity class.
			in case the variable is not initialized, the variable will not be in use*/

      if (localExtras.containsKey(InneractiveMediationDefs.KEY_KEYWORDS) && TextUtils.isEmpty(
          resultKeywords)) {
        resultKeywords = (String) localExtras.get(InneractiveMediationDefs.KEY_KEYWORDS);
      }

			/* Set the age variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use*/

      if (localExtras.containsKey(InneractiveMediationDefs.KEY_AGE)) {
        try {
          age = Integer.valueOf(localExtras.get(InneractiveMediationDefs.KEY_AGE)
              .toString());
        } catch (NumberFormatException e) {
          Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
              InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " Invalid Age");
        }
      }

			/* Set the gender variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use*/

      if (localExtras.containsKey(InneractiveMediationDefs.KEY_GENDER)) {
        String genderStr = localExtras.get(InneractiveMediationDefs.KEY_GENDER)
            .toString();
        if (InneractiveMediationDefs.GENDER_MALE.equals(genderStr)) {
          gender = Gender.MALE;
        } else if (InneractiveMediationDefs.GENDER_FEMALE.equals(genderStr)) {
          gender = Gender.FEMALE;
        }
      }

			/* Set zipCode variable as defined on IaMediationActivity class.   
			in case the variable is not initialized, the variable will not be in use*/
      if (localExtras.containsKey(InneractiveMediationDefs.KEY_ZIPCODE)) {
        zipCode = (String) localExtras.get(InneractiveMediationDefs.KEY_ZIPCODE);
      }
    }

    // Destroy previous ad
    if (mBannerSpot != null) {
      mBannerSpot.destroy();
    }

    mBannerSpot = InneractiveAdSpotManager.get()
        .createSpot();
    // Set your mediation name
    mBannerSpot.setMediationName(InneractiveMediationName.MOPUB);

    InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
    mBannerSpot.addUnitController(controller);

    InneractiveAdRequest request = new InneractiveAdRequest(resultSpotId);
    // Set optional parameters for better targeting.
    request.setUserParams(new InneractiveUserConfig().setGender(gender)
        .setZipCode(zipCode)
        .setAge(age));
    if (resultKeywords != null && resultKeywords.length() > 0) {
      request.setKeywords(resultKeywords.toString());
    }

    // Load an Ad
    mBannerSpot.setRequestListener(new InneractiveAdSpot.RequestListener() {
      @Override public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
        if (adSpot != mBannerSpot) {
          Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
              "Wrong Banner Spot: Received - " + adSpot + ", Actual - " + mBannerSpot);
          return;
        }

        // Create a parent layout for the Banner Ad
        ViewGroup layout = new RelativeLayout(context);
        InneractiveAdViewUnitController controller =
            (InneractiveAdViewUnitController) mBannerSpot.getSelectedUnitController();
        controller.setEventsListener(new InneractiveAdViewEventsListener() {
          @Override public void onAdImpression(InneractiveAdSpot adSpot) {
            customEventListener.onBannerImpression();
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - onAdImpression");
          }

          @Override public void onAdClicked(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - inneractiveBannerClicked");
            customEventListener.onBannerClicked();
          }

          @Override public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER
                    + " - inneractiveInternalBrowserDismissed");
          }

          @Override public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER
                    + " - inneractiveAdWillOpenExternalApp");
            // customEventListener.onLeaveApplication();
            // Don't call the onLeaveApplication() API since it causes a false Click event on MoPub
          }

          @Override
          public void onAdEnteredErrorState(InneractiveAdSpot adSpot, AdDisplayError error) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER
                    + " - onAdEnteredErrorState - "
                    + error.getMessage());
          }

          @Override public void onAdExpanded(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - inneractiveBannerExpanded");
            customEventListener.onBannerExpanded();
          }

          @Override public void onAdResized(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - inneractiveBannerResized");
          }

          @Override public void onAdCollapsed(InneractiveAdSpot adSpot) {
            Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
                InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - inneractiveBannerCollapsed");
            customEventListener.onBannerCollapsed();
          }
        });

        Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
            InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - inneractiveBannerLoaded");

        controller.bindView(layout);
        customEventListener.onBannerLoaded(layout);
      }

      @Override public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot,
          InneractiveErrorCode errorCode) {
        Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
            InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER
                + " - inneractiveBannerFailed with Error: "
                + errorCode);
        if (errorCode == InneractiveErrorCode.CONNECTION_ERROR) {
          customEventListener.onBannerFailed(MoPubErrorCode.NO_CONNECTION);
        } else if (errorCode == InneractiveErrorCode.CONNECTION_TIMEOUT) {
          customEventListener.onBannerFailed(MoPubErrorCode.NETWORK_TIMEOUT);
        } else if (errorCode == InneractiveErrorCode.NO_FILL) {
          customEventListener.onBannerFailed(MoPubErrorCode.NO_FILL);
        } else {
          customEventListener.onBannerFailed(MoPubErrorCode.SERVER_ERROR);
        }
      }
    });

    mBannerSpot.requestAd(request);
  }

  /**
   * Called when an ad view should be cleared
   */
  @Override protected void onInvalidate() {
    Log.d(InneractiveMediationDefs.INNERACTIVE_MEDIATION_SAMPLE_APP_TAG,
        InneractiveMediationDefs.IA_LOG_FOR_MOPUB_BANNER + " - onInvalidate");
    if (mBannerSpot != null) {
      mBannerSpot.destroy();
      mBannerSpot = null;
    }
  }
}
