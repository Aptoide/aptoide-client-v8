package cm.aptoide.pt.view;

import cm.aptoide.pt.ActivityModule;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  FragmentComponent plus(FragmentModule fragmentModule);

}
