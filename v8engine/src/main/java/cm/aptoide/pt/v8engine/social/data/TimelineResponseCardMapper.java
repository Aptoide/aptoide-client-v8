package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
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
  public List<Media> map(GetUserTimeline timelineResponse, LinksHandlerFactory linksFactory) {
    final List<Media> cards = new ArrayList();

    for (TimelineItem<TimelineCard> item : timelineResponse.getDatalist()
        .getList()) {
      if (item instanceof ArticleTimelineItem) {
        final Article article = ((ArticleTimelineItem) item).getData();
        String ab = null;
        if (article.getAb() != null
            && article.getAb()
            .getConversion() != null) {
          ab = article.getAb()
              .getConversion()
              .getUrl();
        }

        cards.add(new Media(article.getCardId(), article.getTitle(), article.getThumbnailUrl(),
            article.getDate(), article.getApps()
            .get(0), ab, article.getPublisher()
            .getLogoUrl(), article.getPublisher()
            .getName(), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            article.getPublisher()
                .getBaseUrl()),
            linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()),
            CardType.ARTICLE));
      } else if (item instanceof VideoTimelineItem) {
        final Video video = ((VideoTimelineItem) item).getData();
        String ab = null;
        if (video.getAb() != null
            && video.getAb()
            .getConversion() != null) {
          ab = video.getAb()
              .getConversion()
              .getUrl();
        }
        cards.add(
            new Media(video.getCardId(), video.getTitle(), video.getThumbnailUrl(), video.getDate(),
                video.getApps()
                    .get(0), ab, video.getPublisher()
                .getLogoUrl(), video.getPublisher()
                .getName(), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
                video.getPublisher()
                    .getBaseUrl()),
                linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, video.getUrl()),
                CardType.VIDEO));
      }
    }

    return cards;
  }
}
