package cm.aptoide.pt;

import cm.aptoide.pt.install.InstallService;
import cm.aptoide.pt.install.InstalledIntentService;
import cm.aptoide.pt.notification.PullingContentService;
import cm.aptoide.pt.toolbox.ToolboxContentProvider;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.fragment.DescriptionFragment;
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

  void inject(DescriptionFragment descriptionFragment);
}
