package cm.aptoide.pt;

import cm.aptoide.pt.install.InstallService;
import cm.aptoide.pt.toolbox.ToolboxContentProvider;
import cm.aptoide.pt.view.ActivityComponent;
import cm.aptoide.pt.view.ActivityModule;
import cm.aptoide.pt.view.ApplicationTestScope;
import dagger.Component;

/**
 * Created by jose_messejana on 10-11-2017.
 */

@ApplicationTestScope @Component(modules = { ApplicationModuleTest.class }) public interface ApplicationComponentTest extends ApplicationComponent {

  ActivityComponent plus(ActivityModule activityModule);

  void inject(AptoideApplication application);

  void inject(ToolboxContentProvider toolboxContentProvider);

  void inject(InstallService installService);
}
