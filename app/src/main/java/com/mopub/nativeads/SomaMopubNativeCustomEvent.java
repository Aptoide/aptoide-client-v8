/*
 * Copyright (c) 2018 Smaato Inc. All rights reserved.
 */

package com.mopub.nativeads;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import com.smaato.soma.ActivityIntentHandler;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import com.smaato.soma.internal.nativead.BannerNativeAd;
import com.smaato.soma.nativead.MediationNativeAdListener;
import com.smaato.soma.nativead.NativeAd;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

/**
 * Adapter class for MoPub to Smaato mediation for native ads.
 * For integration please follow the instructions from
 * <a href="https://wiki.smaato.com/display/IN/Android">Smaato Wiki</a> (section "Smaato as
 * secondary").
 * </p>
 * Tested with Smaato v9.0 and Mopub v5.3.0.
 * Created by palani on 04/07/16.
 */
public class SomaMopubNativeCustomEvent extends CustomEventNative {

  private static final String TAG = "SomaMopubNativeCustomEvent";

  @Override protected void loadNativeAd(@NonNull Context context,
      @NonNull CustomEventNativeListener customEventNativeListener,
      @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) {
    try {
      long publisherId = Long.parseLong(serverExtras.get("publisherId"));
      long adSpaceId = Long.parseLong(serverExtras.get("adSpaceId"));

      if (isInputValid(publisherId, adSpaceId)) {
        SmaatoForwardingNativeAd smaatoForwardingNativeAd =
            new SmaatoForwardingNativeAd(context, publisherId, adSpaceId, customEventNativeListener,
                new ImpressionTracker(context), new NativeClickHandler(context));
        smaatoForwardingNativeAd.loadAd();
      } else {
        customEventNativeListener.onNativeAdFailed(
            NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
      }
    } catch (Exception e) {
      Debugger.showLog(
          new LogMessage(TAG, "Exception in Adapter Configuration. Please check inputs",
              Debugger.Level_1, DebugCategory.DEBUG));
      customEventNativeListener.onNativeAdFailed(
          NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
    }
  }

  private boolean isInputValid(long publisherId, long adSpaceId) {
    return publisherId > -1 && adSpaceId > -1;
  }

  static class SmaatoForwardingNativeAd extends StaticNativeAd
      implements MediationNativeAdListener {

    private final Context context;
    private final CustomEventNativeListener mCustomEventNativeListener;
    private final NativeAd nativeAd;

    private ImpressionTracker mImpressionTracker;
    private NativeClickHandler mNativeClickHandler;

    SmaatoForwardingNativeAd(final Context context, final long publisherId, final long adSpaceID,
        final CustomEventNativeListener customEventNativeListener,
        final ImpressionTracker impressionTracker, final NativeClickHandler nativeClickHandler) {
      this.context = context.getApplicationContext();
      nativeAd = new NativeAd(context.getApplicationContext());
      nativeAd.getAdSettings()
          .setAdspaceId(adSpaceID);
      nativeAd.getAdSettings()
          .setPublisherId(publisherId);

      // Optional: You can also set user profile targeting parameters by updating the nativeAd.getUserSettings() object.
      // Please check the Smaato wiki for all available properties and further details.

      mCustomEventNativeListener = customEventNativeListener;
      mImpressionTracker = impressionTracker;
      mNativeClickHandler = nativeClickHandler;
    }

    @Override public void onAdClicked() {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          notifyAdClicked();
          return null;
        }
      }.execute();
    }

    @Override public void onLoggingImpression() {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          notifyAdImpressed();
          return null;
        }
      }.execute();
    }

    @Override public void onAdLoaded(final BannerNativeAd nativeAd) {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          setTitle(nativeAd.getDefaultTitle());
          setText(nativeAd.getDescriptionText());
          setMainImageUrl(nativeAd.getDefaultMainImageUrl());
          setIconImageUrl(nativeAd.getDefaultIconImageUrl());

          setCallToAction(nativeAd.getClickToActionText());
          setClickDestinationUrl(nativeAd.getClickToActionUrl());
          setStarRating((double) nativeAd.getRating());

          // TODO inside preCacheImages
          //mCustomEventNativeListener.onNativeAdLoaded(SmaatoForwardingNativeAd.this);

          final List<String> imageUrls = new ArrayList<>();
          final String mainImageUrl = getMainImageUrl();
          if (mainImageUrl != null) {
            imageUrls.add(getMainImageUrl());
          }
          final String iconUrl = getIconImageUrl();
          if (iconUrl != null) {
            imageUrls.add(getIconImageUrl());
          }

          // check this Image caching
          preCacheImages(context, imageUrls, new NativeImageHelper.ImageListener() {
            @Override public void onImagesCached() {
              mCustomEventNativeListener.onNativeAdLoaded(SmaatoForwardingNativeAd.this);
            }

            @Override public void onImagesFailedToCache(NativeErrorCode errorCode) {
              mCustomEventNativeListener.onNativeAdFailed(errorCode);
            }
          });

          return null;
        }
      }.execute();
    }

    @Override public void onError(final ErrorCode errorCode, String errorMessage) {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {

          if (errorCode == null || errorCode == ErrorCode.UNSPECIFIED) {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          } else if (errorCode == ErrorCode.NO_AD_AVAILABLE) {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
          } else if (errorCode == ErrorCode.NO_CONNECTION_ERROR) {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
          } else {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
          }

          return null;
        }
      }.execute();
    }

    @Override public void prepare(@NonNull final View view) {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          mImpressionTracker.addView(view, SmaatoForwardingNativeAd.this);
          nativeAd.registerViewForInteraction(view);
          mNativeClickHandler.setOnClickListener(view, SmaatoForwardingNativeAd.this);

          return null;
        }
      }.execute();
    }

    @Override public void clear(@NonNull final View view) {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          nativeAd.unRegisterView(view);
          mImpressionTracker.removeView(view);
          mNativeClickHandler.clearOnClickListener(view);
          return null;
        }
      }.execute();
    }

    @Override public void destroy() {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          mImpressionTracker.destroy();
          // TODO remove any listeners added
          //nativeAd.setAdListener();

          nativeAd.destroy();
          return null;
        }
      }.execute();
    }

    @Override public void recordImpression(@NonNull final View view) {
      try {
        notifyAdImpressed();
        nativeAd.fireViewedImpression(view);
      } catch (Exception m) {

        Debugger.showLog(new LogMessage(TAG,
            "Exception in Adapter Configuration. Please check inputs" + m.getMessage(),
            Debugger.Level_1, DebugCategory.DEBUG));
      }
    }

    @Override public void handleClick(@NonNull final View view) {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          notifyAdClicked();

          if (getClickDestinationUrl() != null) {
            ActivityIntentHandler.openBrowserApp(getClickDestinationUrl(), context);
          }

          nativeAd.recordClickImpression(view);

          Debugger.showLog(new LogMessage(TAG, "Smaato Native Ad clicked", Debugger.Level_1,
              DebugCategory.DEBUG));

          return null;
        }
      }.execute();
    }

    void loadAd() {
      new CrashReportTemplate<Void>() {
        @Override public Void process() {
          nativeAd.loadMediationNativeAd(SmaatoForwardingNativeAd.this);
          return null;
        }
      }.execute();
    }
  }
}