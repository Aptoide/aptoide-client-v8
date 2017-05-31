package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.os.Build;
import android.support.v4.app.Fragment;
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
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreThemeEnum;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialInstallDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialStoreLatestAppsWidget
    extends CardWidget<AggregatedSocialStoreLatestAppsDisplayable> {
  private final LayoutInflater inflater;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> baseBodyInterceptor;
  private final OkHttpClient httpClient;
  private TextView seeMore;
  private LinearLayout subCardsContainer;
  private ImageView headerAvatar1;
  private ImageView headerAvatar2;
  private TextView headerNames;
  private TextView headerTime;
  private CardView cardView;
  private TextView sharedStoreName;
  private TextView sharedStoreSubscribersNumber;
  private TextView sharedStoreAppsNumber;
  private LinearLayout appsContainer;
  private ImageView sharedStoreAvatar;
  private Map<View, Long> apps;
  private Map<Long, String> appsPackages;
  private Button followStore;
  private StoreRepository storeRepository;
  private StoreUtilsProxy storeUtilsProxy;
  private TextView sharedStoreNameBodyTitle;
  private TextView additionalNumberOfSharesLabel;
  private ImageView additionalNumberOfSharesCircularMask;

  public AggregatedSocialStoreLatestAppsWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    apps = new HashMap<>();
    appsPackages = new HashMap<>();
    storeRepository = RepositoryFactory.getStoreRepository();
    baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    storeUtilsProxy =
        new StoreUtilsProxy(accountManager, baseBodyInterceptor, new StoreCredentialsProviderImpl(),
            AccessorFactory.getAccessorFor(Store.class), httpClient,
            WebService.getDefaultConverter());
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    subCardsContainer =
        (LinearLayout) itemView.findViewById(R.id.timeline_sub_minimal_card_container);
    seeMore = (TextView) itemView.findViewById(R.id.timeline_aggregated_see_more);
    headerAvatar1 = (ImageView) itemView.findViewById(R.id.card_header_avatar_1);
    headerAvatar2 = (ImageView) itemView.findViewById(R.id.card_header_avatar_2);
    headerNames = (TextView) itemView.findViewById(R.id.card_title);
    headerTime = (TextView) itemView.findViewById(R.id.card_date);
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
    additionalNumberOfSharesCircularMask =
        (ImageView) itemView.findViewById(R.id.card_header_avatar_plus);
    additionalNumberOfSharesLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
  }

  @Override public void bindView(AggregatedSocialStoreLatestAppsDisplayable displayable) {
    super.bindView(displayable);

    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(displayable.getSharers()
            .get(0)
            .getUser()
            .getAvatar(), headerAvatar1);
    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(displayable.getSharers()
            .get(1)
            .getUser()
            .getAvatar(), headerAvatar2);
    headerNames.setText(displayable.getCardHeaderNames());
    headerTime.setText(displayable.getTimeSinceLastUpdate(getContext()));

    sharedStoreNameBodyTitle.setText(displayable.getSharedStoreName());

    ImageLoader.with(getContext())
        .loadWithShadowCircleTransform(displayable.getSharedStore()
            .getAvatar(), sharedStoreAvatar);
    sharedStoreName.setText(displayable.getSharedStoreName());
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
    for (App latestApp : displayable.getLatestApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(getContext())
          .load(latestApp.getIcon(), latestAppIcon);
      latestAppName.setText(latestApp.getName());
      appsContainer.addView(latestAppView);
      apps.put(latestAppView, latestApp.getId());
      appsPackages.put(latestApp.getId(), latestApp.getPackageName());
    }

    for (View app : apps.keySet()) {
      compositeSubscription.add(RxView.clicks(app)
          .subscribe(click -> {
            knockWithSixpackCredentials(displayable.getAbTestingURL());
            String packageName = appsPackages.get(apps.get(app));
            Analytics.AppsTimeline.clickOnCard(getCardTypeName(), packageName,
                Analytics.AppsTimeline.BLANK, displayable.getOwnerStore()
                    .getName(), Analytics.AppsTimeline.OPEN_APP_VIEW);
            displayable.sendStoreOpenAppEvent(packageName);
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newAppViewFragment(apps.get(app), packageName));
          }));
    }

    compositeSubscription.add(RxView.clicks(sharedStoreAvatar)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbTestingURL());
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              Analytics.AppsTimeline.BLANK, displayable.getSharedStore()
                  .getName(), Analytics.AppsTimeline.OPEN_STORE);
          displayable.sendOpenSharedStoreEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(displayable.getSharedStore()
                  .getName(), displayable.getSharedStore()
                  .getAppearance()
                  .getTheme()));
        }));

    StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(displayable.getSharedStore());

    followStore.setBackgroundDrawable(storeThemeEnum.getButtonLayoutDrawable());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followStore.setElevation(0);
    }
    followStore.setTextColor(storeThemeEnum.getStoreHeaderInt());

    final String storeName = displayable.getSharedStoreName();
    //final String storeTheme = displayable.getSharedStore().getAppearance().getTheme();

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
                          storeName));
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
                      AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
                }, err -> {
                  CrashReport.getInstance()
                      .log(err);
                }));
          }
        }, (throwable) -> throwable.printStackTrace()));

    setAdditionalNumberOfSharersLabel(displayable);
    setCardViewMargin(displayable, cardView);
    showSeeMoreAction(displayable);
    showSubCards(displayable, 2);
  }

  @Override String getCardTypeName() {
    return AggregatedSocialInstallDisplayable.CARD_TYPE_NAME;
  }

  private void setAdditionalNumberOfSharersLabel(
      AggregatedSocialStoreLatestAppsDisplayable displayable) {
    int numberOfSharers = displayable.getSharers()
        .size();

    if (numberOfSharers <= 2) {
      additionalNumberOfSharesLabel.setVisibility(View.INVISIBLE);
      additionalNumberOfSharesCircularMask.setVisibility(View.INVISIBLE);
      return;
    } else {
      additionalNumberOfSharesLabel.setVisibility(View.VISIBLE);
      additionalNumberOfSharesCircularMask.setVisibility(View.VISIBLE);
      numberOfSharers -= 2;
    }

    additionalNumberOfSharesLabel.setText(
        String.format(getContext().getString(R.string.timeline_short_plus),
            String.valueOf(numberOfSharers)));
  }

  private void showSubCards(AggregatedSocialStoreLatestAppsDisplayable displayable,
      int numberOfSubCardsToShow) {
    subCardsContainer.removeAllViews();
    int i = 1;
    for (MinimalCard minimalCard : displayable.getMinimalCards()) {
      if (i > numberOfSubCardsToShow) {
        break;
      }
      View subCardView =
          inflater.inflate(R.layout.timeline_sub_minimal_card, subCardsContainer, false);
      ImageView minimalCardHeaderMainAvatar = (ImageView) subCardView.findViewById(R.id.card_image);
      TextView minimalCardHeaderMainName = (TextView) subCardView.findViewById(R.id.card_title);
      ImageView minimalCardHeaderSecondaryAvatar =
          (ImageView) subCardView.findViewById(R.id.card_user_avatar);
      TextView minimalCardHeaderSecondaryName =
          (TextView) subCardView.findViewById(R.id.card_subtitle);
      TextView cardHeaderTimestamp = (TextView) subCardView.findViewById(R.id.card_date);
      LikeButtonView likeSubCardButton =
          (LikeButtonView) subCardView.findViewById(R.id.social_like_button);
      TextView shareSubCardButton = (TextView) subCardView.findViewById(R.id.social_share);
      LinearLayout likeLayout = (LinearLayout) subCardView.findViewById(R.id.social_like);
      TextView comment = (TextView) subCardView.findViewById(R.id.social_comment);
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(minimalCard.getSharers()
              .get(0)
              .getUser()
              .getAvatar(), minimalCardHeaderMainAvatar);

      minimalCardHeaderMainName.setText(minimalCard.getSharers()
          .get(0)
          .getUser()
          .getName());

      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(minimalCard.getSharers()
              .get(0)
              .getStore()
              .getAvatar(), minimalCardHeaderSecondaryAvatar);

      minimalCardHeaderSecondaryName.setText(minimalCard.getSharers()
          .get(0)
          .getStore()
          .getName());

      cardHeaderTimestamp.setText(
          displayable.getTimeSinceLastUpdate(getContext(), minimalCard.getDate()));

      if (minimalCard.getMy()
          .isLiked()) {
        likeSubCardButton.setHeartState(true);
      } else {
        likeSubCardButton.setHeartState(false);
      }

      compositeSubscription.add(RxView.clicks(shareSubCardButton)
          .subscribe(click -> shareCard(displayable, minimalCard.getCardId(), null,
              SharePreviewDialog.SharePreviewOpenMode.SHARE), err -> CrashReport.getInstance()
              .log(err)));

      compositeSubscription.add(RxView.clicks(likeLayout)
          .subscribe(click -> {
            if (!hasSocialPermissions(Analytics.Account.AccountOrigins.LIKE_CARD)) return;
            likeSubCardButton.performClick();
          }, throwable -> CrashReport.getInstance()
              .log(throwable)));

      compositeSubscription.add(RxView.clicks(likeSubCardButton)
          .flatMap(__ -> accountManager.accountStatus()
              .first()
              .toSingle()
              .toObservable())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(account -> likeCard(displayable, minimalCard.getCardId(), 1),
              err -> CrashReport.getInstance()
                  .log(err)));

      compositeSubscription.add(RxView.clicks(seeMore)
          .subscribe(click -> {
            showSubCards(displayable, 10);
            seeMore.setVisibility(View.GONE);
          }, throwable -> CrashReport.getInstance()
              .log(throwable)));

      compositeSubscription.add(accountManager.accountStatus()
          .subscribe());

      likeLayout.setVisibility(View.VISIBLE);
      comment.setVisibility(View.VISIBLE);

      compositeSubscription.add(RxView.clicks(comment)
          .flatMap(aVoid -> Observable.fromCallable(() -> {
            final String elementId = minimalCard.getCardId();
            Fragment fragment = V8Engine.getFragmentProvider()
                .newCommentGridRecyclerFragmentWithCommentDialogOpen(CommentType.TIMELINE,
                    elementId);
            getFragmentNavigator().navigateTo(fragment);
            return null;
          }))
          .subscribe(aVoid -> knockWithSixpackCredentials(displayable.getAbTestingURL()),
              err -> CrashReport.getInstance()
                  .log(err)));

      subCardsContainer.addView(subCardView);
      i++;
    }
  }

  private void showSeeMoreAction(AggregatedSocialStoreLatestAppsDisplayable displayable) {
    if (displayable.getMinimalCards()
        .size() > 2) {
      seeMore.setVisibility(View.VISIBLE);
    } else {
      seeMore.setVisibility(View.GONE);
    }
  }
}
