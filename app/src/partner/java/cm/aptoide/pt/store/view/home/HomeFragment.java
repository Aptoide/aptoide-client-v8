package cm.aptoide.pt.store.view.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DrawerAnalytics;
import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.firstinstall.FirstInstallFragment;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.preferences.PartnersSecurePreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.SuggestionCursorAdapter;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.suggestions.TrendingManager;
import cm.aptoide.pt.search.view.AppSearchSuggestionsView;
import cm.aptoide.pt.search.view.SearchSuggestionsPresenter;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StorePagerAdapter;
import cm.aptoide.pt.updates.UpdateRepository;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.custom.BadgeView;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.text.NumberFormat;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by neuro on 09-05-2016.
 */
public class HomeFragment extends StoreFragment {

  public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";

  @Inject AnalyticsManager analyticsManager;
  @Inject NavigationTracker navigationTracker;
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
  private DrawerAnalytics drawerAnalytics;
  private BackButton.ClickHandler backClickHandler;
  private String defaultThemeName;
  private AppSearchSuggestionsView appSearchSuggestionsView;
  private CrashReport crashReport;
  private SearchNavigator searchNavigator;
  private TrendingManager trendingManager;
  private SearchAnalytics searchAnalytics;

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
        .loadWithCircleTransform(R.drawable.ic_user_white, userAvatarImage);
  }

  private void setVisibleUserImageAndName(Account account) {
    userEmail.setVisibility(View.VISIBLE);
    userUsername.setVisibility(View.VISIBLE);
    userEmail.setText(account.getEmail());
    userUsername.setText(account.getNickname());
    ImageLoader.with(getContext())
        .loadWithCircleTransformAndPlaceHolder(account.getAvatar(), userAvatarImage,
            R.drawable.ic_user_circle);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();

    searchNavigator =
        new SearchNavigator(getFragmentNavigator(), application.getDefaultStoreName());

    defaultThemeName = application.getDefaultThemeName();

    drawerAnalytics = new DrawerAnalytics(analyticsManager, navigationTracker);
    handleFirstInstall(savedInstanceState);

    trendingManager = application.getTrendingManager();
    crashReport = CrashReport.getInstance();
    searchAnalytics = new SearchAnalytics(analyticsManager, navigationTracker);

    setRegisterFragment(false);
    setHasOptionsMenu(true);
  }

  @Override protected boolean hasSearchFromStoreFragment() {
    return false;
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
    inflater.inflate(R.menu.fragment_home, menu);

    final MenuItem menuItem = menu.findItem(R.id.menu_item_search);
    if (appSearchSuggestionsView != null && menuItem != null) {
      appSearchSuggestionsView.initialize(menuItem);
    } else if (menuItem != null) {
      menuItem.setVisible(false);
    } else {
      menu.removeItem(R.id.menu_item_search);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    backClickHandler = () -> {
      if (isDrawerOpened()) {
        closeDrawer();
        return true;
      }

      return false;
    };
    registerClickHandler(backClickHandler);

    final SuggestionCursorAdapter suggestionCursorAdapter =
        new SuggestionCursorAdapter(getContext());

    final Toolbar toolbar = getToolbar();
    final Observable<MenuItem> toolbarMenuItemClick = RxToolbar.itemClicks(toolbar)
        .publish()
        .autoConnect();

    appSearchSuggestionsView =
        new AppSearchSuggestionsView(this, RxView.clicks(toolbar), crashReport,
            suggestionCursorAdapter, PublishSubject.create(), toolbarMenuItemClick,
            searchAnalytics);

    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();

    final SearchSuggestionsPresenter searchSuggestionsPresenter =
        new SearchSuggestionsPresenter(appSearchSuggestionsView,
            application.getSearchSuggestionManager(), AndroidSchedulers.mainThread(),
            suggestionCursorAdapter, crashReport, trendingManager, searchNavigator, false,
            searchAnalytics);

    attachPresenter(searchSuggestionsPresenter);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
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

  private void setupNavigationView() {
    if (navigationView != null) {
      navigationView.setItemIconTintList(null);
      navigationView.setNavigationItemSelectedListener(menuItem -> {

        int itemId = menuItem.getItemId();
        if (itemId == R.id.navigation_item_my_account) {
          drawerAnalytics.drawerInteract("My Account");
          accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.MY_ACCOUNT);
        } else {
          final FragmentNavigator navigator = getFragmentNavigator();
          if (itemId == R.id.navigation_item_excluded_updates) {
            drawerAnalytics.drawerInteract("Excluded Updates");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newExcludedUpdatesFragment(), true);
          } else if (itemId == R.id.navigation_item_settings) {
            drawerAnalytics.drawerInteract("Settings");
            navigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newSettingsFragment(), true);
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

  /**
   * show first install fragment with animation
   */
  @SuppressLint("PrivateResource") private void handleFirstInstall(Bundle savedInstanceState) {
    ConnectivityManager connectivityManager =
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      if (savedInstanceState == null
          && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
          .isConnected()
          && ((PartnerApplication) getContext().getApplicationContext()).getBootConfig()
          .getPartner()
          .getSwitches()
          .getOptions()
          .getFirstInstall()
          .isEnable()
          && !PartnersSecurePreferences.isFirstInstallFinished(
          ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        transaction.add(R.id.fragment_placeholder, FirstInstallFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
      }
    }
  }

  private enum BundleKeys {
    STORE_NAME, STORE_CONTEXT, STORE_THEME
  }
}
