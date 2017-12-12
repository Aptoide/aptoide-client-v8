package cm.aptoide.pt;

import cm.aptoide.pt.install.InstallService;
import cm.aptoide.pt.toolbox.ToolboxContentProvider;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { ApplicationModule.class }) public interface ApplicationComponent {

  ActivityComponent plus(ActivityModule activityModule);

  void inject(AptoideApplication application);

  void inject(ToolboxContentProvider toolboxContentProvider);

  void inject(InstallService installService);
}
