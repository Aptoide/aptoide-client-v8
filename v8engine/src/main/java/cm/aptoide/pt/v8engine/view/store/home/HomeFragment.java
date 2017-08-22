package cm.aptoide.pt.v8engine.view.store.home;

import android.app.Activity;
import android.content.Intent;
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
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.DrawerAnalytics;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.view.SpotSharePreviewActivity;
import cm.aptoide.pt.v8engine.updates.UpdateRepository;
import cm.aptoide.pt.v8engine.util.SearchUtils;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.custom.BadgeView;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigation;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigator;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import cm.aptoide.pt.v8engine.view.store.StorePagerAdapter;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.NumberFormat;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

  public static final String APTOIDE_FACEBOOK_LINK = "http://www.facebook.com/aptoide";
  public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
  public static final String BACKUP_APPS_PACKAGE_NAME = "pt.aptoide.backupapps";
  public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
  public static final String APTOIDE_TWITTER_URL = "http://www.twitter.com/aptoide";

  //private static final int SPOT_SHARE_PERMISSION_REQUEST_CODE = 6531;

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private BadgeView updatesBadge;
  private UpdateRepository updateRepository;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private TabNavigator tabNavigator;
  private TextView userEmail;
  private TextView userUsername;
  private ImageView userAvatarImage;
  private InstalledRepository installedRepository;
  private DrawerAnalytics drawerAnalytics;
  private ClickHandler backClickHandler;

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

  /**
   * @return {@link HomeFragment} instance with default store, store context and theme
   */
  public static HomeFragment newInstance() {
    return newInstance(V8Engine.getConfiguration()
        .getDefaultStore(), StoreContext.home, V8Engine.getConfiguration()
        .getDefaultTheme());
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof TabNavigator) {
      tabNavigator = (TabNavigator) activity;
    } else {
      throw new IllegalStateException(
          "Activity must implement " + TabNavigator.class.getSimpleName());
    }
  }

  @Override public void onResume() {
    super.onResume();

    getToolbar().setTitle("");

    if (navigationView == null || navigationView.getVisibility() != View.VISIBLE) {
      // if the navigation view is not visible do nothing
      return;
    }

    View baseHeaderView = navigationView.getHeaderView(0);
    userEmail = (TextView) baseHeaderView.findViewById(R.id.profile_email_text);
    userUsername = (TextView) baseHeaderView.findViewById(R.id.profile_name_text);
    userAvatarImage = (ImageView) baseHeaderView.findViewById(R.id.profile_image);

    accountManager.accountStatus()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.PAUSE))
        .subscribe(account -> {
          if (account == null || !account.isLoggedIn()) {
            setInvisibleUserImageAndName();
            return;
          }
          setVisibleUserImageAndName(account);
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private void setInvisibleUserImageAndName() {
    userEmail.setText("");
    userUsername.setText("");
    userEmail.setVisibility(View.GONE);
    userUsername.setVisibility(View.GONE);
    ImageLoader.with(getContext())
        .loadWithCircleTransform(R.drawable.user_account_white, userAvatarImage);
  }

  private void setVisibleUserImageAndName(Account account) {
    userEmail.setVisibility(View.VISIBLE);
    userUsername.setVisibility(View.VISIBLE);
    userEmail.setText(account.getEmail());
    userUsername.setText(account.getNickname());
    ImageLoader.with(getContext())
        .loadWithCircleTransformAndPlaceHolder(account.getAvatar(), userAvatarImage,
            R.drawable.user_account_white);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    drawerAnalytics = new DrawerAnalytics(Analytics.getInstance(),
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onDestroyView() {
    userEmail = null;
    userAvatarImage = null;
    userUsername = null;
    updatesBadge = null;
    navigationView.setNavigationItemSelectedListener(null);
    navigationView = null;
    drawerLayout = null;
    final Toolbar toolbar = getToolbar();
    if (toolbar != null) {
      toolbar.setNavigationOnClickListener(null);
    }
    unregisterClickHandler(backClickHandler);
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
          CrashReport.getInstance()
              .log(throwable);
        });

    tabNavigator.navigation()
        .doOnNext(tabNavigation -> viewPager.setCurrentItem(
            ((StorePagerAdapter) viewPager.getAdapter()).getEventNamePosition(
                getEventName(tabNavigation.getTab()))))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override public int getContentViewId() {
    return R.layout.activity_main;
  }

  @Override protected void setupSearch(Menu menu) {
    SearchUtils.setupGlobalSearchView(menu, getActivity(), getFragmentNavigator());
  }

  @Override public void setupViews() {
    super.setupViews();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = new AccountNavigator(getFragmentNavigator(), accountManager);
    setupNavigationView();
  }

  protected boolean displayHomeUpAsEnabled() {
    return false;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle("");
    toolbar.setNavigationIcon(R.drawable.ic_drawer);
    toolbar.setNavigationOnClickListener(v -> {
      drawerLayout.openDrawer(GravityCompat.START);
      drawerAnalytics.drawerOpen();
    });
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    backClickHandler = new ClickHandler() {
      @Override public boolean handle() {
        if (isDrawerOpened()) {
          closeDrawer();
          return true;
        }

        return false;
      }
    };
    registerClickHandler(backClickHandler);
  }

  private void setupNavigationView() {
    if (navigationView != null) {

      try {
        //TODO emoji did not work on xml file. this sould be deleted in the next release
        navigationView.getMenu()
            .findItem(R.id.shareapps)
            .setTitle(getString(R.string.spot_share) + new String(" \uD83D\uDD38"));
      } catch (Exception e) {
        CrashReport.getInstance()
            .log(e);
      }
      navigationView.setItemIconTintList(null);
      navigationView.setNavigationItemSelectedListener(menuItem -> {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.navigation_item_my_account) {
          drawerAnalytics.drawerInteract("My Account");
          accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.MY_ACCOUNT);
        } else {
          final FragmentNavigator navigator = getFragmentNavigator();
          if (itemId == R.id.shareapps) {
            drawerAnalytics.drawerInteract("Spot&Share");
            getActivityNavigator().navigateTo(SpotSharePreviewActivity.class);
          } else if (itemId == R.id.navigation_item_rollback) {
            drawerAnalytics.drawerInteract("Rollback");
            navigator.navigateTo(V8Engine.getFragmentProvider()
                .newRollbackFragment());
          } else if (itemId == R.id.navigation_item_setting_scheduled_downloads) {
            drawerAnalytics.drawerInteract("Scheduled Downloads");
            navigator.navigateTo(V8Engine.getFragmentProvider()
                .newScheduledDownloadsFragment());
          } else if (itemId == R.id.navigation_item_excluded_updates) {
            drawerAnalytics.drawerInteract("Excluded Updates");
            navigator.navigateTo(V8Engine.getFragmentProvider()
                .newExcludedUpdatesFragment());
          } else if (itemId == R.id.navigation_item_settings) {
            drawerAnalytics.drawerInteract("Settings");
            navigator.navigateTo(V8Engine.getFragmentProvider()
                .newSettingsFragment());
          } else if (itemId == R.id.navigation_item_facebook) {
            drawerAnalytics.drawerInteract("Facebook");
            openFacebook();
          } else if (itemId == R.id.navigation_item_twitter) {
            drawerAnalytics.drawerInteract("Twitter");
            openTwitter();
          } else if (itemId == R.id.navigation_item_backup_apps) {
            drawerAnalytics.drawerInteract("Backup Apps");
            openBackupApps();
          } else if (itemId == R.id.send_feedback) {
            drawerAnalytics.drawerInteract("Send Feedback");
            startFeedbackFragment();
          }
        }

        drawerLayout.closeDrawer(navigationView);

        return false;
      });
    }
  }

  private void openFacebook() {

    installedRepository.getInstalled(FACEBOOK_PACKAGE_NAME)
        .first()
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installedFacebook -> {
          openSocialLink(FACEBOOK_PACKAGE_NAME, APTOIDE_FACEBOOK_LINK,
              getContext().getString(R.string.social_facebook_screen_title), Uri.parse(
                  AptoideUtils.SocialLinksU.getFacebookPageURL(
                      installedFacebook == null ? 0 : installedFacebook.getVersionCode(),
                      APTOIDE_FACEBOOK_LINK)));
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  private void openTwitter() {
    openSocialLink(TWITTER_PACKAGE_NAME, APTOIDE_TWITTER_URL,
        getContext().getString(R.string.social_twitter_screen_title),
        Uri.parse(APTOIDE_TWITTER_URL));
  }

  private void openBackupApps() {

    installedRepository.getInstalled(BACKUP_APPS_PACKAGE_NAME)
        .first()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(installed -> {
          if (installed == null) {
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newAppViewFragment(BACKUP_APPS_PACKAGE_NAME, AppViewFragment.OpenType.OPEN_ONLY));
          } else {
            Intent i = getContext().getPackageManager()
                .getLaunchIntentForPackage(BACKUP_APPS_PACKAGE_NAME);
            startActivity(i);
          }
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  private void startFeedbackFragment() {
    String downloadFolderPath = getContext().getApplicationContext()
        .getCacheDir()
        .getPath();
    String screenshotFileName = getActivity().getClass()
        .getSimpleName() + ".jpg";
    AptoideUtils.ScreenU.takeScreenshot(getActivity(), downloadFolderPath, screenshotFileName);
    getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
        .newSendFeedbackFragment(downloadFolderPath + screenshotFileName));
  }

  private void openSocialLink(String packageName, String socialUrl, String pageTitle,
      Uri uriToOpenApp) {

    installedRepository.getInstalled(packageName)
        .first()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(installedFacebook -> {
          if (installedFacebook == null) {
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newSocialFragment(socialUrl, pageTitle));
          } else {
            Intent sharingIntent = new Intent(Intent.ACTION_VIEW, uriToOpenApp);
            getContext().startActivity(sharingIntent);
          }
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  public void refreshUpdatesBadge(int num) {
    // No updates present
    if (updatesBadge == null) {
      return;
    }

    updatesBadge.setTextSize(11);

    if (num > 0) {
      updatesBadge.setText(NumberFormat.getIntegerInstance()
          .format(num));
      if (!updatesBadge.isShown()) {
        updatesBadge.show(true);
      }
    } else {
      if (updatesBadge.isShown()) {
        updatesBadge.hide(true);
      }
    }
  }

  private Event.Name getEventName(int tab) {
    switch (tab) {
      case TabNavigation.DOWNLOADS:
        return Event.Name.myDownloads;
      case TabNavigation.STORES:
        return Event.Name.myStores;
      case TabNavigation.TIMELINE:
        return Event.Name.getUserTimeline;
      case TabNavigation.UPDATES:
        return Event.Name.myUpdates;
      default:
        throw new IllegalArgumentException("Invalid tab.");
    }
  }

  private boolean isDrawerOpened() {
    return drawerLayout.isDrawerOpen(Gravity.LEFT);
  }

  private void closeDrawer() {
    drawerLayout.closeDrawers();
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);

    updateRepository = RepositoryFactory.getUpdateRepository(getContext(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());

    navigationView = (NavigationView) view.findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

    setHasOptionsMenu(true);

    Analytics.AppViewViewedFrom.addStepToList("HOME");
  }
}
