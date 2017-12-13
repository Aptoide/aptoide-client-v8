package cm.aptoide.pt;

import cm.aptoide.pt.ActivityModule;
import cm.aptoide.pt.view.ActivityScope;
import cm.aptoide.pt.view.FragmentComponent;
import cm.aptoide.pt.view.FragmentModule;
import cm.aptoide.pt.view.MainActivity;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  FragmentComponent plus(FragmentModule fragmentModule);

}
