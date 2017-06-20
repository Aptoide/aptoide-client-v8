package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialStoreLatestAppsDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 29/11/2016.
 */

public class SocialStoreLatestAppsWidget
    extends SocialCardWidget<SocialStoreLatestAppsDisplayable> {

  private final LayoutInflater inflater;
  private TextView storeName;
  private TextView userName;
  private TextView sharedStoreName;
  private TextView sharedStoreSubscribersNumber;
  private TextView sharedStoreAppsNumber;
  private LinearLayout appsContainer;
  private ImageView sharedStoreAvatar;
  private View store;
  private Map<View, Long> apps;
  private Map<Long, String> appsPackages;
  private CardView cardView;
  private Button followStore;
  private StoreRepository storeRepository;
  private AptoideAccountManager accountManager;
  private StoreUtilsProxy storeUtilsProxy;
  private TextView sharedStoreNameBodyTitle;

  public SocialStoreLatestAppsWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
    apps = new HashMap<>();
    appsPackages = new HashMap<>();
    storeRepository = RepositoryFactory.getStoreRepository();
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    store = itemView.findViewById(R.id.social_header);
    storeName = (TextView) itemView.findViewById(R.id.card_title);
    userName = (TextView) itemView.findViewById(R.id.card_subtitle);
    storeAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    userAvatar = (ImageView) itemView.findViewById(R.id.card_user_avatar);
    sharedStoreNameBodyTitle = (TextView) itemView.findViewById(R.id.social_shared_store_name);
    sharedStoreName = (TextView) itemView.findViewById(R.id.store_name);
    sharedStoreAvatar = (ImageView) itemView.findViewById(R.id.social_shared_store_avatar);
    appsContainer = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    cardView = (CardView) itemView.findViewById(R.id.card);
    followStore = (Button) itemView.findViewById(R.id.follow_btn);
    sharedStoreSubscribersNumber =
        (TextView) itemView.findViewById(R.id.social_number_of_followers_text);
    sharedStoreAppsNumber = (TextView) itemView.findViewById(R.id.social_number_of_apps_text);
  }

  @Override public void bindView(SocialStoreLatestAppsDisplayable displayable) {
    super.bindView(displayable);
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    storeUtilsProxy =
        new StoreUtilsProxy(accountManager, baseBodyInterceptor, new StoreCredentialsProviderImpl(),
            AccessorFactory.getAccessorFor(Store.class), httpClient,
            WebService.getDefaultConverter(),
            ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    storeName.setText(displayable.getStoreName());
    userName.setText(displayable.getUser()
        .getName());
    setCardViewMargin(displayable, cardView);
    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeName.setText(displayable.getStyledTitle(context, displayable.getStoreName()));
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(displayable.getStore()
              .getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userName.setText(displayable.getUser()
            .getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), userAvatar);
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      userName.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeName.setText(displayable.getStyledTitle(context, displayable.getUser()
            .getName()));
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), storeAvatar);
      }
    }

    sharedStoreNameBodyTitle.setText(displayable.getSharedStore()
        .getName());

    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(displayable.getSharedStore()
            .getAvatar(), sharedStoreAvatar);
    sharedStoreName.setText(displayable.getSharedStore()
        .getName());
    sharedStoreSubscribersNumber.setText(String.valueOf(displayable.getSharedStore()
        .getStats()
        .getSubscribers()));
    sharedStoreAppsNumber.setText(String.valueOf(displayable.getSharedStore()
        .getStats()
        .getApps()));

    appsContainer.removeAllViews();
    apps.clear();
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (SocialStoreLatestAppsDisplayable.LatestApp latestApp : displayable.getLatestApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(context)
          .load(latestApp.getIconUrl(), latestAppIcon);
      latestAppName.setText(latestApp.getAppName());
      appsContainer.addView(latestAppView);
      apps.put(latestAppView, latestApp.getAppId());
      appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
    }

    for (View app : apps.keySet()) {
      compositeSubscription.add(RxView.clicks(app)
          .subscribe(click -> {
            knockWithSixpackCredentials(displayable.getAbTestingUrl());
            String packageName = appsPackages.get(apps.get(app));
            Analytics.AppsTimeline.clickOnCard(getCardTypeName(), packageName,
                Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
                Analytics.AppsTimeline.OPEN_APP_VIEW);
            displayable.sendSocialLatestAppsClickEvent(Analytics.AppsTimeline.OPEN_APP_VIEW,
                packageName, socialAction, displayable.getStoreName());
            displayable.sendStoreOpenAppEvent(packageName);
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newAppViewFragment(apps.get(app), packageName));
          }));
    }

    compositeSubscription.add(RxView.clicks(store)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbTestingUrl());
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
              Analytics.AppsTimeline.OPEN_STORE);
          displayable.sendSocialLatestAppsClickEvent(Analytics.AppsTimeline.OPEN_STORE,
              Analytics.AppsTimeline.BLANK, socialAction, displayable.getStoreName());
          displayable.sendOpenStoreEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(displayable.getStoreName(), displayable.getSharedStore()
                  .getAppearance()
                  .getTheme()));
        }));

    compositeSubscription.add(RxView.clicks(sharedStoreAvatar)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbTestingUrl());
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              Analytics.AppsTimeline.BLANK, displayable.getSharedStore()
                  .getName(), Analytics.AppsTimeline.OPEN_STORE);
          displayable.sendSocialLatestAppsClickEvent(Analytics.AppsTimeline.OPEN_STORE,
              Analytics.AppsTimeline.BLANK, socialAction, displayable.getSharedStore()
                  .getName());
          displayable.sendOpenSharedStoreEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(displayable.getSharedStore()
                  .getName(), displayable.getSharedStore()
                  .getAppearance()
                  .getTheme()));
        }));

    StoreTheme storeThemeEnum = StoreTheme.get(displayable.getSharedStore());

    followStore.setBackgroundDrawable(
        storeThemeEnum.getButtonLayoutDrawable(context.getResources(), context.getTheme()));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followStore.setElevation(0);
    }
    followStore.setTextColor(
        storeThemeEnum.getStoreHeaderColorResource(context.getResources(), context.getTheme()));

    final String storeName = displayable.getSharedStore()
        .getName();
    final String storeTheme = displayable.getSharedStore()
        .getName();

    compositeSubscription.add(storeRepository.isSubscribed(displayable.getSharedStore()
        .getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            followStore.setText(R.string.followed);
            compositeSubscription.add(RxView.clicks(followStore)
                .subscribe(__ -> {
                  storeUtilsProxy.unSubscribeStore(storeName,
                      displayable.getStoreCredentialsProvider());
                  ShowMessage.asSnack(itemView,
                      AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                          getContext().getResources(), storeName));
                }, err -> {
                  CrashReport.getInstance()
                      .log(err);
                }));
          } else {
            //int plusMarkDrawable = storeThemeEnum.getPlusmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
            followStore.setText(R.string.follow);
            compositeSubscription.add(RxView.clicks(followStore)
                .subscribe(__ -> {
                  storeUtilsProxy.subscribeStore(storeName);
                  ShowMessage.asSnack(itemView,
                      AptoideUtils.StringU.getFormattedString(R.string.store_followed,
                          getContext().getResources(), storeName));
                }, err -> {
                  CrashReport.getInstance()
                      .log(err);
                }));
          }
        }, (throwable) -> throwable.printStackTrace()));
  }

  @Override String getCardTypeName() {
    return SocialStoreLatestAppsDisplayable.CARD_TYPE_NAME;
  }
}
