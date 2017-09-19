package cm.aptoide.pt.social.data;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.view.viewholder.AggregatedMediaViewHolder;
import cm.aptoide.pt.social.view.viewholder.AggregatedRecommendationViewHolder;
import cm.aptoide.pt.social.view.viewholder.AggregatedStoreViewHolder;
import cm.aptoide.pt.social.view.viewholder.AppUpdateViewHolder;
import cm.aptoide.pt.social.view.viewholder.MediaViewHolder;
import cm.aptoide.pt.social.view.viewholder.Notifications;
import cm.aptoide.pt.social.view.viewholder.PopularAppViewHolder;
import cm.aptoide.pt.social.view.viewholder.PostViewHolder;
import cm.aptoide.pt.social.view.viewholder.ProgressViewHolder;
import cm.aptoide.pt.social.view.viewholder.RecommendationViewHolder;
import cm.aptoide.pt.social.view.viewholder.SocialMediaViewHolder;
import cm.aptoide.pt.social.view.viewholder.SocialPostMediaViewHolder;
import cm.aptoide.pt.social.view.viewholder.SocialPostRecommendationViewHolder;
import cm.aptoide.pt.social.view.viewholder.SocialRecommendationViewHolder;
import cm.aptoide.pt.social.view.viewholder.SocialStoreViewHolder;
import cm.aptoide.pt.social.view.viewholder.StoreLatestAppsViewHolder;
import cm.aptoide.pt.social.view.viewholder.TimelineAdPostViewHolder;
import cm.aptoide.pt.social.view.viewholder.TimelineLoginPostViewHolder;
import cm.aptoide.pt.social.view.viewholder.TimelineNoNotificationHeaderViewHolder;
import cm.aptoide.pt.social.view.viewholder.TimelineStatsViewHolder;
import cm.aptoide.pt.util.DateCalculator;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 08/06/2017.
 */

public class CardViewHolderFactory {

  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final DateCalculator dateCalculator;
  private final SpannableFactory spannableFactory;
  private final MinimalCardViewFactory minimalCardViewFactory;
  private final String marketName;
  private StoreContext storeContext;

  public CardViewHolderFactory(PublishSubject<CardTouchEvent> cardTouchEventPublishSubject,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      MinimalCardViewFactory minimalCardViewFactory, String marketName, StoreContext storeContext) {
    this.minimalCardViewFactory = minimalCardViewFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.marketName = marketName;
    this.storeContext = storeContext;
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
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, marketName);
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
      case AGGREGATED_SOCIAL_APP:
      case AGGREGATED_SOCIAL_INSTALL:
        return new AggregatedRecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_aggregated_recommendation_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, minimalCardViewFactory);
      case AGGREGATED_SOCIAL_STORE:
        return new AggregatedStoreViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_aggregated_store_item, parent, false),
            cardTouchEventPublishSubject, dateCalculator, spannableFactory, minimalCardViewFactory);
      case SOCIAL_POST_ARTICLE:
      case SOCIAL_POST_VIDEO:
        return new SocialPostMediaViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_social_post_media_item, parent, false), dateCalculator,
            spannableFactory, cardTouchEventPublishSubject);
      case SOCIAL_POST_RECOMMENDATION:
        return new SocialPostRecommendationViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_social_post_recommendation_item, parent, false),
            dateCalculator, spannableFactory, cardTouchEventPublishSubject);
      case PROGRESS:
        return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_progress_item, parent, false));
      case TIMELINE_STATS:
        return new TimelineStatsViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_stats_item, parent, false), spannableFactory,
            cardTouchEventPublishSubject, storeContext);
      case LOGIN:
        return new TimelineLoginPostViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_login_item, parent, false), cardTouchEventPublishSubject);
      case AD:
        return new TimelineAdPostViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_native_ad_item, parent, false),
            cardTouchEventPublishSubject);
      case NOTIFICATIONS:
        return new Notifications(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_notification, parent, false), cardTouchEventPublishSubject,
            ImageLoader.with(parent.getContext()));
      case NO_NOTIFICATIONS:
        return new TimelineNoNotificationHeaderViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.timeline_no_notification, parent, false),
            cardTouchEventPublishSubject);
      default:
        throw new IllegalStateException("Wrong cardType" + cardType.name());
    }
  }
}
