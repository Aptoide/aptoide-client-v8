package cm.aptoide.pt.v8engine.social.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.view.viewholder.AggregatedMediaViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.AggregatedRecommendationViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.AggregatedStoreViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.AppUpdateViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.MediaViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.PopularAppViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.PostViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.ProgressViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.RecommendationViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.SocialMediaViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.SocialRecommendationViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.SocialStoreViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.StoreLatestAppsViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.TimelineLoginPostViewHolder;
import cm.aptoide.pt.v8engine.social.view.viewholder.TimelineStatsViewHolder;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 08/06/2017.
 */

public class CardViewHolderFactory {

  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final MinimalCardViewFactory minimalCardViewFactory;

  public CardViewHolderFactory(PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      MinimalCardViewFactory minimalCardViewFactory) {
    this.minimalCardViewFactory = minimalCardViewFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
  }

  public PostViewHolder createViewHolder(int cardViewType, ViewGroup parent) {
    if (cardViewType > CardType.values().length) {
      throw new IllegalStateException("Wrong card type " + cardViewType);
    }
    CardType cardType = CardType.values()[cardViewType];
    switch (cardType) {
      case ARTICLE:
      case VIDEO:
        return new MediaViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_media_item, parent, false), cardTouchEventPublishSubject,
            dateCalculator, spannableFactory);
      case SIMILAR:
      case RECOMMENDATION:
        return new RecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_recommendation_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory);
      case STORE:
        return new StoreLatestAppsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_store_item, parent, false), cardTouchEventPublishSubject,
            dateCalculator, spannableFactory);
      case UPDATE:
        return new AppUpdateViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_update_item, parent, false), cardTouchEventPublishSubject,
            dateCalculator, spannableFactory);
      case POPULAR_APP:
        return new PopularAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_popular_app_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator);
      case SOCIAL_RECOMMENDATION:
      case SOCIAL_INSTALL:
        return new SocialRecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_social_recommendation_item, parent, false),
            cardType.equals(CardType.SOCIAL_RECOMMENDATION)
                ? R.string.timeline_title_card_title_recommend_present_singular
                : R.string.timeline_title_card_title_install_past_singular,
            cardTouchEventPublishSubject, dateCalculator, spannableFactory);
      case SOCIAL_ARTICLE:
      case SOCIAL_VIDEO:
        return new SocialMediaViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_social_media_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory);
      case SOCIAL_STORE:
        return new SocialStoreViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_social_store_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory);
      case AGGREGATED_SOCIAL_ARTICLE:
      case AGGREGATED_SOCIAL_VIDEO:
        return new AggregatedMediaViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_aggregated_media_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, minimalCardViewFactory);
      case AGGREGATED_SOCIAL_INSTALL:
        return new AggregatedRecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_aggregated_recommendation_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, minimalCardViewFactory);
      case AGGREGATED_SOCIAL_STORE:
        return new AggregatedStoreViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_aggregated_store_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, minimalCardViewFactory);
      case PROGRESS:
        return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_progress_item, parent, false));
      case TIMELINE_STATS:
        return new TimelineStatsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_stats_item, parent, false), spannableFactory,
            cardTouchEventPublishSubject);
      case LOGIN:
        return new TimelineLoginPostViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_login_item, parent, false), cardTouchEventPublishSubject);
      default:
        throw new IllegalStateException("Wrong cardType" + cardType.name());
    }
  }
}
