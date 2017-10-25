package cm.aptoide.pt.social.data;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AdTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialAppTimelineItem;
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
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.PopularAppTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.RecommendationTimelineItem;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SimilarTimelineItem;
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
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.social.data.publisher.AptoidePublisher;
import cm.aptoide.pt.social.data.publisher.MediaPublisher;
import cm.aptoide.pt.social.data.publisher.Poster;
import cm.aptoide.pt.social.data.publisher.PublisherAvatar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class TimelineResponseCardMapper {
  private final String marketName;

  public TimelineResponseCardMapper(String marketName) {
    this.marketName = marketName;
  }

  public List<Post> map(List<TimelineItem<TimelineCard>> cardList,
      LinksHandlerFactory linksFactory) {
    final List<Post> cards = new ArrayList();

    for (TimelineItem<TimelineCard> item : cardList) {
      try {
        addMappedCardFromItem(linksFactory, cards, item);
      } catch (Exception e) {
        CrashReport.getInstance()
            .log(e);
      }
    }

    return cards;
  }

  private void addMappedCardFromItem(LinksHandlerFactory linksFactory, List<Post> cards,
      TimelineItem<TimelineCard> item) throws Exception {
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
          CardType.ARTICLE, getMarkAsReadUrl(article)));
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
              CardType.VIDEO, getMarkAsReadUrl(video)));
    } else {
      if (item instanceof RecommendationTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation recommendation =
            ((RecommendationTimelineItem) item).getData();
        cards.add(new Recommendation(recommendation.getCardId(), recommendation.getRecommendedApp()
            .getId(), recommendation.getRecommendedApp()
            .getPackageName(), recommendation.getRecommendedApp()
            .getName(), recommendation.getRecommendedApp()
            .getIcon(), recommendation.getRecommendedApp()
            .getStats()
            .getRating()
            .getAvg(), recommendation.getRecommendedApp()
            .getStore()
            .getId(), recommendation.getSimilarApps()
            .get(0)
            .getName(), recommendation.getSimilarApps()
            .get(0)
            .getPackageName(), new AptoidePublisher(R.mipmap.ic_launcher, marketName),
            recommendation.getTimestamp(), abUrl, false, CardType.RECOMMENDATION,
            getMarkAsReadUrl(recommendation)));
      } else if (item instanceof SimilarTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation recommendation =
            ((SimilarTimelineItem) item).getData();
        cards.add(new Recommendation(recommendation.getCardId(), recommendation.getRecommendedApp()
            .getId(), recommendation.getRecommendedApp()
            .getPackageName(), recommendation.getRecommendedApp()
            .getName(), recommendation.getRecommendedApp()
            .getIcon(), recommendation.getRecommendedApp()
            .getStats()
            .getRating()
            .getAvg(), recommendation.getSimilarApps()
            .get(0)
            .getId(), recommendation.getSimilarApps()
            .get(0)
            .getName(), recommendation.getSimilarApps()
            .get(0)
            .getPackageName(), new AptoidePublisher(R.mipmap.ic_launcher, marketName),
            recommendation.getTimestamp(), abUrl, false, CardType.SIMILAR,
            getMarkAsReadUrl(recommendation)));
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
            .getApps(), store.getLatestUpdate(), store.getApps(), abUrl, false, CardType.STORE,
            getMarkAsReadUrl(store)));
      } else if (item instanceof AppUpdateTimelineItem) {
        final cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate appUpdate =
            ((AppUpdateTimelineItem) item).getData();
        cards.add(new AppUpdate(appUpdate.getCardId(), appUpdate.getStore()
            .getName(), appUpdate.getStore()
            .getAvatar(), appUpdate.getStore()
            .getAppearance()
            .getTheme(), appUpdate.getStore()
            .getId(), appUpdate.getIcon(), appUpdate.getName(), appUpdate.getId(),
            appUpdate.getPackageName(), appUpdate.getStats()
            .getRating()
            .getAvg(), appUpdate.getAdded(), abUrl, false,
            // TODO: 26/06/2017 probably should get progress state someway because the download might be happening already.
            CardType.UPDATE, appUpdate.getFile(), appUpdate.getObb(),
            Install.InstallationStatus.UNINSTALLED, getMarkAsReadUrl(appUpdate)));
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
            .getStore()
            .getId(), popularApp.getPopularApplication()
            .getStats()
            .getRating()
            .getAvg(), users, popularApp.getDate(), abUrl, false, CardType.POPULAR_APP,
            getMarkAsReadUrl(popularApp)));
      } else if (item instanceof SocialRecommendationTimelineItem) {
        final SocialRecommendation socialRecommendation =
            ((SocialRecommendationTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialRecommendation.getUser(), socialRecommendation.getStore());
        if (!TextUtils.isEmpty(socialRecommendation.getContent())) {
          cards.add(new RatedRecommendation(socialRecommendation.getCardId(),
              new Poster(user.getUser(), user.getStore()), socialRecommendation.getApp()
              .getId(), socialRecommendation.getApp()
              .getPackageName(), socialRecommendation.getApp()
              .getName(), socialRecommendation.getApp()
              .getIcon(), socialRecommendation.getApp()
              .getStore()
              .getId(), socialRecommendation.getApp()
              .getStats()
              .getRating()
              .getAvg(), socialRecommendation.getDate(), abUrl, socialRecommendation.getMy()
              .isLiked(), socialRecommendation.getStats()
              .getComments(), socialRecommendation.getStats()
              .getLikes(), socialRecommendation.getLikes(), socialRecommendation.getComments(),
              null, socialRecommendation.getContent(), CardType.SOCIAL_POST_RECOMMENDATION,
              getMarkAsReadUrl(socialRecommendation)));
        } else {
          String sharedByName =
              socialRecommendation.getUserSharer() != null ? socialRecommendation.getUserSharer()
                  .getName() : null;
          cards.add(new RatedRecommendation(socialRecommendation.getCardId(),
              new Poster(user.getUser(), user.getStore()), socialRecommendation.getApp()
              .getId(), socialRecommendation.getApp()
              .getPackageName(), socialRecommendation.getApp()
              .getName(), socialRecommendation.getApp()
              .getIcon(), socialRecommendation.getApp()
              .getStore()
              .getId(), socialRecommendation.getApp()
              .getStats()
              .getRating()
              .getAvg(), socialRecommendation.getDate(), abUrl, socialRecommendation.getMy()
              .isLiked(), socialRecommendation.getStats()
              .getComments(), socialRecommendation.getStats()
              .getLikes(), socialRecommendation.getLikes(), socialRecommendation.getComments(),
              sharedByName, socialRecommendation.getContent(), CardType.SOCIAL_RECOMMENDATION,
              getMarkAsReadUrl(socialRecommendation)));
        }
      } else if (item instanceof SocialInstallTimelineItem) {
        final SocialInstall socialInstall = ((SocialInstallTimelineItem) item).getData();
        String sharedByName = socialInstall.getUserSharer() != null ? socialInstall.getUserSharer()
            .getName() : null;
        UserSharerTimeline user =
            new UserSharerTimeline(socialInstall.getUser(), socialInstall.getStore());
        cards.add(new RatedRecommendation(socialInstall.getCardId(),
            new Poster(user.getUser(), user.getStore()), socialInstall.getApp()
            .getId(), socialInstall.getApp()
            .getPackageName(), socialInstall.getApp()
            .getName(), socialInstall.getApp()
            .getIcon(), socialInstall.getApp()
            .getStore()
            .getId(), socialInstall.getApp()
            .getStats()
            .getRating()
            .getAvg(), socialInstall.getDate(), abUrl, socialInstall.getMy()
            .isLiked(), socialInstall.getStats()
            .getComments(), socialInstall.getStats()
            .getLikes(), socialInstall.getLikes(), socialInstall.getComments(), sharedByName, "",
            CardType.SOCIAL_INSTALL, getMarkAsReadUrl(socialInstall)));
      } else if (item instanceof SocialArticleTimelineItem) {
        final SocialArticle socialArticle = ((SocialArticleTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialArticle.getUser(), socialArticle.getStore());
        if (!TextUtils.isEmpty(socialArticle.getContent())) {
          cards.add(new SocialMedia(socialArticle.getCardId(),
              new Poster(user.getUser(), user.getStore()), socialArticle.getTitle(),
              socialArticle.getThumbnailUrl(), socialArticle.getDate(), socialArticle.getApps()
              .get(0), abUrl, new MediaPublisher(socialArticle.getPublisher()
              .getName(), new PublisherAvatar(socialArticle.getPublisher()
              .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
              socialArticle.getPublisher()
                  .getBaseUrl()),
              linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialArticle.getUrl()),
              socialArticle.getMy()
                  .isLiked(), socialArticle.getStats()
              .getComments(), socialArticle.getStats()
              .getLikes(), socialArticle.getLikes(), socialArticle.getComments(), null,
              socialArticle.getContent(), CardType.SOCIAL_POST_ARTICLE,
              getMarkAsReadUrl(socialArticle)));
        } else {
          String sharedByName =
              socialArticle.getUserSharer() != null ? socialArticle.getUserSharer()
                  .getName() : null;
          cards.add(new SocialMedia(socialArticle.getCardId(),
              new Poster(user.getUser(), user.getStore()), socialArticle.getTitle(),
              socialArticle.getThumbnailUrl(), socialArticle.getDate(), socialArticle.getApps()
              .get(0), abUrl, new MediaPublisher(socialArticle.getPublisher()
              .getName(), new PublisherAvatar(socialArticle.getPublisher()
              .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
              socialArticle.getPublisher()
                  .getBaseUrl()),
              linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, socialArticle.getUrl()),
              socialArticle.getMy()
                  .isLiked(), socialArticle.getStats()
              .getComments(), socialArticle.getStats()
              .getLikes(), socialArticle.getLikes(), socialArticle.getComments(), sharedByName,
              socialArticle.getContent(), CardType.SOCIAL_ARTICLE,
              getMarkAsReadUrl(socialArticle)));
        }
      } else if (item instanceof SocialVideoTimelineItem) {
        final SocialVideo socialVideo = ((SocialVideoTimelineItem) item).getData();
        UserSharerTimeline user =
            new UserSharerTimeline(socialVideo.getUser(), socialVideo.getStore());

        if (!TextUtils.isEmpty(socialVideo.getContent())) {
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
                      .isLiked(), socialVideo.getStats()
                  .getComments(), socialVideo.getStats()
                  .getLikes(), socialVideo.getLikes(), socialVideo.getComments(), null,
                  socialVideo.getContent(), CardType.SOCIAL_POST_VIDEO,
                  getMarkAsReadUrl(socialVideo)));
        } else {
          String sharedByName = socialVideo.getUserSharer() != null ? socialVideo.getUserSharer()
              .getName() : null;
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
                      .isLiked(), socialVideo.getStats()
                  .getComments(), socialVideo.getStats()
                  .getLikes(), socialVideo.getLikes(), socialVideo.getComments(), sharedByName,
                  socialVideo.getContent(), CardType.SOCIAL_VIDEO, getMarkAsReadUrl(socialVideo)));
        }
      } else if (item instanceof SocialStoreLatestAppsTimelineItem) {
        final SocialStoreLatestApps socialStoreLatestApps =
            ((SocialStoreLatestAppsTimelineItem) item).getData();
        UserSharerTimeline user = new UserSharerTimeline(socialStoreLatestApps.getUser(),
            socialStoreLatestApps.getOwnerStore());
        String sharedByName =
            socialStoreLatestApps.getUserSharer() != null ? socialStoreLatestApps.getUserSharer()
                .getName() : null;
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
                .isLiked(), socialStoreLatestApps.getStats()
            .getComments(), socialStoreLatestApps.getStats()
            .getLikes(), socialStoreLatestApps.getLikes(), socialStoreLatestApps.getComments(),
            sharedByName, CardType.SOCIAL_STORE, getMarkAsReadUrl(socialStoreLatestApps)));
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
                      .isLiked(), minimalCard.getStats()
                  .getComments(), minimalCard.getStats()
                  .getLikes(), minimalCard.getUsersLikes(), minimalCard.getComments(),
                  CardType.MINIMAL_CARD));
        }
        cards.add(new AggregatedMedia("n/a", posters, aggregatedSocialArticle.getTitle(),
            aggregatedSocialArticle.getThumbnailUrl(), aggregatedSocialArticle.getDate(),
            aggregatedSocialArticle.getApps()
                .get(0), abUrl, new MediaPublisher(aggregatedSocialArticle.getPublisher()
            .getName(), new PublisherAvatar(aggregatedSocialArticle.getPublisher()
            .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialArticle.getPublisher()
                .getBaseUrl()), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialArticle.getUrl()), subposts, CardType.AGGREGATED_SOCIAL_ARTICLE,
            getMarkAsReadUrl(aggregatedSocialArticle)));
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
                      .isLiked(), minimalCard.getStats()
                  .getComments(), minimalCard.getStats()
                  .getLikes(), minimalCard.getUsersLikes(), minimalCard.getComments(),
                  CardType.MINIMAL_CARD));
        }

        cards.add(new AggregatedMedia("n/a", posters, aggregatedSocialVideo.getTitle(),
            aggregatedSocialVideo.getThumbnailUrl(), aggregatedSocialVideo.getDate(),
            aggregatedSocialVideo.getApps()
                .get(0), abUrl, new MediaPublisher(aggregatedSocialVideo.getPublisher()
            .getName(), new PublisherAvatar(aggregatedSocialVideo.getPublisher()
            .getLogoUrl())), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialVideo.getPublisher()
                .getBaseUrl()), linksFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE,
            aggregatedSocialVideo.getUrl()), subposts, CardType.AGGREGATED_SOCIAL_VIDEO,
            getMarkAsReadUrl(aggregatedSocialVideo)));
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
                      .isLiked(), minimalCard.getStats()
                  .getComments(), minimalCard.getStats()
                  .getLikes(), minimalCard.getUsersLikes(), minimalCard.getComments(),
                  CardType.MINIMAL_CARD));
        }
        CardType cardType = CardType.AGGREGATED_SOCIAL_INSTALL;
        if (item instanceof AggregatedSocialAppTimelineItem) {
          cardType = CardType.AGGREGATED_SOCIAL_APP;
        }
        cards.add(new AggregatedRecommendation("n/a", posters, subposts,
            aggregatedSocialInstall.getApp()
                .getIcon(), aggregatedSocialInstall.getApp()
            .getName(), aggregatedSocialInstall.getApp()
            .getId(), aggregatedSocialInstall.getApp()
            .getStats()
            .getRating()
            .getAvg(), aggregatedSocialInstall.getApp()
            .getStore()
            .getId(), aggregatedSocialInstall.getApp()
            .getPackageName(), aggregatedSocialInstall.getDate(), abUrl, cardType,
            getMarkAsReadUrl(aggregatedSocialInstall)));
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
                      .isLiked(), minimalCard.getStats()
                  .getComments(), minimalCard.getStats()
                  .getLikes(), minimalCard.getUsersLikes(), minimalCard.getComments(),
                  CardType.MINIMAL_CARD));
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
            aggregatedSocialStoreLatestApps.getApps(), abUrl, CardType.AGGREGATED_SOCIAL_STORE,
            getMarkAsReadUrl(aggregatedSocialStoreLatestApps)));
      } else if (item instanceof AdTimelineItem) {
        cards.add(new AdPost());
      }
    }
  }

  @Nullable private String getMarkAsReadUrl(TimelineCard card) {
    return card.getUrls() == null ? null : card.getUrls()
        .getRead();
  }
}
