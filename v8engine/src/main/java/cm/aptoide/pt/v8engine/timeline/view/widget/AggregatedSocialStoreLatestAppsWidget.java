package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialInstallDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.List;
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
  private final TokenInvalidator tokenInvalidator;
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
    storeRepository = RepositoryFactory.getStoreRepository(getContext().getApplicationContext());
    baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    tokenInvalidator = ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, baseBodyInterceptor,
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
            ((V8Engine) getContext().getApplicationContext()
                .getApplicationContext()).getDatabase(), Store.class)),
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(), tokenInvalidator,
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
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
    if (displayable.getSharers()
        .get(0)
        .getUser() != null) {
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(displayable.getSharers()
              .get(0)
              .getUser()
              .getAvatar(), headerAvatar1);
    }
    if (displayable.getSharers()
        .size() > 1) {
      if (displayable.getSharers()
          .get(1)
          .getUser() != null) {
        ImageLoader.with(getContext())
            .loadWithShadowCircleTransform(displayable.getSharers()
                .get(1)
                .getUser()
                .getAvatar(), headerAvatar2);
      }
    }
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

    StoreTheme storeThemeEnum = StoreTheme.get(displayable.getSharedStore());

    followStore.setBackgroundDrawable(
        storeThemeEnum.getButtonLayoutDrawable(getContext().getResources(),
            getContext().getTheme()));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followStore.setElevation(0);
    }
    followStore.setTextColor(storeThemeEnum.getPrimaryColor());

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

    setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
        additionalNumberOfSharesCircularMask, displayable.getSharers()
            .size());
    setCardViewMargin(displayable, cardView);
    showSeeMoreAction(displayable);
    showSubCards(displayable, 2);
  }

  @Override String getCardTypeName() {
    return AggregatedSocialInstallDisplayable.CARD_TYPE_NAME;
  }

  private void setAdditionalNumberOfSharersLabel(TextView additionalNumberOfSharesLabel,
      ImageView additionalNumberOfSharesCircularMask, int numberOfSharers) {

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
      ImageView minimalCardHeaderMainAvatar =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_1);
      ImageView minimalCardHeaderMainAvatar2 =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_2);
      TextView minimalCardHeaderMainName = (TextView) subCardView.findViewById(R.id.card_title);
      TextView cardHeaderTimestamp = (TextView) subCardView.findViewById(R.id.card_date);
      LikeButtonView likeSubCardButton =
          (LikeButtonView) subCardView.findViewById(R.id.social_like_button);
      TextView shareSubCardButton = (TextView) subCardView.findViewById(R.id.social_share);
      LinearLayout likeLayout = (LinearLayout) subCardView.findViewById(R.id.social_like);
      TextView comment = (TextView) subCardView.findViewById(R.id.social_comment);
      LinearLayout socialInfoBar = (LinearLayout) subCardView.findViewById(R.id.social_info_bar);
      RelativeLayout likePreviewContainer = (RelativeLayout) subCardView.findViewById(
          R.id.displayable_social_timeline_likes_preview_container);
      TextView numberLikes = (TextView) subCardView.findViewById(R.id.social_number_of_likes);
      TextView numberLikesOneLike = (TextView) subCardView.findViewById(R.id.social_one_like);
      TextView numberComments = (TextView) subCardView.findViewById(R.id.social_number_of_comments);
      LinearLayout socialCommentBar =
          (LinearLayout) subCardView.findViewById(R.id.social_latest_comment_bar);
      TextView socialCommentUsername =
          (TextView) subCardView.findViewById(R.id.social_latest_comment_user_name);
      TextView socialCommentBody =
          (TextView) subCardView.findViewById(R.id.social_latest_comment_body);
      ImageView latestCommentMainAvatar =
          (ImageView) subCardView.findViewById(R.id.card_last_comment_main_icon);

      int marginOfTheNextLikePreview = 0;

      FrameLayout plusFrame = (FrameLayout) subCardView.findViewById(R.id.card_header_plus_frame);
      TextView additionalNumberOfSharesLabel = (TextView) subCardView.findViewById(
          R.id.timeline_header_aditional_number_of_shares_circular);
      ImageView additionalNumberOfSharesCircularMask =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_plus);

      if (minimalCard.getSharers()
          .get(0)
          .getUser() != null) {
        ImageLoader.with(getContext())
            .loadWithShadowCircleTransform(minimalCard.getSharers()
                .get(0)
                .getUser()
                .getAvatar(), minimalCardHeaderMainAvatar);
      }

      if (displayable.getSharers()
          .size() > 1) {
        if (displayable.getSharers()
            .get(1)
            .getUser() != null) {
          ImageLoader.with(getContext())
              .loadWithShadowCircleTransform(displayable.getSharers()
                  .get(1)
                  .getUser()
                  .getAvatar(), minimalCardHeaderMainAvatar2);
        }
      } else {
        plusFrame.setVisibility(View.GONE);
        minimalCardHeaderMainAvatar2.setVisibility(View.GONE);
      }

      minimalCardHeaderMainName.setText(getCardHeaderNames(minimalCard.getSharers()));

      cardHeaderTimestamp.setText(
          displayable.getTimeSinceLastUpdate(getContext(), minimalCard.getDate()));

      if (minimalCard.getMy()
          .isLiked()) {
        likeSubCardButton.setHeartState(true);
      } else {
        likeSubCardButton.setHeartState(false);
      }

      setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
          additionalNumberOfSharesCircularMask, minimalCard.getSharers()
              .size());

      likePreviewContainer.removeAllViews();
      showLikesPreview(likePreviewContainer, minimalCard);

      if ((minimalCard.getUsersLikes() != null
          && minimalCard.getUsersLikes()
          .size() != 0)
          || minimalCard.getStats()
          .getComments() > 0) {
        socialInfoBar.setVisibility(View.VISIBLE);
      } else {
        socialInfoBar.setVisibility(View.GONE);
      }

      final long numberOfLikes = minimalCard.getStats()
          .getLikes();
      if (numberOfLikes > 0) {
        if (numberOfLikes > 1) {
          showNumberOfLikes(numberOfLikes, numberLikes, numberLikesOneLike);
        } else if (minimalCard.getUsersLikes() != null
            && minimalCard.getUsersLikes()
            .size() != 0) {
          if (minimalCard.getUsersLikes()
              .get(0)
              .getName() != null) {
            numberLikesOneLike.setText(displayable.getBlackHighlightedLike(getContext(),
                minimalCard.getUsersLikes()
                    .get(0)
                    .getName()));
            numberLikes.setVisibility(View.INVISIBLE);
            numberLikesOneLike.setVisibility(View.VISIBLE);
          } else {
            if (minimalCard.getUsersLikes()
                .get(0)
                .getStore() != null
                && minimalCard.getUsersLikes()
                .get(0)
                .getStore()
                .getName() != null) {
              numberLikesOneLike.setText(displayable.getBlackHighlightedLike(getContext(),
                  minimalCard.getUsersLikes()
                      .get(0)
                      .getStore()
                      .getName()));
              numberLikes.setVisibility(View.INVISIBLE);
              numberLikesOneLike.setVisibility(View.VISIBLE);
            } else {
              showNumberOfLikes(numberOfLikes, numberLikes, numberLikesOneLike);
            }
          }
        }
      } else {
        numberLikes.setVisibility(View.INVISIBLE);
        numberLikesOneLike.setVisibility(View.INVISIBLE);
      }

      if (minimalCard.getStats()
          .getComments() > 0
          && minimalCard.getComments()
          .size() > 0) {
        numberComments.setVisibility(View.VISIBLE);
        numberComments.setText(getContext().getResources()
            .getQuantityString(R.plurals.timeline_short_comment, (int) minimalCard.getStats()
                .getComments(), (int) minimalCard.getStats()
                .getComments()));
        socialCommentBar.setVisibility(View.VISIBLE);
        ImageLoader.with(getContext())
            .loadWithShadowCircleTransform(minimalCard.getComments()
                .get(0)
                .getAvatar(), latestCommentMainAvatar);
        socialCommentUsername.setText(minimalCard.getComments()
            .get(0)
            .getName());
        socialCommentBody.setText(minimalCard.getComments()
            .get(0)
            .getBody());
      } else {
        numberComments.setVisibility(View.INVISIBLE);
        socialCommentBar.setVisibility(View.GONE);
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
          .subscribe(account -> {
            if (likeCard(displayable, minimalCard.getCardId(), 1)) {
              knockWithSixpackCredentials(displayable.getAbTestingURL());
              numberLikes.setText(String.valueOf(minimalCard.getStats()
                  .getLikes() + 1));
              numberLikes.setVisibility(View.VISIBLE);
              if (likePreviewContainer.getChildCount() < 4) {
                if (!minimalCard.getMy()
                    .isLiked()) {
                  UserTimeline user = new UserTimeline();
                  cm.aptoide.pt.dataprovider.model.v7.store.Store store =
                      new cm.aptoide.pt.dataprovider.model.v7.store.Store();
                  store.setAvatar(account.getStore()
                      .getAvatar());
                  user.setAvatar(account.getAvatar());
                  user.setStore(store);
                  addUserToPreview(marginOfTheNextLikePreview, user, likePreviewContainer,
                      marginOfTheNextLikePreview);
                  likePreviewContainer.invalidate();
                }
              }
            }
          }, err -> CrashReport.getInstance()
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

      compositeSubscription.add(RxView.clicks(numberComments)
          .doOnNext(click -> {
            final String elementId = minimalCard.getCardId();
            Fragment fragment = V8Engine.getFragmentProvider()
                .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);
            getFragmentNavigator().navigateTo(fragment);
          })
          .subscribe(aVoid -> knockWithSixpackCredentials(displayable.getAbTestingURL()),
              throwable -> CrashReport.getInstance()
                  .log(throwable)));

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

      compositeSubscription.add(RxView.clicks(likePreviewContainer)
          .subscribe(click -> displayable.likesPreviewClick(minimalCard.getStats()
              .getLikes(), minimalCard.getCardId()), err -> CrashReport.getInstance()
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

  private int addUserToPreview(int marginLeft, UserTimeline user, ViewGroup likePreviewContainer,
      int marginOfTheNextLikePreview) {
    View likeUserPreviewView;
    ImageView likeUserPreviewIcon;
    likeUserPreviewView =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    likeUserPreviewIcon =
        (ImageView) likeUserPreviewView.findViewById(R.id.social_timeline_like_user_preview);
    ViewGroup.MarginLayoutParams p =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView.getLayoutParams();
    p.setMargins(marginLeft, 0, 0, 0);
    likeUserPreviewView.requestLayout();

    if (user != null) {
      final FragmentActivity context = getContext();
      if (user.getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getAvatar(), likeUserPreviewIcon);
      } else if (user.getStore()
          .getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getStore()
                .getAvatar(), likeUserPreviewIcon);
      }
      likePreviewContainer.addView(likeUserPreviewView);
      return marginOfTheNextLikePreview - 20;
    } else {
      return marginOfTheNextLikePreview;
    }
  }

  private void showLikesPreview(ViewGroup likePreviewContainer, MinimalCard minimalCard) {
    int marginOfTheNextLikePreview = 60;
    for (int j = 0; j < minimalCard.getStats()
        .getLikes(); j++) {

      UserTimeline user = null;
      if (minimalCard.getUsersLikes() != null && j < minimalCard.getUsersLikes()
          .size()) {
        user = minimalCard.getUsersLikes()
            .get(j);
      }
      marginOfTheNextLikePreview =
          addUserToPreview(marginOfTheNextLikePreview, user, likePreviewContainer,
              marginOfTheNextLikePreview);
      if (marginOfTheNextLikePreview < 0) {
        break;
      }
    }
  }

  private void showNumberOfLikes(long numberOfLikes, TextView numberLikes,
      TextView numberLikesOneLike) {
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(
        getContext().getString(R.string.timeline_short_like_present_plural, numberOfLikes)
            .toLowerCase());
    numberLikesOneLike.setVisibility(View.INVISIBLE);
  }

  public String getCardHeaderNames(List<UserSharerTimeline> sharers) {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    if (sharers.size() == 1) {
      return headerNamesStringBuilder.append(sharers.get(0)
          .getStore()
          .getName())
          .toString();
    }
    List<UserSharerTimeline> firstSharers = sharers.subList(0, 2);
    for (UserSharerTimeline user : firstSharers) {
      headerNamesStringBuilder.append(user.getStore()
          .getName())
          .append(", ");
    }
    headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    return headerNamesStringBuilder.toString();
  }
}
