package cm.aptoide.pt;

import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.ads.WalletAdsOfferCardManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
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

  @Singleton @Provides @Named("partnerID") String providePartnerID() {
    return "";
  }

  @Singleton @Provides WalletAdsOfferManager providesWalletAdsOfferManager() {
    return new WalletAdsOfferManager(application.getApplicationContext()
        .getPackageManager());
  }

  @Singleton @Provides WalletAdsOfferCardManager providesWalletAdsOfferCardManager(
      BlacklistManager blacklistManager, PackageRepository packageRepository) {
    return new WalletAdsOfferCardManager(blacklistManager, packageRepository);
  }
}
