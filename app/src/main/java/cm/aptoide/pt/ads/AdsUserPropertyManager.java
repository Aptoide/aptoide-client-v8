package cm.aptoide.pt.ads;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import rx.Completable;
import rx.Scheduler;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class AdsUserPropertyManager {
  private final MoPubAdsManager moPubAdsManager;
  private final AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  private final MoPubAnalytics moPubAnalytics;
  private final CrashReport crashReport;
  private final Scheduler ioScheduler;

  public AdsUserPropertyManager(MoPubAdsManager moPubAdsManager,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository, MoPubAnalytics moPubAnalytics,
      CrashReport crashReport, Scheduler ioScheduler) {
    this.moPubAdsManager = moPubAdsManager;
    this.aptoideInstalledAppsRepository = aptoideInstalledAppsRepository;
    this.crashReport = crashReport;
    this.moPubAnalytics = moPubAnalytics;
    this.ioScheduler = ioScheduler;
  }

  public void start() {
    aptoideInstalledAppsRepository.isInstalled(APPCOINS_WALLET_PACKAGE_NAME)
        .observeOn(ioScheduler)
        .distinctUntilChanged()
        .flatMap(__ -> moPubAdsManager.getAdsVisibilityStatus()
            .toObservable())
        .doOnNext(moPubAnalytics::setAdsVisibilityUserProperty)
        .takeWhile(offerResponseStatus -> offerResponseStatus
            != WalletAdsOfferManager.OfferResponseStatus.NO_ADS)
        .subscribe(created -> {
        }, error -> crashReport.log(error));
  }

  public Completable setUp(String id) {
    return aptoideInstalledAppsRepository.isInstalled(APPCOINS_WALLET_PACKAGE_NAME)
        .first()
        .observeOn(ioScheduler)
        .distinctUntilChanged()
        .flatMapSingle(__ -> moPubAdsManager.getAdsVisibilityStatus())
        .doOnNext(offerResponseStatus -> {
          moPubAnalytics.setAdsVisibilityUserProperty(offerResponseStatus);
          moPubAnalytics.setUserId(id);
        })
        .toCompletable();
  }
}
