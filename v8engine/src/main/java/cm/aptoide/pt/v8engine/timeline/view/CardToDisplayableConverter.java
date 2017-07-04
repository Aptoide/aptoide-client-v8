package cm.aptoide.pt.v8engine.timeline.view;

import android.content.res.Resources;
import android.support.annotation.UiThread;
import android.view.WindowManager;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialArticle;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialInstall;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialStoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialVideo;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Article;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Feature;
import cm.aptoide.pt.dataprovider.model.v7.timeline.PopularApp;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialArticle;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialVideo;
import cm.aptoide.pt.dataprovider.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.TimelineCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Video;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.DownloadFactory;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialArticleDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialInstallDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AggregatedSocialVideoDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.ArticleDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.PopularAppDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.displayable.VideoDisplayable;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
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
  private final AppsTimelineNavigator timelineNavigator;
  private final Resources resources;
  private final String marketName;
  private final WindowManager windowManager;
  private final InstalledRepository installedRepository;

  public CardToDisplayableConverter(SocialRepository socialRepository,
      TimelineAnalytics timelineAnalytics, InstallManager installManager,
      PermissionManager permissionManager, StoreCredentialsProvider storeCredentialsProvider,
      InstallEventConverter installEventConverter, Analytics analytics,
      DownloadEventConverter downloadEventConverter, AppsTimelineNavigator timelineNavigator,
      Resources resources, String marketName, WindowManager windowManager,
      InstalledRepository installedRepository) {
    this.socialRepository = socialRepository;
    this.timelineAnalytics = timelineAnalytics;
    this.installManager = installManager;
    this.permissionManager = permissionManager;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.installEventConverter = installEventConverter;
    this.analytics = analytics;
    this.downloadEventConverter = downloadEventConverter;
    this.installedRepository = installedRepository;
    this.converters = new HashMap<>();
    this.timelineNavigator = timelineNavigator;
    this.resources = resources;
    this.marketName = marketName;
    this.windowManager = windowManager;
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
            socialRepository, dateCalculator, timelineAnalytics, timelineNavigator, resources,
            marketName, windowManager);
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
            spannableFactory, socialRepository, dateCalculator, timelineNavigator, resources,
            Application.getConfiguration()
                .getMarketName(), windowManager);
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
            spannableFactory, timelineAnalytics, socialRepository, windowManager);
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
            installEventConverter, analytics, downloadEventConverter, resources, windowManager);
      }
    });

    //
    // StoreLatestApps
    //
    converters.put(StoreLatestApps.class, new CardToDisplayable() {
      @Override public Displayable convert(TimelineCard card, DateCalculator dateCalculator,
          SpannableFactory spannableFactory, DownloadFactory downloadFactory,
          LinksHandlerFactory linksHandlerFactory) {

        return StoreLatestAppsDisplayable.from((StoreLatestApps) card, spannableFactory,
            dateCalculator, timelineAnalytics, socialRepository, windowManager);
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
            timelineAnalytics, socialRepository, spannableFactory, storeCredentialsProvider,
            timelineNavigator, windowManager);
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
            linksHandlerFactory, timelineAnalytics, socialRepository, installedRepository,
            timelineNavigator, windowManager);
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
            spannableFactory, linksHandlerFactory, timelineAnalytics, socialRepository,
            installedRepository, timelineNavigator, windowManager);
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
            linksHandlerFactory, timelineAnalytics, socialRepository, installedRepository,
            windowManager);
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
            linksHandlerFactory, timelineAnalytics, socialRepository, installedRepository,
            windowManager);
      }
    });

    //
    // Popular App
    //
    converters.put(PopularApp.class,
        (card, dateCalculator, spannableFactory, downloadFactory, linksHandlerFactory) -> PopularAppDisplayable.from(
            (PopularApp) card, dateCalculator, socialRepository, timelineAnalytics, windowManager));

    //
    // AggregatedSocialInstall
    //
    converters.put(AggregatedSocialInstall.class,
        (card, dateCalculator, spannableFactory, downloadFactory, linksHandlerFactory) -> {
          return AggregatedSocialInstallDisplayable.from((AggregatedSocialInstall) card,
              timelineAnalytics, socialRepository, dateCalculator, spannableFactory,
              timelineNavigator, windowManager);
        });

    //
    // AggregatedSocialArticle
    //
    converters.put(AggregatedSocialArticle.class,
        (card, dateCalculator, spannableFactory, downloadFactory, linksHandlerFactory) -> AggregatedSocialArticleDisplayable.from(
            (AggregatedSocialArticle) card, dateCalculator, spannableFactory, linksHandlerFactory,
            timelineAnalytics, socialRepository, timelineNavigator, windowManager));

    //
    // AggregatedSocialStore
    //
    converters.put(AggregatedSocialStoreLatestApps.class,
        ((card, dateCalculator, spannableFactory, downloadFactory, linksHandlerFactory) -> AggregatedSocialStoreLatestAppsDisplayable.from(
            (AggregatedSocialStoreLatestApps) card, dateCalculator, spannableFactory,
            timelineAnalytics, socialRepository, storeCredentialsProvider, timelineNavigator,
            windowManager)));

    //
    // AggregatedSocialVideo
    //
    converters.put(AggregatedSocialVideo.class,
        (((card, dateCalculator, spannableFactory, downloadFactory, linksHandlerFactory) -> AggregatedSocialVideoDisplayable.from(
            (AggregatedSocialVideo) card, dateCalculator, spannableFactory, linksHandlerFactory,
            timelineAnalytics, socialRepository, timelineNavigator, windowManager))));
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
