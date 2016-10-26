/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.aptoidesdk.ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.util.referrer.SimpleTimedFuture;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.RegisterAdRefererRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import rx.android.schedulers.AndroidSchedulers;

import static cm.aptoide.pt.dataprovider.util.DataproviderUtils.knock;

/**
 * Created by neuro on 20-06-2016.
 */
class ReferrerUtils extends cm.aptoide.pt.dataprovider.util.referrer.ReferrerUtils {

  private static final String TAG = ReferrerUtils.class.getSimpleName();

  static void extractReferrer(Ad ad, final int retries, boolean broadcastReferrer) {

    String packageName = ad.data.packageName;
    long networkId = ad.network.id;
    String clickUrl = ad.network.clickUrl;

    if (clickUrl == null) {
      Logger.d("ExtractReferrer", "No click_url for packageName " + packageName);
      return;
    }

    if (!AptoideUtils.ThreadU.isUiThread()) {
      throw new RuntimeException("ExtractReferrer must be run on UI thread!");
    }

    final Context context = DataProvider.getContext();

    try {
      Logger.d("ExtractReferrer", "Called for: " + clickUrl + " with packageName " + packageName);

      final String[] internalClickUrl = { clickUrl };
      final SimpleTimedFuture<String> clickUrlFuture = new SimpleTimedFuture<>();

      WindowManager windowManager =
          (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      WindowManager.LayoutParams params;
      params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
          WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);

      params.gravity = Gravity.TOP | Gravity.LEFT;
      params.x = 0;
      params.y = 0;
      params.width = 0;
      params.height = 0;

      LinearLayout view = new LinearLayout(context);
      view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
          RelativeLayout.LayoutParams.MATCH_PARENT));

