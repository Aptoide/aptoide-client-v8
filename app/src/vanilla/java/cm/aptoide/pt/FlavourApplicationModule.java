package cm.aptoide.pt;

import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubInterstitialAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.ads.WalletAdsOfferCardManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.WalletAdsOfferService;
import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.install.PackageRepository;
import cm.aptoide.pt.preferences.AdultContentManager;
import cm.aptoide.pt.preferences.LocalPersistenceAdultContent;
import cm.aptoide.pt.preferences.Preferences;
import cm.aptoide.pt.preferences.SecurePreferences;
import com.google.android.gms.common.GoogleApiAvailability;
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

  @Singleton @Provides LoginPreferences provideLoginPreferences() {
    return new LoginPreferences(application, GoogleApiAvailability.getInstance());
  }

  @Singleton @Provides MoPubBannerAdExperiment providesMoPubBannerAdExperiment(
      @Named("ab-test") ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubBannerAdExperiment(abTestManager, moPubAnalytics);
  }

  @Singleton @Provides MoPubNativeAdExperiment providesMoPubNativeAdExperiment(
      @Named("ab-test") ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubNativeAdExperiment(abTestManager, moPubAnalytics);
  }

  @Singleton @Provides MoPubInterstitialAdExperiment providesMoPubInterstitialAdExperiment(
      @Named("ab-test") ABTestManager abTestManager, MoPubAnalytics moPubAnalytics) {
    return new MoPubInterstitialAdExperiment(abTestManager, moPubAnalytics);
  }

  @Singleton @Provides @Named("partnerID") String providePartnerID() {
    return "";
  }

  @Singleton @Provides WalletAdsOfferManager providesWalletAdsOfferManager(
      WalletAdsOfferService walletAdsOfferService) {
    return new WalletAdsOfferManager(application.getApplicationContext()
        .getPackageManager(), walletAdsOfferService);
  }

  @Singleton @Provides WalletAdsOfferCardManager providesWalletAdsOfferCardManager(
      BlacklistManager blacklistManager, PackageRepository packageRepository) {
    return new WalletAdsOfferCardManager(blacklistManager, packageRepository);
  }

}
