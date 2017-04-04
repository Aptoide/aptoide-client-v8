package cm.aptoide.pt.v8engine.view.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewRateResultsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.store.AddMoreStoresDisplayable;
import cm.aptoide.pt.v8engine.view.store.home.AdultRowDisplayable;
import cm.aptoide.pt.v8engine.view.store.featured.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.store.featured.AppBrickListDisplayable;
import cm.aptoide.pt.v8engine.view.comments.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.completed.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.account.user.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.updates.excluded.ExcludedUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.FollowStoreDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.FooterRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.app.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.app.GridAppListDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.store.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.updates.installed.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.MessageWhiteBgDisplayable;
import cm.aptoide.pt.v8engine.view.store.my.MyStoreDisplayable;
import cm.aptoide.pt.v8engine.view.app.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.app.OtherVersionDisplayable;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoreWidget;
import cm.aptoide.pt.v8engine.view.updates.rollback.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.reviews.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.scheduled.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.search.SearchAdDisplayable;
import cm.aptoide.pt.v8engine.view.search.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreAddCommentDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.store.subscribed.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.TimeLineStatsDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.TimeLineStatsWidget;
import cm.aptoide.pt.v8engine.view.timeline.login.TimelineLoginDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.login.TimelineLoginWidget;
import cm.aptoide.pt.v8engine.view.updates.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.updates.UpdatesHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SimilarDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialArticleDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialInstallDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialRecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialStoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialVideoDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.timeline.displayable.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.EmptyWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewCommentsWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewDescriptionWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewDeveloperWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewFlagThisWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewInstallWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewOtherVersionsWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewRateAndReviewsWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewRateResultsWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewScreenshotsWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewStoreWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewSuggestedAppWidget;
import cm.aptoide.pt.v8engine.view.app.widget.AppViewSuggestedAppsWidget;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadWidget;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadsHeaderWidget;
import cm.aptoide.pt.v8engine.view.store.AddMoreStoresWidget;
import cm.aptoide.pt.v8engine.view.store.home.AdultRowWidget;
import cm.aptoide.pt.v8engine.view.store.featured.AppBrickListWidget;
import cm.aptoide.pt.v8engine.view.store.featured.AppBrickWidget;
import cm.aptoide.pt.v8engine.view.comments.CommentWidget;
import cm.aptoide.pt.v8engine.view.downloads.completed.CompletedDownloadWidget;
import cm.aptoide.pt.v8engine.view.store.CreateStoreWidget;
import cm.aptoide.pt.v8engine.view.updates.excluded.ExcludedUpdateWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.FeatureWidget;
import cm.aptoide.pt.v8engine.view.timeline.follow.FollowStoreWidget;
import cm.aptoide.pt.v8engine.view.timeline.follow.FollowUserWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.FooterRowWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.FooterWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.GridAdWidget;
import cm.aptoide.pt.v8engine.view.app.GridAppListWidget;
import cm.aptoide.pt.v8engine.view.app.GridAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.GridDisplayWidget;
import cm.aptoide.pt.v8engine.view.store.GridStoreMetaWidget;
import cm.aptoide.pt.v8engine.view.store.GridStoreWidget;
import cm.aptoide.pt.v8engine.view.updates.installed.InstalledAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.MessageWhiteBgWidget;
import cm.aptoide.pt.v8engine.view.store.my.MyStoreWidget;
import cm.aptoide.pt.v8engine.view.app.OfficialAppWidget;
import cm.aptoide.pt.v8engine.view.app.widget.OtherVersionWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.ProgressBarWidget;
import cm.aptoide.pt.v8engine.view.updates.rollback.RollbackWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.RowReviewWidget;
import cm.aptoide.pt.v8engine.view.downloads.scheduled.ScheduledDownloadWidget;
import cm.aptoide.pt.v8engine.view.search.SearchAdWidget;
import cm.aptoide.pt.v8engine.view.search.SearchWidget;
import cm.aptoide.pt.v8engine.view.comments.StoreAddCommentWidget;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderWidget;
import cm.aptoide.pt.v8engine.view.comments.StoreLatestCommentsWidget;
import cm.aptoide.pt.v8engine.view.store.subscribed.SubscribedStoreWidget;
import cm.aptoide.pt.v8engine.view.updates.UpdateWidget;
import cm.aptoide.pt.v8engine.view.updates.UpdatesHeaderWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.AppUpdateWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.ArticleWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.RecommendationWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SimilarWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SocialArticleWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SocialInstallWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SocialRecommendationWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SocialStoreLatestAppsWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.SocialVideoWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.StoreLatestAppsWidget;
import cm.aptoide.pt.v8engine.view.timeline.widget.VideoWidget;
import cm.aptoide.pt.v8engine.view.comments.CommentsReadMoreDisplayable;
import cm.aptoide.pt.v8engine.view.comments.CommentsReadMoreWidget;
import cm.aptoide.pt.v8engine.view.comments.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.comments.RateAndReviewCommentWidget;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by neuro on 10-10-2016.
 */

