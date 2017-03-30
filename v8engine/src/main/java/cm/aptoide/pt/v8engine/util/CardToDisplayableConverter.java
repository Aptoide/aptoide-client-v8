package cm.aptoide.pt.v8engine.util;

import android.support.annotation.UiThread;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.Recommendation;
import cm.aptoide.pt.model.v7.timeline.Similar;
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.SocialVideo;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.model.v7.timeline.Video;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.DownloadEventConverter;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEventConverter;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SimilarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.VideoDisplayable;
import java.util.HashMap;

// TODO should this be a singleton?
public class CardToDisplayableConverter implements CardToDisplayable {

  private final HashMap<Class, CardToDisplayable> converters;

  private final SocialRepository socialRepository;
  private final TimelineAnalytics timelineAnalytics;
  private final InstallManager installManager;
  private final PermissionManager permissionManager;
  private final StoreCredentialsProvider storeCredentialsProvider;
  private final InstallEventConverter installEventConverter;
  private final Analytics analytics;
  private final DownloadEventConverter downloadEventConverter;

  public CardToDisplayableConverter(SocialRepository socialRepository,
      TimelineAnalytics timelineAnalytics, InstallManager installManager,
      PermissionManager permissionManager, StoreCredentialsProvider storeCredentialsProvider,
      InstallEventConverter installEventConverter, Analytics analytics,
      DownloadEventConverter downloadEventConverter) {
    this.socialRepository = socialRepository;
    this.timelineAnalytics = timelineAnalytics;
    this.installManager = installManager;
    this.permissionManager = permissionManager;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.installEventConverter = installEventConverter;
    this.analytics = analytics;
    this.downloadEventConverter = downloadEventConverter;
    this.converters = new HashMap<>();
    init();
  }

  private void init() {

    //
    // SocialRecommendation
    //
    converters.put(SocialRecommendation.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {
        return SocialRecommendationDisplayable.from((SocialRecommendation) card, spannableFactory,
            socialRepository, dateCalculator);
      }
    });

    //
    // SocialInstall
    //
    converters.put(SocialInstall.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return SocialInstallDisplayable.from((SocialInstall) card, timelineAnalytics,
            spannableFactory, socialRepository, dateCalculator);
      }
    });

    //
    // Similar
    //
    converters.put(Similar.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return SimilarDisplayable.from((Similar) card, dateCalculator, spannableFactory,
            timelineAnalytics, socialRepository);
      }
    });

    //
    // Recommendation
    //
    converters.put(Recommendation.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return RecommendationDisplayable.from((Recommendation) card, dateCalculator,
            spannableFactory, timelineAnalytics, socialRepository);
      }
    });

    //
    // AppUpdate
    //
    converters.put(AppUpdate.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return AppUpdateDisplayable.from((AppUpdate) card, spannableFactory, downloadFactory,
            dateCalculator, installManager, permissionManager, timelineAnalytics, socialRepository,
            installEventConverter, analytics, downloadEventConverter);
      }
    });

    //
    // StoreLatestApps
    //
    converters.put(StoreLatestApps.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return StoreLatestAppsDisplayable.from((StoreLatestApps) card, dateCalculator,
            timelineAnalytics, socialRepository);
      }
    });

    //
    // Feature
    //
    converters.put(Feature.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return FeatureDisplayable.from((Feature) card, dateCalculator, spannableFactory);
      }
    });

    //
    // SocialStoreLatestApps
    //
    converters.put(SocialStoreLatestApps.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return SocialStoreLatestAppsDisplayable.from((SocialStoreLatestApps) card, dateCalculator,
            timelineAnalytics, socialRepository, spannableFactory, storeCredentialsProvider);
      }
    });

    //
    // SocialVideo
    //
    converters.put(SocialVideo.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return SocialVideoDisplayable.from(((SocialVideo) card), dateCalculator, spannableFactory,
            linksHandlerFactory, timelineAnalytics, socialRepository);
      }
    });

    //
    // SocialArticle
    //
    converters.put(SocialArticle.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return SocialArticleDisplayable.from(((SocialArticle) card), dateCalculator,
            spannableFactory, linksHandlerFactory, timelineAnalytics, socialRepository);
      }
    });

    //
    // Video
    //
    converters.put(Video.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return VideoDisplayable.from((Video) card, dateCalculator, spannableFactory,
            linksHandlerFactory, timelineAnalytics, socialRepository);
      }
    });

    //
    // Article
    //
    converters.put(Article.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return ArticleDisplayable.from((Article) card, dateCalculator, spannableFactory,
            linksHandlerFactory, timelineAnalytics, socialRepository);
      }
    });
  }

  @UiThread @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, DownloadFactory downloadFactory,
      LinksHandlerFactory linksHandlerFactory) {

    CardToDisplayable converter = converters.get(card.getClass());
    if (converter != null) {
      return converter.convert(card, dateCalculator, spannableFactory, downloadFactory,
          linksHandlerFactory);
    }

    throw new IllegalArgumentException(
        "Only articles, features, store latest apps, app updates, videos, recommendations and similar cards supported.");
  }
}
