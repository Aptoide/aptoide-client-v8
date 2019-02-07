package cm.aptoide.pt.view;

import cm.aptoide.pt.FlavourActivityModule;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.analytics.view.AnalyticsActivity;
import cm.aptoide.pt.app.view.donations.view.DonateDialogFragment;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.promotions.ClaimPromotionDialogFragment;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.settings.SettingsFragment;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class, FlavourActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  void inject(ActivityResultNavigator activityResultNavigator);

  void inject(AnalyticsActivity analyticsActivity);

  void inject(BottomNavigationActivity bottomNavigationActivity);

  FragmentComponent plus(FragmentModule fragmentModule,
      FlavourFragmentModule flavourFragmentModule);

  void inject(DialogUtils dialogUtils);

  void inject(DonateDialogFragment donateDialogFragment);

  void inject(ClaimPromotionDialogFragment claimPromotionDialogFragment);

  void inject(BaseActivity baseActivity);

  void inject(SettingsFragment settingsFragment);

  void inject(StoreTabGridRecyclerFragment storeTabGridRecyclerFragment);
}