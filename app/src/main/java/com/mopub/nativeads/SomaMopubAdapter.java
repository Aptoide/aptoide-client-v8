/*
 * Copyright (c) 2018 Smaato Inc. All rights reserved.
 */

package com.mopub.nativeads;

import android.content.Context;
import com.mopub.common.DataKeys;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.AdDimension;
import com.smaato.soma.AdDimensionHelper;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.AdSettings;
import com.smaato.soma.BannerStateListener;
import com.smaato.soma.BannerView;
import com.smaato.soma.BaseView;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.bannerutilities.constant.BannerStatus;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import java.util.Map;

/**
 * Adapter class for MoPub to Smaato mediation for banners.
 * For integration please follow the instructions from
 * <a href="https://wiki.smaato.com/display/IN/Android">Smaato Wiki</a> (section "Smaato as
 * secondary").
 * </p>
 * Tested with Smaato v9.0 and Mopub v5.3.0.
 *
 * @author Palani Soundararajan
 */
public class SomaMopubAdapter extends CustomEventBanner {

  private static final String TAG = "SomaMopubAdapter";
  private BannerView banner;

  @Override
  public void loadBanner(Context context, final CustomEventBannerListener customEventBannerListener,
      Map<String, Object> localExtras, Map<String, String> serverExtras) {
    try {
      if (banner == null) {
        banner = new BannerView(context);
        banner.addAdListener(new AdListenerInterface() {
          @Override
          public void onReceiveAd(AdDownloaderInterface arg0, final ReceivedBannerInterface arg1) {
            new CrashReportTemplate<Void>() {
              @Override public Void process() {
                if (arg1.getStatus() == BannerStatus.ERROR) {
                  printDebugLogs("NO_FILL", DebugCategory.DEBUG);
                  customEventBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
                } else {
                  printDebugLogs("Ad available", DebugCategory.DEBUG);
                  customEventBannerListener.onBannerLoaded(banner);
                }

                return null;
              }
            }.execute();
          }
        });

        banner.setBannerStateListener(new BannerStateListener() {
          @Override public void onWillOpenLandingPage(BaseView arg0) {
            new CrashReportTemplate<Void>() {
              @Override public Void process() throws Exception {
                printDebugLogs("Banner Clicked", DebugCategory.DEBUG);
                customEventBannerListener.onBannerClicked();
                return null;
              }
            }.execute();
          }

          @Override public void onWillCloseLandingPage(BaseView arg0) {
            new CrashReportTemplate<Void>() {
              @Override public Void process() throws Exception {
                printDebugLogs("Banner closed", DebugCategory.DEBUG);
                return null;
              }
            }.execute();
          }
        });
      }

      setAdIdsForAdSettings(serverExtras, banner.getAdSettings());
      int adHeight = (int) localExtras.get(DataKeys.AD_HEIGHT);
      int adWidth = (int) localExtras.get(DataKeys.AD_WIDTH);

      // support for default, medium rectangle, leaderboard, skyscraper and wide skyscraper banner format
      AdDimension adDimension = AdDimensionHelper.getAdDimensionForValues(adHeight, adWidth);

      if (adDimension != null) {
        banner.getAdSettings()
            .setAdDimension(adDimension);
      }

      // Optional: You can also set user profile targeting parameters by updating the banner.getUserSettings() object.
      // Please check the Smaato wiki for all available properties and further details.

      banner.asyncLoadNewBanner();
    } catch (RuntimeException e) {
      e.printStackTrace();
      printDebugLogs("Failed to load banner", DebugCategory.ERROR);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public void onInvalidate() {
    if (banner != null) {
      banner.destroy();
      banner = null;
    }
  }

  private void printDebugLogs(String str, DebugCategory debugCategory) {
    Debugger.showLog(new LogMessage(TAG, str, Debugger.Level_1, debugCategory));
  }

  private void setAdIdsForAdSettings(Map<String, String> serverExtras, AdSettings adSettings) {
    long publisherId = Long.parseLong(serverExtras.get("publisherId"));
    long adSpaceId = Long.parseLong(serverExtras.get("adSpaceId"));
    adSettings.setPublisherId(publisherId);
    adSettings.setAdspaceId(adSpaceId);
  }
}