      AptoideUtils.ThreadU.runOnIoThread(() -> {
        internalClickUrl[0] = DataproviderUtils.AdNetworksUtils.parseMacros(clickUrl);
        clickUrlFuture.set(internalClickUrl[0]);
        Logger.d("ExtractReferrer", "Parsed clickUrl: " + internalClickUrl[0]);
      });
      clickUrlFuture.get();
      WebView wv = new WebView(context);
      wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.MATCH_PARENT));
      view.addView(wv);
      wv.getSettings().setJavaScriptEnabled(true);
      wv.setWebViewClient(new WebViewClient() {

        Future<Void> future;

        @Override public boolean shouldOverrideUrlLoading(WebView view, String clickUrl) {

          Logger.d("ExtractReferrer", "ClickUrl redirect: " + clickUrl);

          if (clickUrl.startsWith("market://") || clickUrl.startsWith("https://play.google.com") ||
              clickUrl.startsWith("http://play.google.com")) {
            Logger.d("ExtractReferrer", "Clickurl landed on market");
            final String referrer = getReferrer(clickUrl);
            if (!TextUtils.isEmpty(referrer)) {
              Logger.d("ExtractReferrer", "Referrer successfully extracted");

              if (broadcastReferrer) {
                broadcastReferrer(packageName, referrer);
              } else {
                //@Cleanup Realm realm = DeprecatedDatabase.get();
                //DeprecatedDatabase.save(
                //    new StoredMinimalAd(packageName, referrer, minimalAd.getCpiUrl(),
                //        minimalAd.getAdId()), realm);

                saveAd(ad);
              }

              future.cancel(false);
              postponeReferrerExtraction(ad, 0, true);
            }
          }

          return false;
        }

        private void saveAd(Ad ad) {
          StoredAdsManager.getInstance().addAd(ad);
        }

        @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
          super.onPageStarted(view, url, favicon);

          Logger.d("ExtractReferrer", "Openened clickUrl: " + url);

          if (future == null) {
            future = postponeReferrerExtraction(ad, TIME_OUT, retries);
          }
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(Ad ad, int delta, int retries) {
          return postponeReferrerExtraction(ad, delta, false, retries);
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(Ad ad, int delta,
            boolean success) {
          return postponeReferrerExtraction(ad, delta, success, 0);
        }

        private ScheduledFuture<Void> postponeReferrerExtraction(Ad ad, int delta,
            final boolean success, final int retries) {
          Logger.d("ExtractReferrer", "Referrer postponed " + delta + " seconds.");

          Callable<Void> callable = () -> {
            Logger.d("ExtractReferrer", "Sending RegisterAdRefererRequest with value " + success);

            RegisterAdRefererRequest.of(ad.id, ad.data.appId, ad.network.clickUrl, success)
                .execute();

            Logger.d("ExtractReferrer", "Retries left: " + retries);

            if (!success) {
              excludedNetworks.add(packageName, networkId);

              try {

                if (retries > 0) {
                  GetAdsRequest.ofSecondTry(packageName)
                      .observe()
                      .filter((getAdsResponse1) -> {
                        Boolean hasAds = hasAds(getAdsResponse1);
                        if (!hasAds) {
                          clearExcludedNetworks(packageName);
                        }
                        return hasAds;
                      })
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(
                          getAdsResponse -> extractReferrer(Ad.from(getAdsResponse.getAds().get(0)),
                              retries - 1, broadcastReferrer), CrashReports::logException);
                } else {
                  // A lista de excluded networks deve ser limpa a cada "ronda"
                  clearExcludedNetworks(packageName);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
              // TODO: 28-07-2016 Baikova Failed to extract referrer.
            } else {
              // A lista de excluded networks deve ser limpa a cada "ronda"
              // TODO: 28-07-2016 Baikova referrer successfully extracted.
              clearExcludedNetworks(packageName);
            }

            return null;
          };

          return executorService.schedule(callable, delta, TimeUnit.SECONDS);
        }
      });

      wv.loadUrl(internalClickUrl[0]);

      // TODO: 28-07-2016 Baikova Opened click_url

      windowManager.addView(view, params);
    } catch (Exception e) {
      CrashReports.logException(e);
    }
  }

  private static List<Long> clearExcludedNetworks(String packageName) {
    return excludedNetworks.remove(packageName);
  }

  private static Boolean hasAds(GetAdsResponse getAdsResponse) {
    return getAdsResponse != null
        && getAdsResponse.getAds() != null
        && getAdsResponse.getAds().size() > 0
        && getAdsResponse.getAds().get(0) != null;
  }

  private static String getReferrer(String uriAsString) {
    Uri uri = Uri.parse(uriAsString);
    String referrer = uri.getQueryParameter("referrer");
    if (!TextUtils.isEmpty(referrer)) {
      Logger.v(TAG, "Found referrer: " + referrer);
    } else {
      Logger.v(TAG, "Didn't find any referrer: " + uriAsString);
    }
    return referrer;
  }

  private static void broadcastReferrer(String packageName, String referrer) {
    Intent i = new Intent("com.android.vending.INSTALL_REFERRER");
    i.setPackage(packageName);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
      i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
    }
    i.putExtra("referrer", referrer);
    DataProvider.getContext().sendBroadcast(i);
    Logger.d(TAG, "Sent broadcast to " + packageName + " with referrer " + referrer);
    // TODO: 28-07-2016 Baikova referrer broadcasted.
  }

  public static void knockCpc(Ad minimalAd) {
    // TODO: 28-07-2016 Baikova clicked on ad.
    knock(minimalAd.clicks.cpcUrl);
  }

  public static void knockCpd(Ad minimalAd) {
    // TODO: 28-07-2016 Baikova clicked on download button.
    knock(minimalAd.clicks.cpdUrl);
  }

  public static void knockCpi(Ad minimalAd) {
    // TODO: 28-07-2016 Baikova ad installed.
    knock(minimalAd.clicks.cpiUrl);
  }

  // FIXME: 29-07-2016 neuro so wrong...
  public static void knockImpression(Ad ad) {
    if (isImpressionUrlPresent(ad)) {
      knock(ad.network.impressionUrl);
    }
  }

  private static boolean isImpressionUrlPresent(Ad ad) {
    return ad != null && ad.network != null && ad.network.impressionUrl != null;
  }
}
