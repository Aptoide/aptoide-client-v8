package cm.aptoide.pt;

import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.MoPubAnalytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.install.InstallService;
import cm.aptoide.pt.install.InstalledIntentService;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.notification.PullingContentService;
import cm.aptoide.pt.toolbox.ToolboxContentProvider;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { ApplicationModule.class, FlavourApplicationModule.class })
public interface ApplicationComponent {

  ActivityComponent plus(ActivityModule activityModule,
      FlavourActivityModule flavourActivityModule);

  void inject(AptoideApplication application);

  void inject(NotificationApplicationView notificationApplicationView);

  void inject(ToolboxContentProvider toolboxContentProvider);

  void inject(InstallService installService);

  void inject(InstalledIntentService installedIntentService);

  void inject(PullingContentService pullingContentService);

  MoPubAdsManager getMopubAdsManager();

  InstalledRepository getIdsRepository();

  MoPubAnalytics getMoPubAnalytics();

  CrashReport getCrashReport();

}
