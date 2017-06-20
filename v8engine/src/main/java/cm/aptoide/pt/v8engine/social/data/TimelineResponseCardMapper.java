package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.RecommendationTimelineItem;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.model.v7.timeline.VideoTimelineItem;
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
      if (item instanceof ArticleTimelineItem) {
        final Article article = ((ArticleTimelineItem) item).getData();
        String abUrl = null;
        if (article.getAb() != null
            && article.getAb()
            .getConversion() != null) {
          abUrl = article.getAb()
              .getConversion()
              .getUrl();
        }

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
        String abUrl = null;
        if (video.getAb() != null
            && video.getAb()
            .getConversion() != null) {
          abUrl = video.getAb()
              .getConversion()
              .getUrl();
        }
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
        String abUrl = null;
        if (recommendation.getAb() != null
            && recommendation.getAb()
            .getConversion() != null) {
          abUrl = recommendation.getAb()
              .getConversion()
              .getUrl();
        }
        cards.add(new cm.aptoide.pt.v8engine.social.data.Recommendation(recommendation.getCardId(),
            recommendation.getRecommendedApp()
                .getId(), recommendation.getRecommendedApp()
            .getPackageName(), recommendation.getRecommendedApp()
            .getName(), recommendation.getRecommendedApp()
            .getIcon(), recommendation.getSimilarApps()
            .get(0)
            .getName(), recommendation.getTimestamp(), abUrl, CardType.RECOMMENDATION));
      }
    }

    return cards;
  }
}
