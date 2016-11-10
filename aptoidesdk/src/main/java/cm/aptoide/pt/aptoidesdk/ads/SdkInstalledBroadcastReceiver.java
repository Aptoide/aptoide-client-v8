package cm.aptoide.pt.aptoidesdk.ads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import rx.android.schedulers.AndroidSchedulers;

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

    Ad ad = getAndRemoveStoredAd(packageName);
    if (ad != null) {
      ReferrerUtils.broadcastReferrer(packageName, ad.referrer);
      ReferrerUtils.knockCpi(ad);
    } else {
      GetAdsRequest.ofSecondInstall(packageName,
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              RxAptoide.getContext())
              .getAptoideClientUUID(),
          DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(RxAptoide.getContext()),
          RxAptoide.getOemid())
          .observe()
          .map(getAdsResponse -> Ad.from(getAdsResponse.getAds().get(0)))
          .observeOn(AndroidSchedulers.mainThread())
          .doOnNext(
              minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true))
          .subscribe(ad1 -> {
          }, throwable -> Logger.e(TAG, throwable));
    }
  }

  private Ad getAndRemoveStoredAd(String packageName) {
    return StoredAdsManager.getInstance(RxAptoide.getContext()).removeAd(packageName);
  }
}
