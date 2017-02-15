package cm.aptoide.pt.v8engine.fragment.implementations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.util.UserCompleteData;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.StorePagerAdapter;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.DrawerFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.UpdateRepository;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.BadgeView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.NumberFormat;
import lombok.Getter;
import lombok.Setter;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment implements DrawerFragment {

  public static final String APTOIDE_FACEBOOK_LINK = "http://www.facebook.com/aptoide";
  public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
  public static final String BACKUP_APPS_PACKAGE_NAME = "pt.aptoide.backupapps";
  public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
  public static final String APTOIDE_TWITTER_URL = "http://www.twitter.com/aptoide";

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private BadgeView updatesBadge;
  @Getter @Setter private Event.Name desiredViewPagerItem = null;
  private ChangeTabReceiver receiver;
  private UpdateRepository updateRepository;

  public static HomeFragment newInstance(String storeName, StoreContext storeContext,
      String storeTheme) {
    Bundle args = new Bundle();
    args.putString(BundleCons.STORE_NAME, storeName);
    args.putSerializable(BundleCons.STORE_CONTEXT, storeContext);
    args.putSerializable(BundleCons.STORE_THEME, storeTheme);
    HomeFragment fragment = new HomeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onResume() {
    super.onResume();
    setUserDataOnHeader();
    getToolbar().setTitle("");
  }

  private void setUserDataOnHeader() {
    if (navigationView == null || navigationView.getVisibility() != View.VISIBLE) {
      // if the navigation view is not visible do nothing
      return;
    }

    View baseHeaderView = navigationView.getHeaderView(0);
    TextView userEmail = (TextView) baseHeaderView.findViewById(R.id.profile_email_text);
    TextView userUsername = (TextView) baseHeaderView.findViewById(R.id.profile_name_text);
    ImageView userAvatarImage = (ImageView) baseHeaderView.findViewById(R.id.profile_image);

    if (AptoideAccountManager.isLoggedIn()) {

      userEmail.setVisibility(View.VISIBLE);
      userUsername.setVisibility(View.VISIBLE);

      UserCompleteData userCompleteData = AptoideAccountManager.getUserData();
      userEmail.setText(userCompleteData.getUserEmail());
      userUsername.setText(userCompleteData.getUserName());

      ImageLoader.with(getContext())
          .loadWithCircleTransformAndPlaceHolder(userCompleteData.getUserAvatar(), userAvatarImage,
              R.drawable.user_account_white);

      return;
    }

    userEmail.setText("");
    userUsername.setText("");

    userEmail.setVisibility(View.GONE);
    userUsername.setVisibility(View.GONE);

    ImageLoader.with(getContext()).load(R.drawable.user_account_white, userAvatarImage);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onDestroyView() {
    getContext().unregisterReceiver(receiver);
    receiver = null;
    super.onDestroyView();
  }

  @Override protected void setupViewPager() {
    super.setupViewPager();

    StorePagerAdapter adapter = (StorePagerAdapter) viewPager.getAdapter();
    int count = adapter.getCount();
    for (int i = 0; i < count; i++) {
      if (Event.Name.myUpdates.equals(adapter.getEventName(i))) {
        updatesBadge = new BadgeView(getContext(),
            ((LinearLayout) pagerSlidingTabStrip.getChildAt(0)).getChildAt(i));
        break;
      }
    }

    updateRepository.getNonExcludedUpdates()
        .map(updates -> updates.size())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(size -> refreshUpdatesBadge(size), throwable -> {
          CrashReport.getInstance().log(throwable);
        });

    if (desiredViewPagerItem != null) {
      if (adapter.containsEventName(desiredViewPagerItem)) {
        viewPager.setCurrentItem(adapter.getEventNamePosition(desiredViewPagerItem));
      }
    }
  }

  public void refreshUpdatesBadge(int num) {
    // No updates present
    if (updatesBadge == null) {
      return;
    }

    updatesBadge.setTextSize(11);

    if (num > 0) {
      updatesBadge.setText(NumberFormat.getIntegerInstance().format(num));
      if (!updatesBadge.isShown()) {
        updatesBadge.show(true);
      }
    } else {
      if (updatesBadge.isShown()) {
        updatesBadge.hide(true);
      }
    }
  }

  @Override public int getContentViewId() {
    return R.layout.activity_main;
  }

  @Override protected void setupSearch(Menu menu) {
    SearchUtils.setupGlobalSearchView(menu, getNavigationManager());
  }

  @Override public void setupViews() {
    super.setupViews();
    setupNavigationView();
  }

  private void setupNavigationView() {
    if (navigationView != null) {
      navigationView.setItemIconTintList(null);
      navigationView.setNavigationItemSelectedListener(menuItem -> {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.navigation_item_my_account) {
          AptoideAccountManager.openAccountManager(getContext());
        } else {
          final NavigationManagerV4 navigationManager = getNavigationManager();
          if (itemId == R.id.navigation_item_rollback) {
            navigationManager.navigateTo(V8Engine.getFragmentProvider().newRollbackFragment());
          } else if (itemId == R.id.navigation_item_setting_scheduled_downloads) {
            navigationManager.navigateTo(
                V8Engine.getFragmentProvider().newScheduledDownloadsFragment());
          } else if (itemId == R.id.navigation_item_excluded_updates) {
            navigationManager.navigateTo(
                V8Engine.getFragmentProvider().newExcludedUpdatesFragment());
          } else if (itemId == R.id.navigation_item_settings) {
            navigationManager.navigateTo(V8Engine.getFragmentProvider().newSettingsFragment());
          } else if (itemId == R.id.navigation_item_facebook) {
            openFacebook();
          } else if (itemId == R.id.navigation_item_twitter) {
            openTwitter();
          } else if (itemId == R.id.navigation_item_backup_apps) {
            openBackupApps();
          } else if (itemId == R.id.send_feedback) {
            startFeedbackFragment();
          }
        }

        drawerLayout.closeDrawer(navigationView);

        return false;
      });
    }
  }

  private void openFacebook() {
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
    installedAccessor.get(FACEBOOK_PACKAGE_NAME)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installedFacebook -> {
          openSocialLink(FACEBOOK_PACKAGE_NAME, APTOIDE_FACEBOOK_LINK,
              getContext().getString(R.string.social_facebook_screen_title), Uri.parse(
                  AptoideUtils.SocialLinksU.getFacebookPageURL(
                      installedFacebook == null ? 0 : installedFacebook.getVersionCode(),
                      APTOIDE_FACEBOOK_LINK)));
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  private void openTwitter() {
    openSocialLink(TWITTER_PACKAGE_NAME, APTOIDE_TWITTER_URL,
        getContext().getString(R.string.social_twitter_screen_title),
        Uri.parse(APTOIDE_TWITTER_URL));
  }

  private void openBackupApps() {
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
    installedAccessor.get(BACKUP_APPS_PACKAGE_NAME)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(installed -> {
          if (installed == null) {
            getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
                .newAppViewFragment(BACKUP_APPS_PACKAGE_NAME, AppViewFragment.OpenType.OPEN_ONLY));
          } else {
            Intent i = getContext().getPackageManager()
                .getLaunchIntentForPackage(BACKUP_APPS_PACKAGE_NAME);
            startActivity(i);
          }
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  private void startFeedbackFragment() {
    String downloadFolderPath = Application.getContext().getCacheDir().getPath();
    String screenshotFileName = getActivity().getClass().getSimpleName() + ".jpg";
    AptoideUtils.ScreenU.takeScreenshot(getActivity(), downloadFolderPath, screenshotFileName);
    getNavigationManager().navigateTo(V8Engine.getFragmentProvider()
        .newSendFeedbackFragment(downloadFolderPath + screenshotFileName));
  }

  private void openSocialLink(String packageName, String socialUrl, String pageTitle,
      Uri uriToOpenApp) {
    InstalledAccessor installedAccessor = AccessorFactory.getAccessorFor(Installed.class);
    installedAccessor.get(packageName)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(installedFacebook -> {
          if (installedFacebook == null) {
            getNavigationManager().navigateTo(
                V8Engine.getFragmentProvider().newSocialFragment(socialUrl, pageTitle));
          } else {
            Intent sharingIntent = new Intent(Intent.ACTION_VIEW, uriToOpenApp);
            getContext().startActivity(sharingIntent);
          }
        }, err -> {
          CrashReport.getInstance().log(err);
        });
  }

  protected boolean displayHomeUpAsEnabled() {
    return false;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle("");
    toolbar.setNavigationIcon(R.drawable.ic_drawer);
    toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    updateRepository = RepositoryFactory.getUpdateRepository();

    navigationView = (NavigationView) view.findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

    setHasOptionsMenu(true);

    receiver = new ChangeTabReceiver();
    getContext().registerReceiver(receiver, new IntentFilter(ChangeTabReceiver.SET_TAB_EVENT));

    Analytics.AppViewViewedFrom.addStepToList("HOME");
  }

  @Override public boolean isDrawerOpened() {
    return drawerLayout.isDrawerOpen(Gravity.LEFT);
  }

  @Override public void openDrawer() {
    drawerLayout.openDrawer(Gravity.LEFT);
  }

  @Override public void closeDrawer() {
    drawerLayout.closeDrawers();
  }

  public class ChangeTabReceiver extends BroadcastReceiver {

    public static final String SET_TAB_EVENT = "SET_TAB_EVENT";

    @Override public void onReceive(Context context, Intent intent) {
      Event.Name tabToChange = (Event.Name) intent.getSerializableExtra(SET_TAB_EVENT);
      if (tabToChange != null) {
        StorePagerAdapter storePagerAdapter = viewPager.getAdapter() instanceof StorePagerAdapter
            ? ((StorePagerAdapter) viewPager.getAdapter()) : null;
        if (storePagerAdapter != null) {
          viewPager.setCurrentItem(
              ((StorePagerAdapter) viewPager.getAdapter()).getEventNamePosition(tabToChange));
        }
      }
    }
  }
}
