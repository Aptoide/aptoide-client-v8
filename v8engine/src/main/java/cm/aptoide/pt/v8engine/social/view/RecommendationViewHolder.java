package cm.aptoide.pt.v8engine.social.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.image.ImageLoader;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 19/06/2017.
 */

public class RecommendationViewHolder extends CardViewHolder<Recommendation> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final ImageView headerIcon;
  private final TextView headerTitle;
  private final TextView headerSubTitle;
  private final ImageView appIcon;
  private final TextView appName;
  private final TextView relatedToText;
  private final TextView relatedToApp;
  private final Button getAppButton;

  public RecommendationViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject, DateCalculator dateCalculator,
      SpannableFactory spannableFactory) {
    super(view);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;

    this.headerIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_icon);
    this.headerTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
    this.headerSubTitle =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_card_subtitle);
    this.appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    this.appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_name);
    this.relatedToText =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    this.relatedToApp =
        (TextView) view.findViewById(R.id.social_timeline_recommendation_card_related_to_app);
    this.getAppButton =
        (Button) view.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);
  }

  @Override void setCard(Recommendation card) {
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(Application.getConfiguration()
            .getIcon(), headerIcon);
    this.headerTitle.setText(getStyledTitle(itemView.getContext()));
    this.headerSubTitle.setText(
        getTimeSinceRecommendation(itemView.getContext(), card.getTimestamp()));
    ImageLoader.with(itemView.getContext())
        .load(card.getAppIcon(), appIcon);
    this.appName.setText(card.getAppName());
    this.relatedToText.setText(itemView.getContext()
        .getString(R.string.related_to)
        .toLowerCase());
    this.relatedToApp.setText(card.getRelatedToAppName());

    this.getAppButton.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.BODY)));
  }

  public String getTimeSinceRecommendation(Context context, Date timestamp) {
    return dateCalculator.getTimeSinceDate(context, timestamp);
  }

  private Spannable getStyledTitle(Context context) {
    String aptoide = Application.getConfiguration()
        .getMarketName();
    return spannableFactory.createColorSpan(getTitle(context.getResources()),
        ContextCompat.getColor(context, R.color.appstimeline_recommends_title), aptoide);
  }

  public String getTitle(Resources resources) {
    return AptoideUtils.StringU.getFormattedString(
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends, resources,
        Application.getConfiguration()
            .getMarketName());
  }
}
