package cm.aptoide.pt.social.view.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.FollowStoreCardTouchEvent;
import cm.aptoide.pt.social.data.LikesPreviewCardTouchEvent;
import cm.aptoide.pt.social.data.PostPopupMenuBuilder;
import cm.aptoide.pt.social.data.SocialCardTouchEvent;
import cm.aptoide.pt.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.social.data.SocialStore;
import cm.aptoide.pt.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.timeline.view.LikeButtonView;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import java.util.HashMap;
import java.util.Map;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class SocialStoreViewHolder extends SocialPostViewHolder<SocialStore> {
  private final StoreRepository storeRepository;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final LayoutInflater inflater;
  private final ImageView headerPrimaryAvatar;
  private final ImageView headerSecondaryAvatar;
  private final TextView headerPrimaryName;
  private final TextView headerSecondaryName;
  private final TextView timestamp;
  private final TextView storeNameBodyHeader;
  private final LinearLayout appsContainer;
  private final TextView storeNameFollow;
  private final ImageView storeAvatarFollow;
  private final TextView storeNumberFollowers;
  private final TextView storeNumberApps;
  private final Button followStoreButton;
  private final RelativeLayout cardHeader;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;
  private final View overflowMenu;
  /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  private final LinearLayout socialInfoBar;
  private final TextView numberLikes;
  private final TextView numberComments;
  private final TextView numberLikesOneLike;
  private final RelativeLayout likePreviewContainer;
  private final TextView sharedBy;
  private final AptoideAccountManager accountManager;
  private int marginOfTheNextLikePreview = 60;

  /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  public SocialStoreViewHolder(View view, StoreRepository storeRepository,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, AptoideAccountManager accountManager) {
    super(view, cardTouchEventPublishSubject);
    this.storeRepository = storeRepository;
    this.accountManager = accountManager;
    this.inflater = LayoutInflater.from(itemView.getContext());
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.headerPrimaryAvatar = (ImageView) view.findViewById(R.id.card_image);
    this.headerSecondaryAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    this.headerPrimaryName = (TextView) view.findViewById(R.id.card_title);
    this.headerSecondaryName = (TextView) view.findViewById(R.id.card_subtitle);
    this.timestamp = (TextView) view.findViewById(R.id.card_date);
    this.storeNameBodyHeader = (TextView) view.findViewById(R.id.social_shared_store_name);
    this.appsContainer = (LinearLayout) view.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    this.storeAvatarFollow = (ImageView) view.findViewById(R.id.social_shared_store_avatar);
    this.storeNameFollow = (TextView) view.findViewById(R.id.store_name);
    this.storeNumberFollowers = (TextView) view.findViewById(R.id.social_number_of_followers_text);
    this.storeNumberApps = (TextView) view.findViewById(R.id.social_number_of_apps_text);
    this.followStoreButton = (Button) view.findViewById(R.id.follow_btn);
    this.cardHeader = (RelativeLayout) view.findViewById(R.id.social_header);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) itemView.findViewById(R.id.social_comment);
    this.shareButton = (TextView) itemView.findViewById(R.id.social_share);
    this.overflowMenu = itemView.findViewById(R.id.overflow_menu);
    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    this.socialInfoBar = (LinearLayout) itemView.findViewById(R.id.social_info_bar);
    this.numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    this.numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
    this.numberLikesOneLike = (TextView) itemView.findViewById(R.id.social_one_like);
    this.likePreviewContainer = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_likes_preview_container);
    this.sharedBy = (TextView) itemView.findViewById(R.id.social_shared_by);
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  }

  @Override public void setPost(SocialStore post, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(post.getPoster()
            .getPrimaryAvatar(), this.headerPrimaryAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(post.getPoster()
            .getSecondaryAvatar(), this.headerSecondaryAvatar);
    this.headerPrimaryName.setText(getStyledTitle(itemView.getContext(), post.getPoster()
        .getPrimaryName()));
    this.headerSecondaryName.setText(post.getPoster()
        .getSecondaryName());
    this.timestamp.setText(
        dateCalculator.getTimeSinceDate(itemView.getContext(), post.getLatestUpdate()));
    this.storeNameBodyHeader.setText(post.getStoreName());
    ImageLoader.with(itemView.getContext())
        .load(post.getStoreAvatar(), storeAvatarFollow);
    this.storeNameFollow.setText(post.getStoreName());
    this.storeNumberFollowers.setText(String.valueOf(post.getSubscribers()));
    this.storeNumberApps.setText(String.valueOf(post.getAppsNumber()));
    this.cardHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new SocialHeaderCardTouchEvent(post, post.getPoster()
            .getStore()
            .getName(), post.getPoster()
            .getStore()
            .getStoreTheme(), post.getPoster()
            .getUser()
            .getId(), CardTouchEvent.Type.HEADER, position)));
    showStoreLatestApps(post, position);
    showFollowButton(post);
    this.followStoreButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new FollowStoreCardTouchEvent(post, post.getStoreId(), post.getStoreName(),
            CardTouchEvent.Type.BODY, position)));
    this.storeAvatarFollow.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new StoreCardTouchEvent(post, post.getStoreName(), post.getStoreTheme(),
            CardTouchEvent.Type.BODY, position)));
    if (post.isLiked()) {
      if (post.isLikeFromClick()) {
        likeButton.setHeartState(true);
        post.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }
    if (post.getSharedByName() != null) {
      sharedBy.setText(spannableFactory.createColorSpan(itemView.getContext()
              .getString(R.string.social_timeline_shared_by, post.getSharedByName()),
          ContextCompat.getColor(itemView.getContext(), R.color.black), post.getSharedByName()));
      sharedBy.setVisibility(View.VISIBLE);
    } else {
      sharedBy.setVisibility(View.GONE);
    }

    setupOverflowMenu(post, position);

    /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    showSocialInformationBar(post, position);
    showLikesPreview(post);
    /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(post, CardTouchEvent.Type.LIKE, position)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new SocialCardTouchEvent(post, CardTouchEvent.Type.COMMENT, position)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.SHARE)));
    this.likePreviewContainer.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikesPreviewCardTouchEvent(post, post.getLikesNumber(),
            CardTouchEvent.Type.LIKES_PREVIEW, position)));
    this.numberLikes.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikesPreviewCardTouchEvent(post, post.getLikesNumber(),
            CardTouchEvent.Type.LIKES_PREVIEW, position)));
    this.numberLikesOneLike.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikesPreviewCardTouchEvent(post, post.getLikesNumber(),
            CardTouchEvent.Type.LIKES_PREVIEW, position)));
    this.numberComments.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.COMMENT_NUMBER)));
  }

  private void showFollowButton(SocialStore post) {
    storeRepository.isSubscribed(post.getStoreId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            followStoreButton.setText(R.string.followed);
          } else {
            followStoreButton.setText(R.string.follow);
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_share_past_singular, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  private void showStoreLatestApps(SocialStore card, int position) {
    Map<View, Long> apps = new HashMap<>();
    LongSparseArray<String> appsPackages = new LongSparseArray<>();

    appsContainer.removeAllViews();
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (App latestApp : card.getApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(itemView.getContext())
          .load(latestApp.getIcon(), latestAppIcon);
      latestAppName.setText(latestApp.getName());
      appsContainer.addView(latestAppView);
      apps.put(latestAppView, latestApp.getId());
      appsPackages.put(latestApp.getId(), latestApp.getPackageName());
    }

    setStoreLatestAppsListeners(card, apps, appsPackages, position);
  }

  /* START - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */
  private void showSocialInformationBar(SocialStore card, int position) {
    if (card.getLikesNumber() > 0 || card.getCommentsNumber() > 0) {
      socialInfoBar.setVisibility(View.VISIBLE);
    } else {
      socialInfoBar.setVisibility(View.GONE);
    }

    handleLikesInformation(card);
    handleCommentsInformation(card, position);
  }

  private void showLikesPreview(SocialStore post) {
    likePreviewContainer.removeAllViews();
    marginOfTheNextLikePreview = 60;
    for (int j = 0; j < post.getLikesNumber(); j++) {

      UserTimeline user = null;
      if (post.getLikes() != null && j < post.getLikes()
          .size()) {
        user = post.getLikes()
            .get(j);
      }
      addUserToPreview(marginOfTheNextLikePreview, user);
      if (marginOfTheNextLikePreview < 0) {
        break;
      }
    }
  }

  private void setStoreLatestAppsListeners(SocialStore card, Map<View, Long> apps,
      LongSparseArray<String> appsPackages, int position) {
    for (View app : apps.keySet()) {
      app.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
          new StoreAppCardTouchEvent(card, CardTouchEvent.Type.BODY,
              appsPackages.get(apps.get(app)), position)));
    }
  }

  private void handleLikesInformation(SocialStore card) {
    if (card.getLikesNumber() > 0) {
      if (card.getLikesNumber() > 1) {
        showNumberOfLikes(card.getLikesNumber());
      } else if (card.getLikes() != null
          && card.getLikes()
          .size() != 0) {
        String firstLikeName = card.getLikes()
            .get(0)
            .getName();
        if (firstLikeName != null) {
          numberLikesOneLike.setText(spannableFactory.createColorSpan(itemView.getContext()
                  .getString(R.string.timeline_short_like_present_singular, firstLikeName),
              ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
              firstLikeName));
          numberLikes.setVisibility(View.INVISIBLE);
          numberLikesOneLike.setVisibility(View.VISIBLE);
        } else {
          String firstStoreName = card.getLikes()
              .get(0)
              .getStore()
              .getName();
          if (card.getLikes()
              .get(0)
              .getStore() != null && firstStoreName != null) {
            numberLikesOneLike.setText(spannableFactory.createColorSpan(itemView.getContext()
                    .getString(R.string.timeline_short_like_present_singular, firstStoreName),
                ContextCompat.getColor(itemView.getContext(), R.color.black_87_alpha),
                firstStoreName));
            numberLikes.setVisibility(View.INVISIBLE);
            numberLikesOneLike.setVisibility(View.VISIBLE);
          } else {
            showNumberOfLikes(card.getLikesNumber());
          }
        }
      }
    } else {
      numberLikes.setVisibility(View.INVISIBLE);
      numberLikesOneLike.setVisibility(View.INVISIBLE);
    }
  }

  private void addUserToPreview(int i, UserTimeline user) {
    View likeUserPreviewView;
    ImageView likeUserPreviewIcon;
    likeUserPreviewView =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    likeUserPreviewIcon =
        (ImageView) likeUserPreviewView.findViewById(R.id.social_timeline_like_user_preview);
    ViewGroup.MarginLayoutParams p =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView.getLayoutParams();
    p.setMargins(i, 0, 0, 0);
    likeUserPreviewView.requestLayout();

    if (user != null) {
      if (user.getAvatar() != null) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(user.getAvatar(), likeUserPreviewIcon);
      } else if (user.getStore()
          .getAvatar() != null) {
        ImageLoader.with(itemView.getContext())
            .loadWithShadowCircleTransform(user.getStore()
                .getAvatar(), likeUserPreviewIcon);
      }
      likePreviewContainer.addView(likeUserPreviewView);
      marginOfTheNextLikePreview -= 20;
    }
  }
  /* END - SOCIAL INFO COMMON TO ALL SOCIAL CARDS */

  private void showNumberOfLikes(long likesNumber) {
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(itemView.getContext()
        .getString(R.string.timeline_short_like_present_plural, likesNumber)
        .toLowerCase());
    numberLikesOneLike.setVisibility(View.INVISIBLE);
  }

  private void setupOverflowMenu(SocialStore post, int position) {
    overflowMenu.setOnClickListener(view -> {
      PostPopupMenuBuilder postPopupMenuBuilder = new PostPopupMenuBuilder();
      postPopupMenuBuilder.prepMenu(itemView.getContext(), overflowMenu)
          .addReportAbuse(menuItem -> {
            cardTouchEventPublishSubject.onNext(
                new CardTouchEvent(post, position, CardTouchEvent.Type.REPORT_ABUSE));
            return false;
          })
          .addUnfollow(menuItem -> {
            cardTouchEventPublishSubject.onNext(
                new CardTouchEvent(post, position, CardTouchEvent.Type.UNFOLLOW_STORE));
            return false;
          });
      accountManager.accountStatus()
          .first()
          .toSingle()
          .subscribe(account -> {
            if (post.getPoster()
                .isMe(account.getNickname(), account.getStore()
                    .getName())) {
              postPopupMenuBuilder.addItemDelete(menuItem -> {
                cardTouchEventPublishSubject.onNext(
                    new CardTouchEvent(post, position, CardTouchEvent.Type.DELETE_POST));
                return false;
              });
            }
          }, throwable -> CrashReport.getInstance()
              .log(throwable));
      postPopupMenuBuilder.getPopupMenu()
          .show();
    });
  }
}
