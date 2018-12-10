package cm.aptoide.pt;

import cm.aptoide.accountmanager.AdultContent;
import cm.aptoide.pt.preferences.AdultContentManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module public class FlavourApplicationModule {

  public FlavourApplicationModule() {
  }

  @Singleton @Provides AdultContent provideAdultContent() {
    return new AdultContentManager();
  }

  @Singleton @Provides @Named("autoUpdateUrl") String provideAutoUpdateUrl() {
    return "http://imgs.aptoide.com/latest_version_v8.xml";
  }
}
