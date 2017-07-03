package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.CardType;
import cm.aptoide.pt.v8engine.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.SocialMedia;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 27/06/2017.
 */

public class SocialMediaViewHolder extends CardViewHolder<SocialMedia> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final ImageView headerPrimaryAvatar;
  private final ImageView headerSecondaryAvatar;
  private final TextView headerPrimaryName;
  private final TextView headerSecondaryName;
  private final TextView timestamp;
  private final TextView mediaTitle;
  private final ImageView mediaThumbnail;
  private final TextView relatedTo;
  private final ImageView playIcon;
  private final RelativeLayout cardHeader;

  public SocialMediaViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;

    this.headerPrimaryAvatar = (ImageView) view.findViewById(R.id.card_image);
    this.headerSecondaryAvatar = (ImageView) view.findViewById(R.id.card_user_avatar);
    this.headerPrimaryName = (TextView) view.findViewById(R.id.card_title);
    this.headerSecondaryName = (TextView) view.findViewById(R.id.card_subtitle);
    this.timestamp = (TextView) view.findViewById(R.id.card_date);
    this.mediaTitle =
        (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    this.mediaThumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    this.relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    this.playIcon = (ImageView) itemView.findViewById(R.id.play_button);
    this.cardHeader = (RelativeLayout) view.findViewById(R.id.social_header);
  }

  @Override public void setCard(SocialMedia card, int position) {
    if (card.getType()
        .equals(CardType.SOCIAL_ARTICLE)) {
      this.playIcon.setVisibility(View.GONE);
    } else if (card.getType()
        .equals(CardType.SOCIAL_VIDEO)) {
      this.playIcon.setVisibility(View.VISIBLE);
    }
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getPrimaryAvatar(), headerPrimaryAvatar);
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(card.getPoster()
            .getSecondaryAvatar(), headerSecondaryAvatar);
    this.headerPrimaryName.setText(getStyledTitle(itemView.getContext(), card.getPoster()
        .getPrimaryName()));
    showHeaderSecondaryName(card);
    this.timestamp.setText(dateCalculator.getTimeSinceDate(itemView.getContext(), card.getDate()));
    this.mediaTitle.setText(card.getMediaTitle());
    ImageLoader.with(itemView.getContext())
        .loadWithCenterCrop(card.getMediaThumbnailUrl(), mediaThumbnail);
    this.relatedTo.setText(spannableFactory.createStyleSpan(itemView.getContext()
        .getString(R.string.displayable_social_timeline_article_related_to, card.getRelatedApp()
            .getName()), Typeface.BOLD, card.getRelatedApp()
        .getName()));
    this.mediaThumbnail.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
    this.cardHeader.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new SocialHeaderCardTouchEvent(card, card.getPoster()
            .getStore()
            .getName(), card.getPoster()
            .getStore()
            .getStoreTheme(), card.getPoster()
            .getUser()
            .getId(), CardTouchEvent.Type.HEADER)));
  }

  private void showHeaderSecondaryName(SocialMedia card) {
    if (TextUtils.isEmpty(card.getPoster()
        .getSecondaryName())) {
      this.headerSecondaryName.setVisibility(View.GONE);
    } else {
      this.headerSecondaryName.setText(card.getPoster()
          .getSecondaryName());
      this.headerSecondaryName.setVisibility(View.VISIBLE);
    }
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(context.getString(R.string.x_shared, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }
}
