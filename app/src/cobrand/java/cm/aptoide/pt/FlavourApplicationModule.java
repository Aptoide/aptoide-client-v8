package cm.aptoide.pt;

import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.preferences.AdultContentManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

@Module public class FlavourApplicationModule {

  public FlavourApplicationModule() {
  }

  @Singleton @Provides AdultContent provideAdultContent() {
    return new AdultContentManager();
  }

  @Singleton @Provides @Named("autoUpdateStoreName") String provideAutoUpdateStoreName() {
    return BuildConfig.COBRAND_APPLICATION_ID_SUFFIX;
  }
}
