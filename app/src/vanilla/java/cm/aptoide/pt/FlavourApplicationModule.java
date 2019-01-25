package cm.aptoide.pt;

import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.preferences.AdultContentManager;
import cm.aptoide.pt.preferences.LocalPersistenceAdultContent;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.SecurePreferences;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class FlavourApplicationModule {

  private final AptoideApplication application;

  public FlavourApplicationModule(AptoideApplication application) {
    this.application = application;
  }

  @Singleton @Provides AdultContent provideAdultContent(
      LocalPersistenceAdultContent localAdultContent, AccountService accountService) {
    return new AdultContentManager(localAdultContent, accountService);
  }

  @Singleton @Provides LocalPersistenceAdultContent provideLocalAdultContent(
      Preferences preferences, @Named("secure") SecurePreferences securePreferences) {
    return new LocalPersistenceAdultContent(preferences, securePreferences);
  }

  @Singleton @Provides @Named("auto-update-store-name") String provideAutoUpdateStoreName() {
    return "v9";
  }

  @Singleton @Provides @Named("support-email") String providesSupportEmail() {
    return application.getString(R.string.aptoide_email);
  }

  @Singleton @Provides MoPubBannerAdExperiment providesMoPubBannerAdExperiment(
      ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubBannerAdExperiment(abTestManager, moPubAnalytics);
  }

  @Singleton @Provides MoPubNativeAdExperiment providesMoPubNativeAdExperiment(
      ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubNativeAdExperiment(abTestManager, moPubAnalytics);
  }

  @Singleton @Provides @Named("partnerID") String providePartnerID() {
    return "";
  }
}
