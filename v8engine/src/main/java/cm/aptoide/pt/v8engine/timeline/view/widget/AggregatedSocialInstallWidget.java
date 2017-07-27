package cm.aptoide.pt.v8engine.timeline.view.widget;

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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallWidget extends CardWidget<AggregatedSocialInstallDisplayable> {
  private final LayoutInflater inflater;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private TextView seeMore;
  private LinearLayout subCardsContainer;
  private ImageView headerAvatar1;
  private ImageView headerAvatar2;
  private TextView headerNames;
  private TextView headerTime;
  private ImageView appIcon;
  private TextView appName;
  private RatingBar ratingBar;
  private Button getAppButton;
  private CardView cardView;
  private TextView additionalNumberOfSharesLabel;
  private ImageView additionalNumberOfSharesCircularMask;

  public AggregatedSocialInstallWidget(View itemView) {
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
    appIcon =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    appName = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_similar_apps);
    ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
    getAppButton = (Button) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    additionalNumberOfSharesCircularMask =
        (ImageView) itemView.findViewById(R.id.card_header_avatar_plus);
    additionalNumberOfSharesLabel =
        (TextView) itemView.findViewById(R.id.timeline_header_aditional_number_of_shares_circular);
  }

  @Override public void bindView(AggregatedSocialInstallDisplayable displayable) {
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
    ImageLoader.with(getContext())
        .load(displayable.getAppIcon(), appIcon);
    appName.setText(displayable.getAppName());
    ratingBar.setRating(displayable.getAppRatingAverage());
    setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
        additionalNumberOfSharesCircularMask, displayable.getSharers()
            .size());
    setCardViewMargin(displayable, cardView);
    showSeeMoreAction(displayable);
    showSubCards(displayable, 2);

    RxView.clicks(getAppButton)
        .subscribe(view -> {
          knockWithSixpackCredentials(displayable.getAbTestingURL());

          Analytics.AppsTimeline.clickOnCard(AggregatedSocialInstallDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK,
              Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.OPEN_APP_VIEW);
          displayable.sendOpenAppEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
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

  private void showSubCards(AggregatedSocialInstallDisplayable displayable,
      int numberOfSubCardsToShow) {
    subCardsContainer.removeAllViews();
    int i = 1;
    for (MinimalCard minimalCard : displayable.getMinimalCardList()) {
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

      setAdditionalNumberOfSharersLabel(additionalNumberOfSharesLabel,
          additionalNumberOfSharesCircularMask, minimalCard.getSharers()
              .size());

      compositeSubscription.add(RxView.clicks(shareSubCardButton)
          .subscribe(click -> shareCard(displayable, minimalCard.getCardId(), null,
              SharePreviewDialog.SharePreviewOpenMode.SHARE), err -> CrashReport.getInstance()
              .log(err)));

      compositeSubscription.add(RxView.clicks(seeMore)
          .subscribe(click -> {
            showSubCards(displayable, 10);
            seeMore.setVisibility(View.GONE);
          }, throwable -> CrashReport.getInstance()
              .log(throwable)));

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

  private void showSeeMoreAction(AggregatedSocialInstallDisplayable displayable) {
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
