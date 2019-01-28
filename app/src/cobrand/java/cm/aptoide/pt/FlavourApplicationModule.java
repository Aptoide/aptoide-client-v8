package cm.aptoide.pt;

import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import cm.aptoide.pt.preferences.AdultContentManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class FlavourApplicationModule {

  private final AptoideApplication application;

  public FlavourApplicationModule(AptoideApplication application) {
    this.application = application;
  }

  @Singleton @Provides AdultContent provideAdultContent() {
    return new AdultContentManager();
  }

  @Singleton @Provides @Named("auto-update-store-name") String provideAutoUpdateStoreName() {
    return BuildConfig.COBRAND_APPLICATION_ID_SUFFIX;
  }

  @Singleton @Provides @Named("support-email") String providesSupportEmail() {
    return "n/a";
  }

  @Singleton @Provides @Named("partnerID") String providePartnerID() {
    return BuildConfig.COBRAND_OEMID;
  }

  @Singleton @Provides MoPubBannerAdExperiment providesMoPubBannerAdExperiment() {
    return new MoPubBannerAdExperiment();
  }

  @Singleton @Provides MoPubNativeAdExperiment providesMoPubNativeAdExperiment() {
    return new MoPubNativeAdExperiment();
  }
}
