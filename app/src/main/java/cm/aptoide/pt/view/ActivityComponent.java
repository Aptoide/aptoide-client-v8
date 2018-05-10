package cm.aptoide.pt.view;

import cm.aptoide.pt.analytics.view.AnalyticsActivity;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.dialog.DialogUtils;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  void inject(ActivityResultNavigator activityResultNavigator);

  void inject(AnalyticsActivity analyticsActivity);

  void inject(BottomNavigationActivity bottomNavigationActivity);

  FragmentComponent plus(FragmentModule fragmentModule);

  void inject(DialogUtils dialogUtils);
}
