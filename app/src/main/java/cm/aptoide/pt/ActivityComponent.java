package cm.aptoide.pt;

import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.ActivityScope;
import cm.aptoide.pt.view.MainActivity;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  void inject(ActivityResultNavigator activityResultNavigator);

  FragmentComponent plus(FragmentModule fragmentModule);

}
