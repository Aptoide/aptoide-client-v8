package cm.aptoide.pt.view;

import dagger.Subcomponent;

/**
 * Created by jose_messejana on 07-12-2017.
 */

@ActivityScopeTest @Subcomponent(modules = { ActivityModuleTest.class })
public interface ActivityComponentTest extends ActivityComponent{

  void inject(MainActivity activity);

  FragmentComponentTest plus(FragmentModuleTest fragmentModule);
}
