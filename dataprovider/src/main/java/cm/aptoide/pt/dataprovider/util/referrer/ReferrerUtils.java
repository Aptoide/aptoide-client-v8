/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/06/2016.
 */

package cm.aptoide.pt.dataprovider.util.referrer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.RegisterAdRefererRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 08-10-2015.
 */
public class ReferrerUtils {

	public static final int TIME_OUT = 5;
	public static final ReferrersMap excludedNetworks = new ReferrersMap();
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public static void extractReferrer(GetAdsResponse.Ad ad, final int retries, boolean broadcastReferrer) {

		String packageName = ad.getData().getPackageName();
		long networkId = ad.getPartner().getInfo().getId();
		String click_url = ad.getPartner().getData().getClickUrl();

		if (!AptoideUtils.ThreadU.isUiThread()) {
			throw new RuntimeException("ExtractReferrer must be run on UI thread!");
		}

		final Context context = DataProvider.getContext();

		try {
			Logger.d("ExtractReferrer", "Called for: " + click_url + " with packageName " + packageName);

			final String[] internalClickUrl = {click_url};
			final SimpleTimedFuture<String> clickUrlFuture = new SimpleTimedFuture<>();

			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			WindowManager.LayoutParams params;
			params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager
                    .LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);

			params.gravity = Gravity.TOP | Gravity.LEFT;
			params.x = 0;
			params.y = 0;
			params.width = 0;
			params.height = 0;

			LinearLayout view = new LinearLayout(context);
			view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));

			AptoideUtils.ThreadU.runOnIoThread(() -> {
				internalClickUrl[0] = DataproviderUtils.AdNetworksUtils.parseMacros(click_url);
				clickUrlFuture.set(internalClickUrl[0]);
				Logger.d("ExtractReferrer", "Parsed click_url: " + internalClickUrl[0]);
			});
			clickUrlFuture.get();
			WebView wv = new WebView(context);
			wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout
                    .LayoutParams.MATCH_PARENT));
			view.addView(wv);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.setWebViewClient(new WebViewClient() {

				Future<Void> future;

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String clickUrl) {

					if (clickUrl.startsWith("market://") || clickUrl.startsWith("https://play.google.com") ||
							clickUrl.startsWith("http://play.google.com")) {
						Logger.d("ExtractReferrer", "Clickurl landed on market");
						final String referrer = getReferrer(clickUrl);
//                        if (simpleFuture != null) {
//                            simpleFuture.set(referrer);
//                        }
						Logger.d("ExtractReferrer", "Referrer successfully extracted");

						if (broadcastReferrer) {
							broadcastReferrer(packageName, referrer);
						} else {
							@Cleanup Realm realm = Database.get();
							Database.RollbackQ.setReferrer(packageName, referrer, realm);
						}

						future.cancel(false);
						postponeReferrerExtraction(0, true);

						return true;
					}

					return false;
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);

					if (future == null) {
						future = postponeReferrerExtraction(TIME_OUT, retries);
					}
				}

				private ScheduledFuture<Void> postponeReferrerExtraction(int delta, int retries) {
					return postponeReferrerExtraction(delta, false, retries);
				}

				private ScheduledFuture<Void> postponeReferrerExtraction(int delta, boolean success) {
					return postponeReferrerExtraction(delta, success, 0);
				}

				private ScheduledFuture<Void> postponeReferrerExtraction(int delta, final boolean success, final int
                        retries) {
					Logger.d("ExtractReferrer", "Referrer postponed " + delta + " seconds.");

					Callable<Void> callable = () -> {
						Logger.d("ExtractReferrer", "Sending RegisterAdRefererRequest with value " + success);

						RegisterAdRefererRequest.of(ad, success).execute();

						Log.d("ExtractReferrer", "Retries left: " + retries);

						if (!success) {
							excludedNetworks.add(packageName, networkId);

							try {

								if (retries > 0) {
									GetAdsRequest.ofSecondTry(packageName)
											.execute(getAdsResponse -> extractReferrer(getAdsResponse.getAds()
													.get(0), retries - 1, broadcastReferrer));
								} else {
									// A lista de excluded networks deve ser limpa a cada "ronda"
									excludedNetworks.remove(packageName);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							// A lista de excluded networks deve ser limpa a cada "ronda"
							excludedNetworks.remove(packageName);
						}

						return null;
					};

					return executorService.schedule(callable, delta, TimeUnit.SECONDS);
				}
			});

			wv.loadUrl(internalClickUrl[0]);

			windowManager.addView(view, params);
		} catch (Exception e) {
			// TODO: 09-06-2016 neuro
//            Crashlytics.logException(e);
		}
	}

	private static String getReferrer(String uri) {
		List<NameValuePair> params = URLEncodedUtils.parse(URI.create(uri), "UTF-8");

		String referrer = null;
		for (NameValuePair param : params) {

			if (param.getName().equals("referrer")) {
				referrer = param.getValue();
			}
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
		Logger.d("InstalledBroadcastReceiver", "Sent broadcast to " + packageName + " with referrer " + referrer);
	}
}
