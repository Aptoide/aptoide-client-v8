package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialArticle;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialArticleTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialInstall;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialInstallTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialStoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialStoreLatestAppsTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialVideo;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialVideoTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdateTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Article;
import cm.aptoide.pt.dataprovider.model.v7.timeline.ArticleTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.PopularAppTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.RecommendationTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialArticleTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialInstallTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialRecommendationTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialStoreLatestAppsTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialVideo;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialVideoTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.StoreLatestAppsTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Video;
import cm.aptoide.pt.dataprovider.model.v7.timeline.VideoTimelineItem;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.social.data.publisher.AptoidePublisher;
import cm.aptoide.pt.v8engine.social.data.publisher.MediaPublisher;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.social.data.publisher.PublisherAvatar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineResponseCardMapper {
  public List<Post> map(GetUserTimeline timelineResponse, LinksHandlerFactory linksFactory) {
    final List<Post> cards = new ArrayList();

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
            .get(0), abUrl, new MediaPublisher(article.getPublisher()
            .getName(), new PublisherAvatar(article.getPublisher()
            .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            article.getPublisher()
                .getBaseUrl()),
            linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, article.getUrl()), false,
            CardType.ARTICLE));
      } else if (item instanceof VideoTimelineItem) {
        final Video video = ((VideoTimelineItem) item).getData();
        cards.add(
            new Media(video.getCardId(), video.getTitle(), video.getThumbnailUrl(), video.getDate(),
                video.getApps()
                    .get(0), abUrl, new MediaPublisher(video.getPublisher()
                .getName(), new PublisherAvatar(video.getPublisher()
                .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
                video.getPublisher()
                    .getBaseUrl()),
                linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, video.getUrl()), false,
                CardType.VIDEO));
      } else if (item instanceof RecommendationTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation recommendation =
            ((RecommendationTimelineItem) item).getData();
        cards.add(new Recommendation(recommendation.getCardId(), recommendation.getRecommendedApp()
            .getId(), recommendation.getRecommendedApp()
            .getPackageName(), recommendation.getRecommendedApp()
            .getName(), recommendation.getRecommendedApp()
            .getIcon(), recommendation.getSimilarApps()
            .get(0)
            .getName(), new AptoidePublisher(), recommendation.getTimestamp(), abUrl, false,
            CardType.RECOMMENDATION));
      } else if (item instanceof StoreLatestAppsTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.StoreLatestApps store =
            ((StoreLatestAppsTimelineItem) item).getData();
        cards.add(new StoreLatestApps(store.getCardId(), store.getStore()
            .getId(), store.getStore()
            .getName(), store.getStore()
            .getAvatar(), store.getStore()
            .getAppearance()
            .getTheme(), store.getStore()
            .getStats()
            .getSubscribers(), store.getStore()
            .getStats()
            .getApps(), store.getLatestUpdate(), store.getApps(), abUrl, false, CardType.STORE));
      } else if (item instanceof AppUpdateTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate appUpdate =
            ((AppUpdateTimelineItem) item).getData();
        cards.add(new AppUpdate(appUpdate.getCardId(), appUpdate.getStore()
            .getName(), appUpdate.getStore()
            .getAvatar(), appUpdate.getStore()
            .getAppearance()
            .getTheme(), appUpdate.getIcon(), appUpdate.getName(), appUpdate.getPackageName(),
            appUpdate.getAdded(), abUrl, false, CardType.UPDATE, appUpdate.getFile(),
            // TODO: 26/06/2017 probably should get progress state someway because the download might be happening already.
            appUpdate.getObb(), Install.InstallationStatus.UNINSTALLED));
      } else if (item instanceof PopularAppTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.PopularApp popularApp =
            ((PopularAppTimelineItem) item).getData();
        List<UserSharerTimeline.User> users = new ArrayList<>();
        for (Comment.User user : popularApp.getUsers()) {
          users.add(new UserSharerTimeline.User(user.getId(), user.getName(), user.getAvatar()));
        }
        cards.add(new PopularApp(popularApp.getCardId(), popularApp.getPopularApplication()
            .getId(), popularApp.getPopularApplication()
            .getPackageName(), popularApp.getPopularApplication()
            .getName(), popularApp.getPopularApplication()
            .getIcon(), popularApp.getPopularApplication()
            .getStats()
            .getRating()
            .getAvg(), users, popularApp.getDate(), abUrl, false, CardType.POPULAR_APP));
      } else if (item instanceof SocialRecommendationTimelineItem) {
        final SocialRecommendation socialRecommendation =
            ((SocialRecommendationTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialRecommendation.getUser(), socialRecommendation.getStore());

        cards.add(new RatedRecommendation(socialRecommendation.getCardId(),
            new Poster(user.getUser(), user.getStore()), socialRecommendation.getApp()
            .getId(), socialRecommendation.getApp()
            .getPackageName(), socialRecommendation.getApp()
            .getName(), socialRecommendation.getApp()
            .getIcon(), socialRecommendation.getApp()
            .getStats()
            .getRating()
            .getAvg(), socialRecommendation.getDate(), abUrl, false,
            CardType.SOCIAL_RECOMMENDATION));
      } else if (item instanceof SocialInstallTimelineItem) {
        final SocialInstall socialInstall = ((SocialInstallTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialInstall.getUser(), socialInstall.getStore());
        cards.add(new RatedRecommendation(socialInstall.getCardId(),
            new Poster(user.getUser(), user.getStore()), socialInstall.getApp()
            .getId(), socialInstall.getApp()
            .getPackageName(), socialInstall.getApp()
            .getName(), socialInstall.getApp()
            .getIcon(), socialInstall.getApp()
            .getStats()
            .getRating()
            .getAvg(), socialInstall.getDate(), abUrl, false, CardType.SOCIAL_INSTALL));
      } else if (item instanceof SocialArticleTimelineItem) {
        final SocialArticle socialArticle = ((SocialArticleTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialArticle.getUser(), socialArticle.getStore());
        cards.add(
            new SocialMedia(socialArticle.getCardId(), new Poster(user.getUser(), user.getStore()),
                socialArticle.getTitle(), socialArticle.getThumbnailUrl(), socialArticle.getDate(),
                socialArticle.getApps()
                    .get(0), abUrl, new MediaPublisher(socialArticle.getPublisher()
                .getName(), new PublisherAvatar(socialArticle.getPublisher()
                .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
                socialArticle.getPublisher()
                    .getBaseUrl()),
                linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialArticle.getUrl()),
                socialArticle.getMy()
                    .isLiked(), CardType.SOCIAL_ARTICLE));
      } else if (item instanceof SocialVideoTimelineItem) {
        final SocialVideo socialVideo = ((SocialVideoTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialVideo.getUser(), socialVideo.getStore());
        cards.add(
            new SocialMedia(socialVideo.getCardId(), new Poster(user.getUser(), user.getStore()),
                socialVideo.getTitle(), socialVideo.getThumbnailUrl(), socialVideo.getDate(),
                socialVideo.getApps()
                    .get(0), abUrl, new MediaPublisher(socialVideo.getPublisher()
                .getName(), new PublisherAvatar(socialVideo.getPublisher()
                .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
                socialVideo.getPublisher()
                    .getBaseUrl()),
                linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialVideo.getUrl()),
                socialVideo.getMy()
                    .isLiked(), CardType.SOCIAL_VIDEO));
      } else if (item instanceof SocialStoreLatestAppsTimelineItem) {
        final SocialStoreLatestApps socialStoreLatestApps =
            ((SocialStoreLatestAppsTimelineItem) item).getData();
        UserSharerTimeline user = new UserSharerTimeline(socialStoreLatestApps.getUser(),
            socialStoreLatestApps.getOwnerStore());
        cards.add(new SocialStore(socialStoreLatestApps.getCardId(),
            new Poster(user.getUser(), user.getStore()), socialStoreLatestApps.getSharedStore()
            .getId(), socialStoreLatestApps.getSharedStore()
            .getName(), socialStoreLatestApps.getSharedStore()
            .getAvatar(), socialStoreLatestApps.getSharedStore()
            .getAppearance()
            .getTheme(), socialStoreLatestApps.getSharedStore()
            .getStats()
            .getSubscribers(), socialStoreLatestApps.getSharedStore()
            .getStats()
            .getApps(), socialStoreLatestApps.getDate(), socialStoreLatestApps.getApps(), abUrl,
            socialStoreLatestApps.getMy()
                .isLiked(), CardType.SOCIAL_STORE));
      } else if (item instanceof AggregatedSocialArticleTimelineItem) {
        final AggregatedSocialArticle aggregatedSocialArticle =
            ((AggregatedSocialArticleTimelineItem) item).getData();
        List<Poster> posters = new ArrayList<>();
        for (UserSharerTimeline sharer : aggregatedSocialArticle.getSharers()) {
          posters.add(new Poster(sharer.getUser(), sharer.getStore()));
        }
        List<Post> subposts = new ArrayList<>();
        for (MinimalCard minimalCard : aggregatedSocialArticle.getMinimalCardList()) {
          List<Poster> minimalPostPosters = new ArrayList<>();
          for (UserSharerTimeline sharer : minimalCard.getSharers()) {
            minimalPostPosters.add(new Poster(sharer.getUser(), sharer.getStore()));
          }

          subposts.add(
              new MinimalPost(minimalCard.getCardId(), minimalPostPosters, minimalCard.getDate(),
                  minimalCard.getMy()
                      .isLiked(), CardType.MINIMAL_CARD));
        }
        cards.add(new AggregatedMedia("n/a", posters, aggregatedSocialArticle.getTitle(),
            aggregatedSocialArticle.getThumbnailUrl(), aggregatedSocialArticle.getDate(),
            aggregatedSocialArticle.getApps()
                .get(0), abUrl, new MediaPublisher(aggregatedSocialArticle.getPublisher()
            .getName(), new PublisherAvatar(aggregatedSocialArticle.getPublisher()
            .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialArticle.getPublisher()
                .getBaseUrl()), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialArticle.getUrl()), subposts, CardType.AGGREGATED_SOCIAL_ARTICLE));
      } else if (item instanceof AggregatedSocialVideoTimelineItem) {
        final AggregatedSocialVideo aggregatedSocialVideo =
            ((AggregatedSocialVideoTimelineItem) item).getData();
        List<Poster> posters = new ArrayList<>();
        for (UserSharerTimeline sharer : aggregatedSocialVideo.getSharers()) {
          posters.add(new Poster(sharer.getUser(), sharer.getStore()));
        }
        List<Post> subposts = new ArrayList<>();
        for (MinimalCard minimalCard : aggregatedSocialVideo.getMinimalCards()) {
          List<Poster> minimalPostPosters = new ArrayList<>();
          for (UserSharerTimeline sharer : minimalCard.getSharers()) {
            minimalPostPosters.add(new Poster(sharer.getUser(), sharer.getStore()));
          }

          subposts.add(
              new MinimalPost(minimalCard.getCardId(), minimalPostPosters, minimalCard.getDate(),
                  minimalCard.getMy()
                      .isLiked(), CardType.MINIMAL_CARD));
        }

        cards.add(new AggregatedMedia("n/a", posters, aggregatedSocialVideo.getTitle(),
            aggregatedSocialVideo.getThumbnailUrl(), aggregatedSocialVideo.getDate(),
            aggregatedSocialVideo.getApps()
                .get(0), abUrl, new MediaPublisher(aggregatedSocialVideo.getPublisher()
            .getName(), new PublisherAvatar(aggregatedSocialVideo.getPublisher()
            .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialVideo.getPublisher()
                .getBaseUrl()), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialVideo.getUrl()), subposts, CardType.AGGREGATED_SOCIAL_VIDEO));
      } else if (item instanceof AggregatedSocialInstallTimelineItem) {
        final AggregatedSocialInstall aggregatedSocialInstall =
            ((AggregatedSocialInstallTimelineItem) item).getData();
        List<Poster> posters = new ArrayList<>();
        for (UserSharerTimeline sharer : aggregatedSocialInstall.getSharers()) {
          posters.add(new Poster(sharer.getUser(), sharer.getStore()));
        }
        List<Post> subposts = new ArrayList<>();
        for (MinimalCard minimalCard : aggregatedSocialInstall.getMinimalCardList()) {
          List<Poster> minimalPostPosters = new ArrayList<>();
          for (UserSharerTimeline sharer : minimalCard.getSharers()) {
            minimalPostPosters.add(new Poster(sharer.getUser(), sharer.getStore()));
          }

          subposts.add(
              new MinimalPost(minimalCard.getCardId(), minimalPostPosters, minimalCard.getDate(),
                  minimalCard.getMy()
                      .isLiked(), CardType.MINIMAL_CARD));
        }
        cards.add(new AggregatedRecommendation("n/a", posters, subposts,
            aggregatedSocialInstall.getApp()
                .getIcon(), aggregatedSocialInstall.getApp()
            .getName(), aggregatedSocialInstall.getApp()
            .getId(), aggregatedSocialInstall.getApp()
            .getStats()
            .getRating()
            .getAvg(), aggregatedSocialInstall.getApp()
            .getPackageName(), aggregatedSocialInstall.getDate(), abUrl,
            CardType.AGGREGATED_SOCIAL_INSTALL));
      } else if (item instanceof AggregatedSocialStoreLatestAppsTimelineItem) {
        final AggregatedSocialStoreLatestApps aggregatedSocialStoreLatestApps =
            ((AggregatedSocialStoreLatestAppsTimelineItem) item).getData();
        List<Poster> posters = new ArrayList<>();
        for (UserSharerTimeline sharer : aggregatedSocialStoreLatestApps.getSharers()) {
          posters.add(new Poster(sharer.getUser(), sharer.getStore()));
        }
        List<Post> subposts = new ArrayList<>();
        for (MinimalCard minimalCard : aggregatedSocialStoreLatestApps.getMinimalCardList()) {
          List<Poster> minimalPostPosters = new ArrayList<>();
          for (UserSharerTimeline sharer : minimalCard.getSharers()) {
            minimalPostPosters.add(new Poster(sharer.getUser(), sharer.getStore()));
          }

          subposts.add(
              new MinimalPost(minimalCard.getCardId(), minimalPostPosters, minimalCard.getDate(),
                  minimalCard.getMy()
                      .isLiked(), CardType.MINIMAL_CARD));
        }
        cards.add(new AggregatedStore("n/a", posters, subposts,
            aggregatedSocialStoreLatestApps.getSharedStore()
                .getId(), aggregatedSocialStoreLatestApps.getSharedStore()
            .getName(), aggregatedSocialStoreLatestApps.getSharedStore()
            .getAvatar(), aggregatedSocialStoreLatestApps.getSharedStore()
            .getAppearance()
            .getTheme(), aggregatedSocialStoreLatestApps.getSharedStore()
            .getStats()
            .getSubscribers(), aggregatedSocialStoreLatestApps.getSharedStore()
            .getStats()
            .getApps(), aggregatedSocialStoreLatestApps.getDate(),
            aggregatedSocialStoreLatestApps.getApps(), abUrl, CardType.AGGREGATED_SOCIAL_STORE));
      }
    }

    return cards;
  }
}
