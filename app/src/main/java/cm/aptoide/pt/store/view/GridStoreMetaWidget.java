package cm.aptoide.pt.store.view;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import java.util.List;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaWidget extends MetaStoresBaseWidget<GridStoreMetaDisplayable> {

  private static final String TAG = GridStoreMetaWidget.class.getSimpleName();
  private AptoideAccountManager accountManager;
  private LinearLayout socialChannelsLayout;
  private ImageView mainIcon;
  private TextView mainName;
  private TextView description;
  private Button followStoreButton;
  private TextView followersCountTv;
  private TextView appsCountTv;
  private TextView followingCountTv;
  private ImageView secondaryIcon;
  private TextView secondaryName;
  private StoreUtilsProxy storeUtilsProxy;
  private ImageView badgeIcon;
  private View separator;
  private SpannableFactory spannableFactory;
  private View editStoreButton;
  private View buttonsLayout;
  private StoreCredentialsProviderImpl storeCredentialsProvider;
  private StoreAccessor storeAccessor;

  public GridStoreMetaWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    socialChannelsLayout = (LinearLayout) itemView.findViewById(R.id.social_channels);
    mainIcon = (ImageView) itemView.findViewById(R.id.main_icon);
    secondaryIcon = (ImageView) itemView.findViewById(R.id.secondary_icon);
    mainName = (TextView) itemView.findViewById(R.id.main_name);
    secondaryName = (TextView) itemView.findViewById(R.id.secondary_name);
    description = (TextView) itemView.findViewById(R.id.description);
    followStoreButton = (Button) itemView.findViewById(R.id.follow_store_button);
    editStoreButton = itemView.findViewById(R.id.edit_store_button);
    badgeIcon = (ImageView) itemView.findViewById(R.id.badge_icon);
    appsCountTv = (TextView) itemView.findViewById(R.id.apps_text_view);
    followingCountTv = (TextView) itemView.findViewById(R.id.following_text_view);
    buttonsLayout = itemView.findViewById(R.id.action_button_layout);
    followersCountTv = (TextView) itemView.findViewById(R.id.followers_text_view);
    separator = itemView.findViewById(R.id.separator);
  }

  @Override public void bindView(GridStoreMetaDisplayable displayable) {

    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    storeAccessor = AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()).getDatabase(), Store.class);
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor,
        new StoreCredentialsProviderImpl(storeAccessor),
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(),
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
    spannableFactory = new SpannableFactory();
    storeCredentialsProvider = new StoreCredentialsProviderImpl(storeAccessor);
    FragmentNavigator fragmentNavigator = getFragmentNavigator();
    Resources resources = getContext().getResources();
    followersCountTv.setOnClickListener(v -> {
      navigateToFollowersScreen(displayable, resources, fragmentNavigator);
    });
    followingCountTv.setOnClickListener(
        v -> navigateToFollowingScreen(displayable, fragmentNavigator, resources));

    compositeSubscription.add(createHomeMeta(displayable).observeOn(AndroidSchedulers.mainThread())
        .doOnNext(homeMeta -> {
          ParcelableSpan[] textStyle = {
              new StyleSpan(android.graphics.Typeface.BOLD),
              new ForegroundColorSpan(homeMeta.getThemeColor())
          };
          showMainIcon(homeMeta.getMainIcon());
          showSecondaryIcon(homeMeta.getSecondaryIcon());
          showMainName(homeMeta.getMainName());
          showSecondaryName(homeMeta.getSecondaryName());
          setupActionButton(homeMeta.isShowButton(), homeMeta.isOwner(), homeMeta.getStoreId(),
              StoreTheme.get(homeMeta.getThemeColor())
                  .getThemeName(), homeMeta.getMainName(), homeMeta.getDescription(),
              homeMeta.getMainIcon(), homeMeta.isFollowingStore());
          showSocialChannels(homeMeta.getSocialChannels());
          showAppsCount(homeMeta.getAppsCount(), textStyle, homeMeta.isShowApps());
          showFollowersCount(homeMeta.getFollowersCount(), textStyle);
          showFollowingCount(homeMeta.getFollowingCount(), textStyle);
          showDescription(homeMeta.getDescription());
          showBadge(homeMeta.getBadge());
        })
        .subscribe());
  }

  private void showBadge(HomeMeta.Badge badge) {
    Logger.d(TAG, "showBadge: " + badge.name());
  }

  private void navigateToFollowingScreen(GridStoreMetaDisplayable displayable,
      FragmentNavigator fragmentNavigator, Resources resources) {
    String screenTitle =
        AptoideUtils.StringU.getFormattedString(R.string.social_timeline_following_fragment_title,
            resources, displayable.getFollowingsCount());
    if (displayable.hasStore()) {
      fragmentNavigator.navigateTo(
          TimeLineFollowingFragment.newInstanceUsingStoreId(displayable.getStoreId(),
              displayable.getStoreTheme()
                  .getThemeName(), screenTitle, StoreContext.meta), true);
    } else {
      fragmentNavigator.navigateTo(
          TimeLineFollowingFragment.newInstanceUsingUserId(displayable.getUserId(), "DEFAULT",
              screenTitle, StoreContext.meta), true);
    }
  }

  private void navigateToFollowersScreen(GridStoreMetaDisplayable displayable, Resources resources,
      FragmentNavigator fragmentNavigator) {

    String screenTitle =
        AptoideUtils.StringU.getFormattedString(R.string.social_timeline_followers_fragment_title,
            resources, displayable.getFollowersCount());
    if (displayable.hasStore()) {
      fragmentNavigator.navigateTo(
          TimeLineFollowersFragment.newInstanceUsingStore(displayable.getStoreId(),
              displayable.getStoreTheme()
                  .getThemeName(), screenTitle, StoreContext.meta), true);
    } else {
      fragmentNavigator.navigateTo(
          TimeLineFollowersFragment.newInstanceUsingUser(displayable.getUserId(), "DEFAULT",
              screenTitle, StoreContext.meta), true);
    }
  }

  private void showDescription(String descriptionText) {
    if (descriptionText != null && !descriptionText.isEmpty()) {
      description.setText(descriptionText);
      description.setVisibility(View.VISIBLE);
    } else {
      description.setVisibility(View.GONE);
    }
  }

  private void showFollowingCount(long followingCount, ParcelableSpan[] textStyle) {
    String countText = AptoideUtils.StringU.withSuffix(followingCount);
    String followingText =
        String.format(getContext().getString(R.string.storehometab_short_following), countText);
    followingCountTv.setText(spannableFactory.createMultiSpan(followingText, textStyle, countText));
  }

  private void showFollowersCount(long followersCount, ParcelableSpan[] textStyle) {
    String count = AptoideUtils.StringU.withSuffix(followersCount);
    String followingText =
        String.format(getContext().getString(R.string.storehometab_short_subscribers), count);
    followersCountTv.setText(spannableFactory.createMultiSpan(followingText, textStyle, count));
  }

  private void showAppsCount(long appsCount, ParcelableSpan[] textStyle, boolean showApps) {
    if (showApps) {
      appsCountTv.setVisibility(View.VISIBLE);
      String count = AptoideUtils.StringU.withSuffix(appsCount);
      String followingText =
          String.format(getContext().getString(R.string.storehometab_short_apps), count);
      appsCountTv.setText(spannableFactory.createMultiSpan(followingText, textStyle, count));
    } else {
      appsCountTv.setVisibility(View.GONE);
    }
  }

  private void showSocialChannels(
      List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannel> socialChannels) {
    if (socialChannels != null && !socialChannels.isEmpty()) {
      setupSocialLinks(socialChannels, socialChannelsLayout);
      socialChannelsLayout.setVisibility(View.VISIBLE);
      separator.setVisibility(View.GONE);
    } else {
      socialChannelsLayout.setVisibility(View.GONE);
      separator.setVisibility(View.VISIBLE);
    }
  }

  private void setupActionButton(boolean shouldShow, boolean owner, long storeId,
      String storeThemeName, String storeName, String storeDescription, String storeImagePath,
      boolean isFollowed) {
    if (shouldShow) {
      buttonsLayout.setVisibility(View.VISIBLE);
      if (owner) {
        setupEditButton(storeId, storeThemeName, storeName, storeDescription, storeImagePath);
      } else {
        setupFollowButton(storeName, isFollowed);
      }
    } else {
      buttonsLayout.setVisibility(View.GONE);
    }
  }

  private void setupFollowButton(String storeName, boolean isFollowed) {
    editStoreButton.setVisibility(View.GONE);
    followStoreButton.setVisibility(View.VISIBLE);
    if (isFollowed) {
      setupUnfollowButton(storeName);
    } else {
      setupFollowButton(storeName);
    }
  }

  private void setupUnfollowButton(String storeName) {
    followStoreButton.setOnClickListener(
        v -> storeUtilsProxy.unSubscribeStore(storeName, storeCredentialsProvider));
    followStoreButton.setText(R.string.unfollow);
  }

  private void setupFollowButton(String storeName) {
    followStoreButton.setText(R.string.follow);
    followStoreButton.setOnClickListener(v -> storeUtilsProxy.subscribeStoreObservable(storeName)
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe(() -> {
          followStoreButton.setText(R.string.unfollow);
          followStoreButton.setEnabled(false);
        })
        .subscribe(getStoreMeta -> {
          if (getStoreMeta.isOk()) {
            followStoreButton.setText(R.string.unfollow);
            followStoreButton.setEnabled(true);
          } else {
            showFollowStoreError();
          }
        }, throwable -> showFollowStoreError()));
  }

  private void showFollowStoreError() {
    followStoreButton.setText(R.string.follow);
    followStoreButton.setEnabled(true);
    Snackbar.make(itemView, R.string.storetab_short_follow_error,
        BaseTransientBottomBar.LENGTH_LONG)
        .show();
  }

  private void setupEditButton(long storeId, String storeThemeName, String storeName,
      String storeDescription, String storeImagePath) {
    editStoreButton.setVisibility(View.VISIBLE);
    followStoreButton.setVisibility(View.GONE);
    editStoreButton.setOnClickListener(
        v -> navigateToEditStore(storeId, storeThemeName, storeName, storeDescription,
            storeImagePath));
  }

  private void navigateToEditStore(long storeId, String storeThemeName, String storeName,
      String storeDescription, String storeImagePath) {
    ManageStoreFragment.ViewModel viewModel =
        new ManageStoreFragment.ViewModel(storeId, StoreTheme.fromName(storeThemeName), storeName,
            storeDescription, storeImagePath);
    getFragmentNavigator().navigateTo(ManageStoreFragment.newInstance(viewModel, false), true);
  }

  private void showSecondaryName(String secondaryNameString) {
    if (secondaryName != null) {
      secondaryName.setText(secondaryNameString);
    }
  }

  private void showMainName(String mainNameString) {
    if (mainNameString != null) {
      mainName.setText(mainNameString);
    }
  }

  private Observable<HomeMeta> createHomeMeta(GridStoreMetaDisplayable displayable) {
    return displayable.isFollowingStore(storeAccessor)
        .flatMap(isFollowing -> displayable.isStoreOwner(accountManager)
            .map(isOwner -> new HomeMeta(displayable.getMainIcon(), displayable.getSecondaryIcon(),
                displayable.getMainName(), displayable.getSecondaryName(), isOwner,
                displayable.hasStore(), isFollowing, displayable.getSocialLinks(),
                displayable.getAppsCount(), displayable.getFollowersCount(),
                displayable.getFollowingsCount(), displayable.getDescription(),
                getColorOrDefault(displayable.getStoreTheme()), displayable.getStoreId(),
                displayable.hasStore(), displayable.getBadge())));
  }

  private @ColorInt int getColorOrDefault(StoreTheme theme) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return getContext().getResources()
          .getColor(theme.getPrimaryColor(), getContext().getTheme());
    } else {
      return getContext().getResources()
          .getColor(theme.getPrimaryColor());
    }
  }

  private void showMainIcon(String mainIconUrl) {
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(mainIconUrl, mainIcon);
  }

  private void showSecondaryIcon(String secondaryIconUrl) {
    if (secondaryIconUrl != null) {
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(secondaryIconUrl, secondaryIcon);
      secondaryIcon.setVisibility(View.VISIBLE);
    } else {
      secondaryIcon.setVisibility(View.GONE);
    }
  }

  public static class HomeMeta {
    private final String mainIcon;
    private final String secondaryIcon;
    private final String mainName;
    private final String secondaryName;
    private final boolean owner;
    private final boolean showButton;
    private final boolean followingStore;
    private final List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannel>
        socialChannels;
    private final long appsCount;
    private final long followersCount;
    private final long followingCount;
    private final String description;
    private final int themeColor;
    private final long storeId;
    private final boolean showApps;
    private final Badge badge;

    public HomeMeta(String mainIcon, String secondaryIcon, String mainName, String secondaryName,
        boolean owner, boolean showButton, boolean followingStore,
        List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannel> socialChannels,
        long appsCount, long followersCount, long followingCount, String description,
        int themeColor, long storeId, boolean showApps, Badge badge) {
      this.mainIcon = mainIcon;
      this.secondaryIcon = secondaryIcon;
      this.mainName = mainName;
      this.secondaryName = secondaryName;
      this.owner = owner;
      this.showButton = showButton;
      this.followingStore = followingStore;
      this.socialChannels = socialChannels;
      this.appsCount = appsCount;
      this.followersCount = followersCount;
      this.followingCount = followingCount;
      this.description = description;
      this.themeColor = themeColor;
      this.storeId = storeId;
      this.showApps = showApps;
      this.badge = badge;
    }

    public Badge getBadge() {
      return badge;
    }

    public boolean isFollowingStore() {
      return followingStore;
    }

    public boolean isShowButton() {
      return showButton;
    }

    public long getStoreId() {
      return storeId;
    }

    public String getMainIcon() {
      return mainIcon;
    }

    public String getDescription() {
      return description;
    }

    public String getSecondaryIcon() {
      return secondaryIcon;
    }

    public String getMainName() {
      return mainName;
    }

    public String getSecondaryName() {
      return secondaryName;
    }

    public boolean isOwner() {
      return owner;
    }

    public List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannel> getSocialChannels() {
      return socialChannels;
    }

    public long getAppsCount() {
      return appsCount;
    }

    public long getFollowersCount() {
      return followersCount;
    }

    public long getFollowingCount() {
      return followingCount;
    }

    public int getThemeColor() {
      return themeColor;
    }

    public boolean isShowApps() {
      return showApps;
    }

    enum Badge {
      NONE, BRONZE, SILVER, GOLD, PLATINUM
    }
  }
}
