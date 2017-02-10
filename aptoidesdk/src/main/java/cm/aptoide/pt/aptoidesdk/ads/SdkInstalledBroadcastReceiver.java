package cm.aptoide.pt.aptoidesdk.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by neuro on 24-10-2016.
 */
public class SdkInstalledBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = SdkInstalledBroadcastReceiver.class.getSimpleName();

  @Override public void onReceive(Context context, Intent intent) {

    String action = intent.getAction();
    String packageName = intent.getData().getEncodedSchemeSpecificPart();

    Logger.d(TAG,
        String.format("SdkInstalledBroadcastReceiver invoked with action %s for packageName %s.",
            action, packageName));

    switch (action) {
      case Intent.ACTION_PACKAGE_ADDED:
        checkAndBroadcastReferrer(packageName);
        break;
      case Intent.ACTION_PACKAGE_REPLACED:
        // Not listening yet
        break;
      case Intent.ACTION_PACKAGE_REMOVED:
        // Not listening yet
        break;
    }
  }

  private void checkAndBroadcastReferrer(String packageName) {

    AptoideAd ad = getAndRemoveStoredAd(packageName);
    if (ad != null) {
      ReferrerUtils.broadcastReferrer(packageName, ad.referrer);
      ReferrerUtils.knockCpi(ad);
    }
  }

  private AptoideAd getAndRemoveStoredAd(String packageName) {
    return StoredAdsManager.getInstance(RxAptoide.getContext()).removeAd(packageName);
  }
}
