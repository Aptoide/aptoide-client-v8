package cm.aptoide.pt.view;

import cm.aptoide.pt.navigator.ActivityResultNavigator;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  void inject(ActivityResultNavigator activityResultNavigator);

  FragmentComponent plus(FragmentModule fragmentModule);
}
