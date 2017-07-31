package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.AppUpdate;
import cm.aptoide.pt.v8engine.social.data.AppUpdateCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.LikeCardTouchEvent;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 22/06/2017.
 */

public class AppUpdateViewHolder extends PostViewHolder<AppUpdate> {
  private final DateCalculator dateCalculator;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  private final ImageView appIcon;
  private final TextView appName;
  private final Button appUpdate;
  private final SpannableFactory spannableFactory;
  private final TextView errorText;
  private final RelativeLayout cardHeader;
  private final LinearLayout like;
  private final LikeButtonView likeButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final TextView commentButton;
  private final TextView shareButton;

  public AppUpdateViewHolder(View view, PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory) {
    super(view);
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.headerIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_app_update_card_image);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_app_update_card_title);
    this.headerSubTitle = (TextView) view.findViewById(
        R.id.displayable_social_timeline_app_update_card_card_subtitle);
    this.appIcon = (ImageView) view.findViewById(R.id.displayable_social_timeline_app_update_icon);
    this.appName = (TextView) view.findViewById(R.id.displayable_social_timeline_app_update_name);
    this.appUpdate =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
    this.errorText =
        (TextView) view.findViewById(R.id.displayable_social_timeline_app_update_error);
    this.cardHeader =
        (RelativeLayout) view.findViewById(R.id.displayable_social_timeline_app_update_header);
    this.likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    this.like = (LinearLayout) itemView.findViewById(R.id.social_like);
    this.commentButton = (TextView) itemView.findViewById(R.id.social_comment);
    this.shareButton = (TextView) itemView.findViewById(R.id.social_share);
  }

  @Override public void setPost(AppUpdate card, int position) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getStoreAvatar(), headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext(), card.getStoreName()));
    this.headerSubTitle.setText(
        getTimeSinceLastUpdate(itemView.getContext(), card.getUpdateAddedDate()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppUpdateIcon(), appIcon);
    this.appName.setText(getAppTitle(itemView.getContext(), card.getAppUpdateName()));
    setAppUpdateButtonText(card);
    this.errorText.setVisibility(View.GONE);
    this.appUpdate.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new AppUpdateCardTouchEvent(card, CardTouchEvent.Type.BODY, position)));
    this.appIcon.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.cardHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.HEADER)));
    if (card.isLiked()) {
      if (card.isLikeFromClick()) {
        likeButton.setHeartState(true);
        card.setLikedFromClick(false);
      } else {
        likeButton.setHeartStateWithoutAnimation(true);
      }
    } else {
      likeButton.setHeartState(false);
    }
    this.like.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new LikeCardTouchEvent(card, CardTouchEvent.Type.LIKE, position)));

    this.commentButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.COMMENT)));
    this.shareButton.setOnClickListener(click -> this.cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.SHARE)));
  }

  private Spannable getStyledTitle(Context context, String storeName) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_has_update_present_singular,
            storeName), ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }

  public String getTimeSinceLastUpdate(Context context, Date updatedDate) {
    return dateCalculator.getTimeSinceDate(context, updatedDate);
  }

  private Spannable getAppTitle(Context context, String appUpdateName) {
    return spannableFactory.createColorSpan(appUpdateName,
        ContextCompat.getColor(context, R.color.black), appUpdateName);
  }

  private void setAppUpdateButtonText(AppUpdate card) {
    if (card.getInstallationStatus()
        .equals(Install.InstallationStatus.UNINSTALLED) || card.getInstallationStatus()
        .equals(Install.InstallationStatus.PAUSED)) {
      this.appUpdate.setText(getUpdateAppText(itemView.getContext()).toString()
          .toUpperCase());
    } else if (card.getInstallationStatus()
        .equals(Install.InstallationStatus.INSTALLING)) {
      this.appUpdate.setText(itemView.getContext()
          .getString(R.string.displayable_social_timeline_app_update_updating));
    } else if (card.getInstallationStatus()
        .equals(Install.InstallationStatus.INSTALLED)) {
      this.appUpdate.setText(itemView.getContext()
          .getString(R.string.displayable_social_timeline_app_update_updated));
    } else if (card.getInstallationStatus()
        .equals(Install.InstallationStatus.GENERIC_ERROR)) {
      this.errorText.setText(R.string.displayable_social_timeline_app_update_error);
      this.errorText.setVisibility(View.VISIBLE);
      this.appUpdate.setText(getUpdateAppText(itemView.getContext()).toString()
          .toUpperCase());
    }
  }

  private Spannable getUpdateAppText(Context context) {
    String application = context.getString(R.string.appstimeline_update_app);
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_app_update_button, application),
        Typeface.NORMAL, application);
  }
}
