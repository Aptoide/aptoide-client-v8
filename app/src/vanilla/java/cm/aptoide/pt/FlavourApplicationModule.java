package cm.aptoide.pt;

import dagger.Module;

@Module public class FlavourApplicationModule {

  public FlavourApplicationModule() {
  }

  @Singleton @Provides AdultContent provideAdultContent(
      LocalPersistenceAdultContent localAdultContent, AccountService accountService) {
    return new AdultContentManager(localAdultContent, accountService);
  }

  @Singleton @Provides LocalPersistenceAdultContent provideLocalAdultContent(
      Preferences preferences, @Named("secure") SecurePreferences securePreferences) {
    return new LocalPersistenceAdultContent(preferences, securePreferences);
  }
}
