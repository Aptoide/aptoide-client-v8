package cm.aptoide.pt.v8engine.usagestatsmanager.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoredMinimalAdAccessor;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.ads.AdsRepository;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.usagestatsmanager.UsageStatsManager;
import cm.aptoide.pt.v8engine.usagestatsmanager.utils.CollectionUtils;
import cm.aptoide.pt.v8engine.util.referrer.ReferrerUtils;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OpenedAppsReceiver extends WakelockReceiver {

  private static final String TAG = OpenedAppsReceiver.class.getSimpleName();

  @Override protected void wakeLockReceive(Context context, Intent intent) {
    Logger.d(TAG, "OpenedAppsReceiver Alarm Called.");

    broadcastOpenendAppsReferrer(context);
  }

  private void broadcastOpenendAppsReferrer(Context context) {
    UsageStatsManager usageStatsManager = new UsageStatsManager(context).loadTimestamp();
    List<UsageStatsManager.UsageEvent> usageEventList = usageStatsManager.refresh();
    broadcastAndRemoveReferrer(context,
        CollectionUtils.map(usageEventList, UsageStatsManager.UsageEvent::getPackageName));
  }

  private void broadcastAndRemoveReferrer(Context context, List<String> map) {
    // TODO: 02-06-2017 neuro
  }

  private void checkAndBroadcastReferrer(String packageName) {
    StoredMinimalAdAccessor storedMinimalAdAccessor =
        AccessorFactory.getAccessorFor(StoredMinimalAd.class);
    Subscription unManagedSubscription = storedMinimalAdAccessor.get(packageName)
        .flatMapCompletable(storeMinimalAd -> {
          if (storeMinimalAd != null) {
            return knockCpi(packageName, storedMinimalAdAccessor, storeMinimalAd);
          } else {
            return extractReferrer(packageName);
          }
        })
        .subscribe(__ -> { /* do nothing */ }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  private Completable knockCpi(String packageName, StoredMinimalAdAccessor storedMinimalAdAccessor,
      StoredMinimalAd storeMinimalAd) {
    return Completable.fromCallable(() -> {
      ReferrerUtils.broadcastReferrer(packageName, storeMinimalAd.getReferrer());
      DataproviderUtils.AdNetworksUtils.knockCpi(storeMinimalAd);
      storedMinimalAdAccessor.remove(storeMinimalAd);
      return null;
    });
  }

  @NonNull private Completable extractReferrer(String packageName) {
    final AptoideAccountManager accountManager =
        ((V8Engine) getApplicationContext()).getAccountManager();
    OkHttpClient httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();
    Converter.Factory converterFactory = WebService.getDefaultConverter();
    AdsRepository adsRepository =
        new AdsRepository(((V8Engine) getApplicationContext()).getIdsRepository(), accountManager,
            httpClient, converterFactory, V8Engine.getQManager());

    return adsRepository.getAdsFromSecondInstall(packageName)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(minimalAd -> ReferrerUtils.extractReferrer(minimalAd, ReferrerUtils.RETRIES, true,
            adsRepository, httpClient, converterFactory))
        .toCompletable();
  }
}