public class DisplayableWidgetMapping {

  private static final String TAG = DisplayableWidgetMapping.class.getName();
  private static final DisplayableWidgetMapping instance = new DisplayableWidgetMapping();
  private List<Displayable> cachedDisplayables;
  private Class<? extends Displayable> displayableClass;
  private Class<? extends Widget> widgetClass;
  private Map<Integer, DisplayableWidgetMapping> viewTypeMapping = new HashMap<>();

  @Partners protected DisplayableWidgetMapping() {
    parseMappings(createMapping());
  }

  @Partners public DisplayableWidgetMapping(Class<? extends Widget> widgetClass,
      Class<? extends Displayable> displayableClass) {
    this.displayableClass = displayableClass;
    this.widgetClass = widgetClass;
  }

  public static DisplayableWidgetMapping getInstance() {
    return instance;
  }

  @Partners protected void parseMappings(@NonNull List<DisplayableWidgetMapping> mapping) {
    for (DisplayableWidgetMapping displayableWidgetMapping : mapping) {
      viewTypeMapping.put(displayableWidgetMapping.newDisplayable().getViewLayout(),
          displayableWidgetMapping);
    }
  }

  @Partners protected List<DisplayableWidgetMapping> createMapping() {

    LinkedList<DisplayableWidgetMapping> displayableWidgetMappings = new LinkedList<>();

    // empty widget
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(EmptyWidget.class, EmptyDisplayable.class));

