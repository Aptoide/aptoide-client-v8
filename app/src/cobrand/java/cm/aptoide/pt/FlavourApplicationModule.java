package cm.aptoide.pt;

import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.ads.MoPubConsentDialogManager;
import cm.aptoide.pt.ads.MoPubConsentDialogView;
import cm.aptoide.pt.ads.MoPubConsentManager;
import cm.aptoide.pt.ads.WalletAdsOfferCardManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
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

  @Singleton @Provides LoginPreferences provideLoginPreferences() {
    return new LoginPreferences();
  }


  @Singleton @Provides WalletAdsOfferManager providesWalletAdsOfferManager() {
    return new WalletAdsOfferManager();
  }

  @Singleton @Provides WalletAdsOfferCardManager providesWalletAdsOfferCardManager() {
    return new WalletAdsOfferCardManager();
  }

  @Singleton @Provides MoPubConsentManager providesMoPubConsentManager() {
    return new MoPubConsentManager();
  }

  @Singleton @Provides @Named("mopub-consent-dialog-view")
  MoPubConsentDialogView providesMoPubConsentDialogView(MoPubConsentManager moPubConsentManager) {
    return moPubConsentManager;
  }

  @Singleton @Provides @Named("mopub-consent-dialog-manager")
  MoPubConsentDialogManager providesMoPubConsentDialogManager(
      MoPubConsentManager moPubConsentManager) {
    return moPubConsentManager;
  }
}
