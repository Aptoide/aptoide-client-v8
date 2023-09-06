package cm.aptoide.pt;

import cm.aptoide.pt.home.bundles.apps.EskillsAppsBundleViewHolder;
import cm.aptoide.pt.install.DownloadService;
import cm.aptoide.pt.install.InstalledIntentService;
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

  void inject(DownloadService downloadService);

  void inject(InstalledIntentService installedIntentService);

  void inject(PullingContentService pullingContentService);

  void inject(EskillsAppsBundleViewHolder eskillsAppsBundleViewHolder);
}
