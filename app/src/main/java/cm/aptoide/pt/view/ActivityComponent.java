package cm.aptoide.pt.view;

import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  FragmentComponent plus(FragmentModule fragmentModule);
  FragmentComponentTest plus(FragmentModuleTest fragmentModuleTest);
}
