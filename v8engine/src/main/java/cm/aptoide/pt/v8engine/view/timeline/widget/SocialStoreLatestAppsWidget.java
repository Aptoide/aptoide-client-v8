package cm.aptoide.pt.v8engine.view.timeline.widget;

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
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialStoreLatestAppsDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
  private LinearLayout appsContaner;
  private ImageView sharedStoreAvatar;
  private View store;
  private Map<View, Long> apps;
  private Map<Long, String> appsPackages;
  private CardView cardView;
  private Button followStore;
  private StoreRepository storeRepository;
  private AptoideAccountManager accountManager;
  private StoreUtilsProxy storeUtilsProxy;
  //private TextView sharedBy;

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
    sharedStoreName = (TextView) itemView.findViewById(R.id.store_name);
    sharedStoreAvatar = (ImageView) itemView.findViewById(R.id.social_shared_store_avatar);
    appsContaner = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    cardView = (CardView) itemView.findViewById(R.id.card);
    followStore = (Button) itemView.findViewById(R.id.follow_btn);
    sharedStoreSubscribersNumber = (TextView) itemView.findViewById(R.id.number_of_followers);
    sharedStoreAppsNumber = (TextView) itemView.findViewById(R.id.number_of_apps);
    //sharedBy = (TextView) itemView.findViewById(R.id.social_shared_by);
  }

  @Override public void bindView(SocialStoreLatestAppsDisplayable displayable) {
    super.bindView(displayable);
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    storeUtilsProxy =
        new StoreUtilsProxy(accountManager, baseBodyInterceptor, new StoreCredentialsProviderImpl(),
            AccessorFactory.getAccessorFor(Store.class));
    storeName.setText(displayable.getStoreName());
    userName.setText(displayable.getUser().getName());
    setCardViewMargin(displayable, cardView);
    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeName.setText(displayable.getStore().getName());
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userName.setText(displayable.getUser().getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
      } else {
        userName.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      userName.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        storeName.setVisibility(View.VISIBLE);
        storeName.setText(displayable.getUser().getName());
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }

    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(displayable.getSharedStore().getAvatar(), sharedStoreAvatar);
    sharedStoreName.setText(displayable.getSharedStore().getName());

    appsContaner.removeAllViews();
    apps.clear();
    View latestAppView;
    ImageView latestAppIcon;
    for (SocialStoreLatestAppsDisplayable.LatestApp latestApp : displayable.getLatestApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContaner, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
      ImageLoader.with(context).load(latestApp.getIconUrl(), latestAppIcon);
      appsContaner.addView(latestAppView);
      apps.put(latestAppView, latestApp.getAppId());
      appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
    }

    for (View app : apps.keySet()) {
      compositeSubscription.add(RxView.clicks(app).subscribe(click -> {
        knockWithSixpackCredentials(displayable.getAbTestingUrl());
        String packageName = appsPackages.get(apps.get(app));
        Analytics.AppsTimeline.clickOnCard(getCardTypeName(), packageName,
            Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
            Analytics.AppsTimeline.OPEN_APP_VIEW);
        displayable.sendStoreOpenAppEvent(packageName);
        getFragmentNavigator().navigateTo(
            V8Engine.getFragmentProvider().newAppViewFragment(apps.get(app), packageName));
      }));
    }

    compositeSubscription.add(RxView.clicks(store).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbTestingUrl());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
          Analytics.AppsTimeline.OPEN_STORE);
      displayable.sendOpenStoreEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getStoreName(),
              displayable.getSharedStore().getAppearance().getTheme()));
    }));

    compositeSubscription.add(RxView.clicks(sharedStoreAvatar).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbTestingUrl());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          Analytics.AppsTimeline.BLANK, displayable.getSharedStore().getName(),
          Analytics.AppsTimeline.OPEN_STORE);
      displayable.sendOpenSharedStoreEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getSharedStore().getName(),
              displayable.getSharedStore().getAppearance().getTheme()));
    }));

    StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(displayable.getSharedStore());

    followStore.setBackgroundDrawable(storeThemeEnum.getButtonLayoutDrawable());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followStore.setElevation(0);
    }
    followStore.setTextColor(storeThemeEnum.getStoreHeaderInt());

    final String storeName = displayable.getSharedStore().getName();
    final String storeTheme = displayable.getSharedStore().getName();

    compositeSubscription.add(storeRepository.isSubscribed(displayable.getSharedStore().getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            followStore.setText(R.string.followed);
            compositeSubscription.add(RxView.clicks(followStore).subscribe(__ -> {
              storeUtilsProxy.unSubscribeStore(storeName,
                  displayable.getStoreCredentialsProvider());
              ShowMessage.asSnack(itemView,
                  AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                      storeName));
            }, err -> {
              CrashReport.getInstance().log(err);
            }));
          } else {
            //int plusMarkDrawable = storeThemeEnum.getPlusmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
            followStore.setText(R.string.follow);
            compositeSubscription.add(RxView.clicks(followStore).subscribe(__ -> {
              storeUtilsProxy.subscribeStore(storeName);
              ShowMessage.asSnack(itemView,
                  AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
            }, err -> {
              CrashReport.getInstance().log(err);
            }));
          }
        }, (throwable) -> {
          throwable.printStackTrace();
        }));
  }

  @Override String getCardTypeName() {
    return SocialStoreLatestAppsDisplayable.CARD_TYPE_NAME;
  }

  private void handleIsSubscribed(boolean isSubscribed, String storeName, String storeTheme) {
    if (isSubscribed) {
      // set store already followed button text and open store action
      Action1<Void> openStore = __ -> {
        getFragmentNavigator().navigateTo(
            V8Engine.getFragmentProvider().newStoreFragment(storeName, storeTheme));
      };
      followStore.setText(R.string.followed);
      compositeSubscription.add(RxView.clicks(followStore).subscribe(openStore));
    } else {

      // set follow store button text and subscribe store action
      Action1<Void> subscribeStore = __ -> {
        storeUtilsProxy.subscribeStore(storeName, getStoreMeta -> {
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
        }, err -> {
          CrashReport.getInstance().log(err);
        }, accountManager);
      };
      followStore.setText(R.string.follow);
      compositeSubscription.add(RxView.clicks(followStore).subscribe(subscribeStore));
    }
  }
}
