package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.AppUpdateTimelineItem;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.RecommendationTimelineItem;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.StoreLatestAppsTimelineItem;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.model.v7.timeline.VideoTimelineItem;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineResponseCardMapper {
  public List<Card> map(GetUserTimeline timelineResponse, LinksHandlerFactory linksFactory) {
    final List<Card> cards = new ArrayList();

    for (TimelineItem<TimelineCard> item : timelineResponse.getDatalist()
        .getList()) {
      String abUrl = null;
      if (item.getAb() != null
          && item.getAb()
          .getConversion() != null) {
        abUrl = item.getAb()
            .getConversion()
            .getUrl();
      }
      if (item instanceof ArticleTimelineItem) {
        final Article article = ((ArticleTimelineItem) item).getData();
        cards.add(new Media(article.getCardId(), article.getTitle(), article.getThumbnailUrl(),
            article.getDate(), article.getApps()
            .get(0), abUrl, article.getPublisher()
            .getLogoUrl(), article.getPublisher()
            .getName(), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            article.getPublisher()
                .getBaseUrl()),
            linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()),
            CardType.ARTICLE));
      } else if (item instanceof VideoTimelineItem) {
        final Video video = ((VideoTimelineItem) item).getData();
        cards.add(
            new Media(video.getCardId(), video.getTitle(), video.getThumbnailUrl(), video.getDate(),
                video.getApps()
                    .get(0), abUrl, video.getPublisher()
                .getLogoUrl(), video.getPublisher()
                .getName(), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
                video.getPublisher()
                    .getBaseUrl()),
                linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, video.getUrl()),
                CardType.VIDEO));
      } else if (item instanceof RecommendationTimelineItem) {
        final Recommendation recommendation = ((RecommendationTimelineItem) item).getData();
        cards.add(new cm.aptoide.pt.v8engine.social.data.Recommendation(recommendation.getCardId(),
            recommendation.getRecommendedApp()
                .getId(), recommendation.getRecommendedApp()
            .getPackageName(), recommendation.getRecommendedApp()
            .getName(), recommendation.getRecommendedApp()
            .getIcon(), recommendation.getSimilarApps()
            .get(0)
            .getName(), recommendation.getTimestamp(), abUrl, CardType.RECOMMENDATION));
      } else if (item instanceof StoreLatestAppsTimelineItem) {
        final StoreLatestApps store = ((StoreLatestAppsTimelineItem) item).getData();
        cards.add(new cm.aptoide.pt.v8engine.social.data.StoreLatestApps(store.getCardId(),
            store.getStore()
                .getName(), store.getStore()
            .getAvatar(), store.getStore()
            .getStats()
            .getSubscribers(), store.getStore()
            .getStats()
            .getApps(), store.getLatestUpdate(), store.getApps(), abUrl, CardType.STORE));
      } else if (item instanceof AppUpdateTimelineItem) {
        final AppUpdate appUpdate = ((AppUpdateTimelineItem) item).getData();
        cards.add(new cm.aptoide.pt.v8engine.social.data.AppUpdate(appUpdate.getCardId(),
            appUpdate.getStore()
                .getName(), appUpdate.getStore()
            .getAvatar(), appUpdate.getIcon(), appUpdate.getName(), appUpdate.getPackageName(),
            appUpdate.getAdded(), abUrl, CardType.UPDATE, appUpdate.getFile(), appUpdate.getObb(),
            // TODO: 26/06/2017 probably should get progress state someway because the download might be happening already.
            Progress.INACTIVE));
      }
    }

    return cards;
  }
}
