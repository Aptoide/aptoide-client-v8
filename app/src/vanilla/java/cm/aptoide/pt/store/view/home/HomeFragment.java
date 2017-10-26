package cm.aptoide.pt.store.view.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DrawerAnalytics;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.crashreports.IssuesAnalytics;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.view.SearchBuilder;
import cm.aptoide.pt.spotandshareapp.SpotAndShareActivity;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StorePagerAdapter;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.custom.BadgeView;
import com.crashlytics.android.answers.Answers;
import com.facebook.appevents.AppEventsLogger;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.NumberFormat;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
  private static final String TAG = HomeFragment.class.getName();
  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private BadgeView updatesBadge;
  private BadgeView notificationsBadge;
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
  private PageViewsAnalytics pageViewsAnalytics;
  private SearchBuilder searchBuilder;
  private String defaultThemeName;
  private IssuesAnalytics issuesAnalytics;

  public static HomeFragment newInstance(String storeName, StoreContext storeContext,
      String storeTheme) {
    Bundle args = new Bundle();
    args.putString(BundleKeys.STORE_NAME.name(), storeName);
    args.putSerializable(BundleKeys.STORE_CONTEXT.name(), storeContext);
    args.putSerializable(BundleKeys.STORE_THEME.name(), storeTheme);
    HomeFragment fragment = new HomeFragment();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * @return {@link HomeFragment} instance with default store, store context and theme
   */
  public static HomeFragment newInstance(String defaultStore, String defaultTheme) {
    return newInstance(defaultStore, StoreContext.home, defaultTheme);
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

    baseHeaderView.setBackgroundColor(ContextCompat.getColor(getContext(),
        StoreTheme.get(defaultThemeName)
            .getPrimaryColor()));

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

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();

    defaultThemeName = application.getDefaultThemeName();
    final SearchManager searchManager =
        (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

    final SearchNavigator searchNavigator =
        new SearchNavigator(getFragmentNavigator(), application.getDefaultStoreName());

    final Analytics analytics = Analytics.getInstance();
    issuesAnalytics = new IssuesAnalytics(analytics, Answers.getInstance());

    searchBuilder = new SearchBuilder(searchManager, searchNavigator);

    drawerAnalytics = new DrawerAnalytics(analytics,
        AppEventsLogger.newLogger(getContext().getApplicationContext()));
    installedRepository =
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext());
    pageViewsAnalytics =
        new PageViewsAnalytics(AppEventsLogger.newLogger(getContext().getApplicationContext()),
            analytics, navigationTracker);
    setRegisterFragment(false);
    setHasOptionsMenu(true);
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

    View layout = getTabLayout(adapter, Event.Name.myUpdates);
    if (layout != null) {
      updatesBadge = new BadgeView(getContext(), layout);
    }

    layout = getTabLayout(adapter, Event.Name.getUserTimeline);
    if (layout != null) {
      notificationsBadge = new BadgeView(getContext(), layout);
    }

    ((AptoideApplication) getContext().getApplicationContext()).getNotificationCenter()
        .getUnreadNotifications()
        .observeOn(Schedulers.computation())
        .map(aptoideNotifications -> aptoideNotifications.size())
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(numberOfNotificationsUnread -> refreshBadge(numberOfNotificationsUnread,
            notificationsBadge), throwable -> CrashReport.getInstance()
            .log(throwable));

    updateRepository.getNonExcludedUpdates()
        .map(updates -> updates.size())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(size -> refreshBadge(size, updatesBadge), throwable -> {
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

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.removeItem(R.id.menu_share);
    if (searchBuilder != null && searchBuilder.isValid()) {
      final FragmentActivity activity = getActivity();
      // from getActivity() "May return null if the fragment is associated with a Context instead."
      final Context context = getContext();
      if (activity != null) {
        searchBuilder.attachSearch(activity, menu.findItem(R.id.action_search));
        issuesAnalytics.attachSearchSuccess(false);
        return;
      } else if (context != null) {
        searchBuilder.attachSearch(context, menu.findItem(R.id.action_search));
        issuesAnalytics.attachSearchSuccess(true);
        return;
      } else {
        issuesAnalytics.attachSearchFailed(true);
        Logger.e(TAG, new IllegalStateException(
            "Unable to attach search to this fragment due to null parent"));
      }
    } else {
      issuesAnalytics.attachSearchFailed(false);
      Logger.e(TAG, new IllegalStateException(
          "Unable to attach search to this fragment due to invalid search builder"));
    }

    menu.removeItem(R.id.action_search);
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
      navigationTracker.registerScreen(ScreenTagHistory.Builder.build("Drawer"));
      pageViewsAnalytics.sendPageViewedEvent();
    });
  }

  @Override public void setupViews() {
    super.setupViews();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    setupNavigationView();
  }

  private View getTabLayout(StorePagerAdapter adapter, Event.Name tab) {
    for (int i = 0; i < adapter.getCount(); i++) {
      if (tab.equals(adapter.getEventName(i))) {
        return ((LinearLayout) pagerSlidingTabStrip.getChildAt(0)).getChildAt(i);
      }
    }
    return null;
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
            .setTitle(getString(R.string.spotandshare_title) + new String(" \uD83D\uDD38"));
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
            getActivityNavigator().navigateTo(SpotAndShareActivity.class);
            // FIXME: 10-08-2017 NAVIGATE TO SPOTANDSHARE NEW VERSION
          } else if (itemId == R.id.navigation_item_rollback) {
            drawerAnalytics.drawerInteract("Rollback");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newRollbackFragment(), true);
          } else if (itemId == R.id.navigation_item_setting_scheduled_downloads) {
            drawerAnalytics.drawerInteract("Scheduled Downloads");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newScheduledDownloadsFragment(), true);
          } else if (itemId == R.id.navigation_item_excluded_updates) {
            drawerAnalytics.drawerInteract("Excluded Updates");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newExcludedUpdatesFragment(), true);
          } else if (itemId == R.id.navigation_item_settings) {
            drawerAnalytics.drawerInteract("Settings");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newSettingsFragment(), true);
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
            getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
                    .newAppViewFragment(BACKUP_APPS_PACKAGE_NAME, AppViewFragment.OpenType.OPEN_ONLY),
                true);
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
    getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
        .newSendFeedbackFragment(downloadFolderPath + screenshotFileName), true);
  }

  private void openSocialLink(String packageName, String socialUrl, String pageTitle,
      Uri uriToOpenApp) {

    installedRepository.getInstalled(packageName)
        .first()
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(installedFacebook -> {
          if (installedFacebook == null) {
            getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
                .newSocialFragment(socialUrl, pageTitle), true);
          } else {
            Intent sharingIntent = new Intent(Intent.ACTION_VIEW, uriToOpenApp);
            getContext().startActivity(sharingIntent);
          }
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        });
  }

  public void refreshBadge(int num, BadgeView badgeToUpdate) {
    // No updates present
    if (badgeToUpdate == null) {
      return;
    }

    badgeToUpdate.setTextSize(11);

    if (num > 0) {
      badgeToUpdate.setText(NumberFormat.getIntegerInstance()
          .format(num));
      if (!badgeToUpdate.isShown()) {
        badgeToUpdate.show(true);
      }
    } else {
      if (badgeToUpdate.isShown()) {
        badgeToUpdate.hide(true);
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
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());

    navigationView = (NavigationView) view.findViewById(R.id.nav_view);
    drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

    setHasOptionsMenu(true);
  }

  private enum BundleKeys {
    STORE_NAME, STORE_CONTEXT, STORE_THEME
  }
}
