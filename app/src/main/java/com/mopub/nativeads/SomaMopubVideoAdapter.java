/*
 * Copyright (c) 2018 Smaato Inc. All rights reserved.
 */

package com.mopub.nativeads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.AdSettings;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.video.VASTAdListener;
import com.smaato.soma.video.Video;
import java.util.Map;

/**
 * Adapter class for MoPub to Smaato mediation for video interstitials.
 * For integration please follow the instructions from
 * <a href="https://wiki.smaato.com/display/IN/Android">Smaato Wiki</a> (section "Smaato as
 * secondary").
 * </p>
 * Tested with Smaato v9.0 and Mopub v5.3.0.
 */
public class SomaMopubVideoAdapter extends CustomEventInterstitial implements VASTAdListener {

  private Video video;
  private CustomEventInterstitialListener customEventInterstitialListener;
  private Handler handler;

  @Override public void onReadyToShow() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            customEventInterstitialListener.onInterstitialLoaded();
          }
        });
        return null;
      }
    }.execute();
  }

  @Override public void onWillShow() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            customEventInterstitialListener.onInterstitialShown();
          }
        });
        return null;
      }
    }.execute();
  }

  @Override public void onWillOpenLandingPage() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            customEventInterstitialListener.onInterstitialClicked();
          }
        });
        return null;
      }
    }.execute();
  }

  @Override public void onWillClose() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            customEventInterstitialListener.onInterstitialDismissed();
          }
        });
        return null;
      }
    }.execute();
  }

  @Override public void onFailedToLoadAd() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
          }
        });
        return null;
      }
    }.execute();
  }

  @Override protected void loadInterstitial(final Context context,
      final CustomEventInterstitialListener customEventInterstitialListener,
      Map<String, Object> localExtras, final Map<String, String> serverExtras) {
    this.customEventInterstitialListener = customEventInterstitialListener;
    handler = new Handler(Looper.getMainLooper());

    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        video = new Video(context);
        video.setVastAdListener(SomaMopubVideoAdapter.this);
        setAdIdsForAdSettings(serverExtras, video.getAdSettings());

        // Optional: You can also set user profile targeting parameters by updating the video.getUserSettings() object.
        // Please check the Smaato wiki for all available properties and further details.

        video.asyncLoadNewBanner();

        return null;
      }
    }.execute();
  }

  @Override protected void showInterstitial() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            if (video.isVideoPlayable()) {
              video.show();
            }
          }
        });
        return null;
      }
    }.execute();
  }

  @Override protected void onInvalidate() {
    if (video != null) {
      video.destroy();
      video = null;
    }
  }

  private void setAdIdsForAdSettings(Map<String, String> serverExtras, AdSettings adSettings) {
    long publisherId = Long.parseLong(serverExtras.get("publisherId"));
    long adSpaceId = Long.parseLong(serverExtras.get("adSpaceId"));
    adSettings.setPublisherId(publisherId);
    adSettings.setAdspaceId(adSpaceId);
  }
}