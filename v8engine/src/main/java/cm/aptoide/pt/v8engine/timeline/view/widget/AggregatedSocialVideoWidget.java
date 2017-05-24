package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialVideoWidget extends CardWidget<AggregatedSocialVideoDisplayable> {
  private final LayoutInflater inflater;
  private final AptoideAccountManager accountManager;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private TextView seeMore;
  private LinearLayout subCardsContainer;
  private ImageView headerAvatar1;
  private ImageView headerAvatar2;
  private TextView headerNames;
  private TextView headerTime;
  private TextView videoTitle;
  private ImageView thumbnail;
  private ImageView play_button;
  private FrameLayout mediaLayout;
  private View url;
  private CardView cardView;
  private TextView relatedTo;
  private String appName;
  private RatingBar ratingBar;

  public AggregatedSocialVideoWidget(View itemView) {
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
    videoTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    play_button = (ImageView) itemView.findViewById(R.id.play_button);
    mediaLayout = (FrameLayout) itemView.findViewById(R.id.media_layout);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    cardView = (CardView) itemView.findViewById(R.id.card);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
  }

  @Override public void bindView(AggregatedSocialVideoDisplayable displayable) {
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

    ratingBar.setVisibility(View.INVISIBLE);
    videoTitle.setText(displayable.getTitle());
    setCardViewMargin(displayable, cardView);
    ImageLoader.with(getContext())
        .load(displayable.getThumbnailUrl(), thumbnail);
    thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
    play_button.setVisibility(View.VISIBLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mediaLayout.setForeground(getContext().getResources()
          .getDrawable(R.color.overlay_black, getContext().getTheme()));
    } else {
      mediaLayout.setForeground(getContext().getResources()
          .getDrawable(R.color.overlay_black));
    }

    compositeSubscription.add(RxView.clicks(mediaLayout)
        .subscribe(v -> {
          knockWithSixpackCredentials(displayable.getAbTestingURL());
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              displayable.getTitle(), displayable.getTitle(), Analytics.AppsTimeline.OPEN_VIDEO);
          displayable.getLink()
              .launch();
          displayable.sendOpenVideoEvent();
        }));

    setCardViewMargin(displayable, cardView);

    showSeeMoreAction(displayable);
    showSubCards(displayable, 2);
  }

  @Override String getCardTypeName() {
    return AggregatedSocialVideoDisplayable.CARD_TYPE_NAME;
  }

  private void showSubCards(AggregatedSocialVideoDisplayable displayable,
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
            final String elementId = displayable.getTimelineCard()
                .getCardId();
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

  private void setAppNameToFirstLinkedApp(AggregatedSocialVideoDisplayable displayable) {
    if (!displayable.getRelatedToApps()
        .isEmpty()) {
      appName = displayable.getRelatedToApps()
          .get(0)
          .getName();
    }
  }

  private void showSeeMoreAction(AggregatedSocialVideoDisplayable displayable) {
    if (displayable.getMinimalCards()
        .size() > 2) {
      seeMore.setVisibility(View.VISIBLE);
    } else {
      seeMore.setVisibility(View.GONE);
    }
  }
}
