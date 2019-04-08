package cm.aptoide.pt;

import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.MoPubAdsService;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstalledRepository;
import dagger.Provides;
import dagger.producers.ProducerModule;
import dagger.producers.Produces;
import dagger.producers.Production;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import rx.schedulers.Schedulers;

@ProducerModule public class AsyncApplicationModule {

  @Provides @Production static Executor executor() {
    return Executors.newCachedThreadPool();
  }

  @Produces MoPubAdsService providesMoPubAdsService(MoPubAdsManager moPubAdsManager,
      InstalledRepository installedRepository, MoPubAnalytics moPubAnalytics,
      CrashReport crashReport) {
    return new MoPubAdsService(moPubAdsManager, installedRepository, moPubAnalytics, crashReport,
        Schedulers.io());
  }
}
