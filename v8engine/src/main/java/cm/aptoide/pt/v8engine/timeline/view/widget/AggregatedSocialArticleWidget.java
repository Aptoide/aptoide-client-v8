package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialArticleDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialArticleWidget extends CardWidget<AggregatedSocialArticleDisplayable> {
  private final LayoutInflater inflater;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private TextView seeMore;
  private LinearLayout subCardsContainer;
  private ImageView headerAvatar1;
  private ImageView headerAvatar2;
  private TextView headerNames;
  private TextView headerTime;
  private TextView articleTitle;
  private ImageView thumbnail;
  private View url;
  private CardView cardView;
  private TextView relatedTo;
  private String appName;
  private RatingBar ratingBar;
  private TextView additionalNumberOfSharesLabel;
  private ImageView additionalNumberOfSharesCircularMask;

  public AggregatedSocialArticleWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
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
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    cardView = (CardView) itemView.findViewById(R.id.card);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
    additionalNumberOfSharesCircularMask =
        (ImageView) itemView.findViewById(R.id.card_header_avatar_plus);
    additionalNumberOfSharesLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
  }

  @Override public void bindView(AggregatedSocialArticleDisplayable displayable) {
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

    articleTitle.setText(displayable.getTitle());

    ImageLoader.with(getContext())
        .load(displayable.getThumbnailUrl(), thumbnail);
    thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);

    url.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbTestingURL());
      displayable.getLink()
          .launch();
      Analytics.AppsTimeline.clickOnCard(SocialArticleDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_ARTICLE);
      displayable.sendOpenArticleEvent();
    });

    ratingBar.setVisibility(View.INVISIBLE);

    setCardViewMargin(displayable, cardView);

    setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
        additionalNumberOfSharesCircularMask, displayable.getSharers()
            .size());
    showSeeMoreAction(displayable);
    showSubCards(displayable, 2);
  }

  @Override String getCardTypeName() {
    return AggregatedSocialArticleDisplayable.CARD_TYPE_NAME;
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

  private void showSubCards(AggregatedSocialArticleDisplayable displayable,
      int numberOfCardsToShow) {
    subCardsContainer.removeAllViews();
    int i = 1;
    for (MinimalCard minimalCard : displayable.getMinimalCardList()) {
      if (i > numberOfCardsToShow) {
        break;
      }
      View subCardView =
          inflater.inflate(R.layout.timeline_sub_minimal_card, subCardsContainer, false);
      TextView minimalCardHeaderMainName = (TextView) subCardView.findViewById(R.id.card_title);
      ImageView minimalCardHeaderMainAvatar =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_1);
      ImageView minimalCardHeaderMainAvatar2 =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_2);
      TextView cardHeaderTimestamp = (TextView) subCardView.findViewById(R.id.card_date);
      TextView comment = (TextView) subCardView.findViewById(R.id.social_comment);
      TextView shareSubCardButton = (TextView) subCardView.findViewById(R.id.social_share);
      LikeButtonView likeSubCardButton =
          (LikeButtonView) subCardView.findViewById(R.id.social_like_button);
      LinearLayout likeLayout = (LinearLayout) subCardView.findViewById(R.id.social_like);
      FrameLayout plusFrame = (FrameLayout) subCardView.findViewById(R.id.card_header_plus_frame);
      TextView additionalNumberOfSharesLabel = (TextView) subCardView.findViewById(
          R.id.timeline_header_aditional_number_of_shares_circular);
      ImageView additionalNumberOfSharesCircularMask =
          (ImageView) subCardView.findViewById(R.id.card_header_avatar_plus);
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

      ImageLoader.with(getContext())
          .loadWithShadowCircleTransform(minimalCard.getSharers()
              .get(0)
              .getUser()
              .getAvatar(), minimalCardHeaderMainAvatar);

      if (minimalCard.getSharers()
          .size() > 1) {
        ImageLoader.with(getContext())
            .loadWithShadowCircleTransform(minimalCard.getSharers()
                .get(1)
                .getUser()
                .getAvatar(), minimalCardHeaderMainAvatar2);
      } else {
        plusFrame.setVisibility(View.GONE);
        minimalCardHeaderMainAvatar2.setVisibility(View.GONE);
      }

      minimalCardHeaderMainName.setText(getCardHeaderNames(minimalCard.getSharers()));

      cardHeaderTimestamp.setText(
          displayable.getTimeSinceLastUpdate(getContext(), minimalCard.getDate()));

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

      setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
          additionalNumberOfSharesCircularMask, minimalCard.getSharers()
              .size());

      compositeSubscription.add(displayable.getRelatedToApplication()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(installeds -> {
            if (installeds != null && !installeds.isEmpty()) {
              appName = installeds.get(0)
                  .getName();
            } else {
              setAppNameToFirstLinkedApp(displayable);
            }
            if (appName != null) {
              relatedTo.setTextSize(11);
              relatedTo.setText(displayable.getAppRelatedToText(getContext(), appName));
            }
          }, throwable -> {
            setAppNameToFirstLinkedApp(displayable);
            if (appName != null) {
              relatedTo.setTextSize(11);
              relatedTo.setText(displayable.getAppRelatedToText(getContext(), appName));
            }
            throwable.printStackTrace();
          }));

      if (minimalCard.getMy()
          .isLiked()) {
        likeSubCardButton.setHeartState(true);
      } else {
        likeSubCardButton.setHeartState(false);
      }

      if (minimalCard.getStats()
          .getComments() > 0
          && minimalCard.getComments()
          .size() > 0) {
        numberComments.setVisibility(View.VISIBLE);
        numberComments.setText(String.format("%s %s", String.valueOf(minimalCard.getStats()
            .getComments()), getContext().getString(R.string.comments)
            .toLowerCase()));
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
                  Store store = new Store();
                  store.setAvatar(account.getStoreAvatar());
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

      compositeSubscription.add(RxView.clicks(shareSubCardButton)
          .subscribe(click -> shareCard(displayable, minimalCard.getCardId(), null,
              SharePreviewDialog.SharePreviewOpenMode.SHARE), err -> CrashReport.getInstance()
              .log(err)));

      compositeSubscription.add(accountManager.accountStatus()
          .subscribe());
      likeLayout.setVisibility(View.VISIBLE);
      comment.setVisibility(View.VISIBLE);

      compositeSubscription.add(RxView.clicks(seeMore)
          .subscribe(click -> {
            showSubCards(displayable, 10);
            seeMore.setVisibility(View.GONE);
          }, throwable -> CrashReport.getInstance()
              .log(throwable)));

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

  private void setAppNameToFirstLinkedApp(AggregatedSocialArticleDisplayable displayable) {
    if (!displayable.getRelatedToAppsList()
        .isEmpty()) {
      appName = displayable.getRelatedToAppsList()
          .get(0)
          .getName();
    }
  }

  private void showSeeMoreAction(AggregatedSocialArticleDisplayable displayable) {
    if (displayable.getMinimalCardList()
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
    numberLikes.setText(String.format("%s %s", String.valueOf(numberOfLikes),
        getContext().getString(R.string.likes)
            .toLowerCase()));
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
