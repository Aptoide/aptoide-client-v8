package cm.aptoide.pt.ads;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import rx.Scheduler;

public class AdsUserPropertyManager {

  private static final String WALLET_PACKAGE = "com.appcoins.wallet";

  private final MoPubAdsManager moPubAdsManager;
  private final InstalledRepository installedRepository;
  private final MoPubAnalytics moPubAnalytics;
  private final CrashReport crashReport;
  private final Scheduler ioScheduler;

  public AdsUserPropertyManager(MoPubAdsManager moPubAdsManager,
      InstalledRepository installedRepository, MoPubAnalytics moPubAnalytics,
      CrashReport crashReport, Scheduler ioScheduler) {
    this.moPubAdsManager = moPubAdsManager;
    this.installedRepository = installedRepository;
    this.crashReport = crashReport;
    this.moPubAnalytics = moPubAnalytics;
    this.ioScheduler = ioScheduler;
  }

  public void start() {
    installedRepository.isInstalled(WALLET_PACKAGE)
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
}
