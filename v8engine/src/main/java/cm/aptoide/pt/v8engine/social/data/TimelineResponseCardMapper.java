package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
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
        String ab = null;
        final cm.aptoide.pt.model.v7.timeline.Article article =
            ((ArticleTimelineItem) item).getData();
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
      }
    }

    return cards;
  }
}
