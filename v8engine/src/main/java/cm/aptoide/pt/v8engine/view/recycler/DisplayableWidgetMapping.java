package cm.aptoide.pt.v8engine.view.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewInstallDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateResultsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewScreenshotsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AddMoreStoresDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AdultRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CreateStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ExcludedUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MessageWhiteBgDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MyStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OtherVersionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RecommendedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RecommendedStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreAddCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimeLineStatsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimeLineStatsWidget;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimelineLoginDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.TimelineLoginWidget;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
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
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.EmptyWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewCommentsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewDescriptionWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewDeveloperWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewFlagThisWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewInstallWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewOtherVersionsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewRateAndReviewsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewRateResultsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewScreenshotsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewSuggestedAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewSuggestedAppsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ActiveDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ActiveDownloadsHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AddMoreStoresWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AdultRowWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AppBrickListWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AppBrickWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CommentWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CompletedDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CreateStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ExcludedUpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FeatureWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FollowStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FollowUserWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterRowWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAdWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppListWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridDisplayWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridStoreMetaWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.InstalledAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.MessageWhiteBgWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.MyStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.OfficialAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.OtherVersionWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ProgressBarWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RollbackWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RowReviewWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ScheduledDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SearchAdWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SearchWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.StoreAddCommentWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.StoreGridHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.StoreLatestCommentsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SubscribedStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.UpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.UpdatesHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.AppUpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.ArticleWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.RecommendationWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SimilarWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SocialArticleWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SocialInstallWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SocialRecommendationWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SocialStoreLatestAppsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.SocialVideoWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.StoreLatestAppsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline.VideoWidget;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentsReadMoreDisplayable;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentsReadMoreWidget;
import cm.aptoide.pt.viewRateAndCommentReviews.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.viewRateAndCommentReviews.RateAndReviewCommentWidget;
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
