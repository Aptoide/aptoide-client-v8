package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineClickEvent;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewStoreWidget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 29/11/2016.
 */

public class SocialStoreLatestAppsWidget
    extends SocialCardWidget<SocialStoreLatestAppsDisplayable> {

  private static final String CARD_TYPE_NAME = "SOCIAL_LATEST_APPS";
  private final LayoutInflater inflater;
  private TextView storeName;
  private TextView userName;
  private TextView sharedStoreName;
  private TextView sharedStoreSubscribersNumber;
  private TextView sharedStoreAppsNumber;
  private LinearLayout appsContaner;
  private ImageView storeAvatar;
  private ImageView userAvatar;
  private ImageView sharedStoreAvatar;
  private View store;
  private Map<View, Long> apps;
  private Map<Long, String> appsPackages;
  private CardView cardView;
  private Button followStore;
  private StoreRepository storeRepository;
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
    storeName.setText(displayable.getStoreName());
    userName.setText(displayable.getUser().getName());
    setCardViewMargin(displayable, cardView);
    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      storeName.setVisibility(View.VISIBLE);
      storeName.setText(displayable.getStore().getName());
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        userName.setVisibility(View.VISIBLE);
        userName.setText(displayable.getUser().getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
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
        ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }

    //if ((displayable.getUserSharer() != null || displayable.getUserSharer().getName() != null)) {
    //  if (!displayable.getUser().getName().equals(displayable.getUserSharer().getName())) {
    //    sharedBy.setVisibility(View.VISIBLE);
    //    sharedBy.setText(displayable.getSharedBy(getContext()));
    //  }
    //}

    ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getSharedStore().getAvatar(),
        sharedStoreAvatar);
    sharedStoreName.setText(displayable.getSharedStore().getName());
    //sharedStoreSubscribersNumber.setText("" + displayable.getSharedStore().getStats().getSubscribers());
    //sharedStoreAppsNumber.setText("" + displayable.getSharedStore().getStats().getApps());

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
        displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
            .cardType(getCardTypeName())
            .source(TimelineClickEvent.SOURCE_APTOIDE)
            .specific(SendEventRequest.Body.Specific.builder()
                .app(packageName)
                .store(displayable.getStoreName())
                .build())
            .build(), TimelineClickEvent.OPEN_APP);
        ((FragmentShower) context).pushFragmentV4(
            V8Engine.getFragmentProvider().newAppViewFragment(apps.get(app), packageName));
      }));
    }

    compositeSubscription.add(RxView.clicks(store).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbTestingUrl());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
          Analytics.AppsTimeline.OPEN_STORE);
      displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
          .cardType(getCardTypeName())
          .source(TimelineClickEvent.SOURCE_APTOIDE)
          .specific(
              SendEventRequest.Body.Specific.builder().store(displayable.getStoreName()).build())
          .build(), TimelineClickEvent.OPEN_STORE);
      ((FragmentShower) context).pushFragmentV4(V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getStoreName(),
              displayable.getSharedStore().getAppearance().getTheme()));
    }));

    compositeSubscription.add(RxView.clicks(sharedStoreAvatar).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbTestingUrl());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          Analytics.AppsTimeline.BLANK, displayable.getSharedStore().getName(),
          Analytics.AppsTimeline.OPEN_STORE);
      displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
          .cardType(getCardTypeName())
          .source(TimelineClickEvent.SOURCE_APTOIDE)
          .specific(SendEventRequest.Body.Specific.builder()
              .store(displayable.getSharedStore().getName())
              .build())
          .build(), TimelineClickEvent.OPEN_STORE);
      ((FragmentShower) context).pushFragmentV4(V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getSharedStore().getName(),
              displayable.getSharedStore().getAppearance().getTheme()));
    }));

    StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(displayable.getSharedStore());

    followStore.setBackgroundDrawable(storeThemeEnum.getButtonLayoutDrawable());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followStore.setElevation(0);
    }
    followStore.setTextColor(storeThemeEnum.getStoreHeaderInt());

    compositeSubscription.add(storeRepository.isSubscribed(displayable.getSharedStore().getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            //int checkmarkDrawable = storeThemeEnum.getCheckmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(checkmarkDrawable, 0, 0, 0);
            followStore.setText(R.string.followed);
            followStore.setOnClickListener(
                new AppViewStoreWidget.Listeners().newOpenStoreListener(itemView,
                    displayable.getSharedStore().getName(),
                    displayable.getSharedStore().getAppearance().getTheme()));
          } else {
            //int plusMarkDrawable = storeThemeEnum.getPlusmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
            followStore.setText(R.string.appview_follow_store_button_text);
            followStore.setOnClickListener(
                new AppViewStoreWidget.Listeners().newSubscribeStoreListener(itemView,
                    displayable.getSharedStore().getName()));
          }
        }, (throwable) -> {
          throwable.printStackTrace();
        }));
  }

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
  }
}