    // common widgets / displayables
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AddMoreStoresWidget.class, AddMoreStoresDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppBrickWidget.class, AppBrickDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(FooterWidget.class, FooterDisplayable.class));
    displayableWidgetMappings.add(new DisplayableWidgetMapping(SubscribedStoreWidget.class,
        SubscribedStoreDisplayable.class));

    // grid widgets / displayables
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridAppWidget.class, GridAppDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridDisplayWidget.class, GridDisplayDisplayable.class));
    displayableWidgetMappings.add(new DisplayableWidgetMapping(StoreGridHeaderWidget.class,
        StoreGridHeaderDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(FooterRowWidget.class, FooterRowDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridStoreWidget.class, GridStoreDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridStoreMetaWidget.class, GridStoreMetaDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridAdWidget.class, GridAdDisplayable.class));

    // Multi Layout
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridAppListWidget.class, GridAppListDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppBrickListWidget.class, AppBrickListDisplayable.class));

    // Updates
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(InstalledAppWidget.class, InstalledAppDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(UpdateWidget.class, UpdateDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ExcludedUpdateWidget.class, ExcludedUpdateDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(UpdatesHeaderWidget.class, UpdatesHeaderDisplayable.class));

    // Social Timeline
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ArticleWidget.class, ArticleDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(FeatureWidget.class, FeatureDisplayable.class));
    displayableWidgetMappings.add(new DisplayableWidgetMapping(StoreLatestAppsWidget.class,
        StoreLatestAppsDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppUpdateWidget.class, AppUpdateDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(VideoWidget.class, VideoDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SimilarWidget.class, SimilarDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(RecommendationWidget.class, RecommendationDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SocialArticleWidget.class, SocialArticleDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SocialVideoWidget.class, SocialVideoDisplayable.class));
    displayableWidgetMappings.add(new DisplayableWidgetMapping(SocialStoreLatestAppsWidget.class,
        SocialStoreLatestAppsDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SocialInstallWidget.class, SocialInstallDisplayable.class));
    displayableWidgetMappings.add(new DisplayableWidgetMapping(SocialRecommendationWidget.class,
        SocialRecommendationDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(RollbackWidget.class, RollbackDisplayable.class));

    // Search
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SearchWidget.class, SearchDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(SearchAdWidget.class, SearchAdDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AdultRowWidget.class, AdultRowDisplayable.class));

    // Loading
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ProgressBarWidget.class, ProgressBarDisplayable.class));

    // appView widgets / displayables
    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewCommentsWidget.class,
        AppViewCommentsDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewDescriptionWidget.class,
        AppViewDescriptionDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewDeveloperWidget.class,
        AppViewDeveloperDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewScreenshotsWidget.class,
        AppViewScreenshotsDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppViewInstallWidget.class, AppViewInstallDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewRateAndReviewsWidget.class,
        AppViewRateAndCommentsDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewFlagThisWidget.class,
        AppViewFlagThisDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewOtherVersionsWidget.class,
        AppViewOtherVersionsDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewRateResultsWidget.class,
        AppViewRateResultsDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppViewStoreWidget.class, AppViewStoreDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewSuggestedAppsWidget.class,
        AppViewSuggestedAppsDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewSuggestedAppWidget.class,
        AppViewSuggestedAppDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(OtherVersionWidget.class, OtherVersionDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(RateAndReviewCommentWidget.class,
        RateAndReviewCommentDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(ScheduledDownloadWidget.class,
        ScheduledDownloadDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(CompletedDownloadWidget.class,
        CompletedDownloadDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ActiveDownloadWidget.class, ActiveDownloadDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(ActiveDownloadsHeaderWidget.class,
        ActiveDownloadsHeaderDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(RowReviewWidget.class, RowReviewDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(CommentWidget.class, CommentDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((CommentsReadMoreWidget.class),
        CommentsReadMoreDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((StoreLatestCommentsWidget.class),
        StoreLatestCommentsDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((StoreAddCommentWidget.class),
        StoreAddCommentDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((CreateStoreWidget.class), CreateStoreDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((MyStoreWidget.class), MyStoreDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((RecommendedStoreWidget.class),
        RecommendedStoreDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((OfficialAppWidget.class), OfficialAppDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((TimeLineStatsWidget.class), TimeLineStatsDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((FollowUserWidget.class), FollowUserDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((MessageWhiteBgWidget.class),
        MessageWhiteBgDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((TimelineLoginWidget.class), TimelineLoginDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((FollowStoreWidget.class), FollowStoreDisplayable.class));
    return displayableWidgetMappings;
  }

  @Partners @Nullable public Displayable newDisplayable() {
    try {
      return displayableClass.newInstance();
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
      String errMsg =
          String.format("Error instantiating displayable '%s'", displayableClass.getName());
      Logger.e(TAG, errMsg, e);
      throw new RuntimeException(errMsg);
    }
  }

  public Widget newWidget(View view, int viewType) {
    DisplayableWidgetMapping displayableWidgetMapping = viewTypeMapping.get(viewType);
    if (displayableWidgetMapping != null) {
      return displayableWidgetMapping.newWidget(view);
    }

    throw new IllegalStateException(String.format("There's no widget for '%s' viewType", viewType)
        + "\nDid you forget to add the mapping to DisplayableWidgetMapping enum??");
  }

  @Nullable private Widget newWidget(View view) {
    Class[] cArg = new Class[1];
    cArg[0] = View.class;
    try {
      return widgetClass.getDeclaredConstructor(cArg).newInstance(view);
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
      String errMsg = String.format("Error instantiating widget '%s'", widgetClass.getName());
      Logger.e(TAG, errMsg, e);
      throw new RuntimeException(errMsg);
    }
  }

  public List<Displayable> getCachedDisplayables() {
    if (cachedDisplayables == null) {
      List<Displayable> tmp = new LinkedList<>();

      for (DisplayableWidgetMapping displayableWidgetMapping : viewTypeMapping.values()) {
        tmp.add(displayableWidgetMapping.newDisplayable());
      }
      cachedDisplayables = Collections.unmodifiableList(tmp);
    }
    return cachedDisplayables;
  }

  /**
   * needed in the partners to get the displayableClass
   *
   * @return displayableClass
   */
  @Partners public Class<? extends Displayable> getDisplayableClass() {
    return displayableClass;
  }

  /**
   * needed in the partners to get the widgetClass
   *
   * @return widgetClass
   */
  @Partners public Class<? extends Widget> getWidgetClass() {
    return widgetClass;
  }

  /**
   * needed in partners to add it's own displayables/widgets
   *
   * @return Map of widgets and displayables
   */
  @Partners protected Map<Integer, DisplayableWidgetMapping> getViewTypeMapping() {
    return viewTypeMapping;
  }
}
