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
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import com.smaato.soma.interstitial.Interstitial;
import com.smaato.soma.interstitial.InterstitialAdListener;
import java.util.Map;

/**
 * Adapter class for MoPub to Smaato mediation for image and rich media interstitials.
 * For integration please follow the instructions from
 * <a href="https://wiki.smaato.com/display/IN/Android">Smaato Wiki</a> (section "Smaato as
 * secondary").
 * </p>
 * Tested with Smaato v9.0 and Mopub v5.3.0.
 *
 * @author Chouaieb Nabil
 */
public class SomaMopubAdapterInterstitial extends CustomEventInterstitial
    implements InterstitialAdListener {

  private static final String TAG = "SomaMopubAdapterInterstitial";
  private Interstitial interstitial;
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
            printDebugLogs("onWillShow ", DebugCategory.ERROR);
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

  @Override protected void loadInterstitial(Context context,
      final CustomEventInterstitialListener customEventInterstitialListener,
      Map<String, Object> localExtras, final Map<String, String> serverExtras) {
    handler = new Handler(Looper.getMainLooper());
    this.customEventInterstitialListener = customEventInterstitialListener;

    if (interstitial == null) {
      interstitial = new Interstitial(context);
      interstitial.setInterstitialAdListener(this);
    }

    // Optional: You can also set user profile targeting parameters by updating the interstitial.getUserSettings() object.
    // Please check the Smaato wiki for all available properties and further details.

    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        setAdIdsForAdSettings(serverExtras, interstitial.getAdSettings());
        interstitial.asyncLoadNewBanner();

        return null;
      }
    }.execute();
  }

  @Override protected void showInterstitial() {
    new CrashReportTemplate<Void>() {
      @Override public Void process() {
        handler.post(new Runnable() {
          @Override public void run() {
            if (interstitial.isInterstitialReady()) {
              interstitial.show();
            }
          }
        });

        return null;
      }
    }.execute();
  }

  @Override protected void onInvalidate() {
    if (interstitial != null) {
      interstitial.destroy();
      interstitial = null;
    }
  }

  private void setAdIdsForAdSettings(Map<String, String> serverExtras, AdSettings adSettings) {
    long publisherId = Long.parseLong(serverExtras.get("publisherId"));
    long adSpaceId = Long.parseLong(serverExtras.get("adSpaceId"));
    adSettings.setPublisherId(publisherId);
    adSettings.setAdspaceId(adSpaceId);
  }

  private void printDebugLogs(String str, DebugCategory debugCategory) {
    Debugger.showLog(new LogMessage(TAG, str, Debugger.Level_1, debugCategory));
  }
}