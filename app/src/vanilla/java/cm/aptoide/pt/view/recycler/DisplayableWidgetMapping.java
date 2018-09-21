package cm.aptoide.pt.view.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.account.view.LoginDisplayable;
import cm.aptoide.pt.account.view.user.CreateStoreDisplayable;
import cm.aptoide.pt.app.view.GridAppDisplayable;
import cm.aptoide.pt.app.view.GridAppListDisplayable;
import cm.aptoide.pt.app.view.GridAppListWidget;
import cm.aptoide.pt.app.view.GridAppWidget;
import cm.aptoide.pt.app.view.OfficialAppDisplayable;
import cm.aptoide.pt.app.view.OfficialAppWidget;
import cm.aptoide.pt.app.view.OtherVersionDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewDescriptionDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewFlagThisDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewInstallDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewRateAndCommentsDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewRateResultsDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewRewardAppDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewStoreDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewSuggestedAdDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.app.view.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.app.view.widget.AppViewDescriptionWidget;
import cm.aptoide.pt.app.view.widget.AppViewFlagThisWidget;
import cm.aptoide.pt.app.view.widget.AppViewInstallWidget;
import cm.aptoide.pt.app.view.widget.AppViewOtherVersionsWidget;
import cm.aptoide.pt.app.view.widget.AppViewRateAndReviewsWidget;
import cm.aptoide.pt.app.view.widget.AppViewRateResultsWidget;
import cm.aptoide.pt.app.view.widget.AppViewRewardAppWidget;
import cm.aptoide.pt.app.view.widget.AppViewStoreWidget;
import cm.aptoide.pt.app.view.widget.AppViewSuggestedAdWidget;
import cm.aptoide.pt.app.view.widget.AppViewSuggestedAppWidget;
import cm.aptoide.pt.app.view.widget.AppViewSuggestedAppsWidget;
import cm.aptoide.pt.app.view.widget.OtherVersionWidget;
import cm.aptoide.pt.comments.view.CommentDisplayable;
import cm.aptoide.pt.comments.view.CommentWidget;
import cm.aptoide.pt.comments.view.CommentsReadMoreDisplayable;
import cm.aptoide.pt.comments.view.CommentsReadMoreWidget;
import cm.aptoide.pt.comments.view.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.comments.view.RateAndReviewCommentWidget;
import cm.aptoide.pt.comments.view.StoreAddCommentWidget;
import cm.aptoide.pt.comments.view.StoreCommentDisplayable;
import cm.aptoide.pt.comments.view.StoreCommentWidget;
import cm.aptoide.pt.comments.view.StoreLatestCommentsWidget;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.home.GridAppCoinsRewardAppsDisplayable;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.reviews.ReviewsLanguageFilterDisplayable;
import cm.aptoide.pt.reviews.ReviewsLanguageFilterWidget;
import cm.aptoide.pt.reviews.ReviewsRatingDisplayable;
import cm.aptoide.pt.reviews.ReviewsRatingWidget;
import cm.aptoide.pt.reviews.RowReviewDisplayable;
import cm.aptoide.pt.store.view.CreateStoreWidget;
import cm.aptoide.pt.store.view.GridDisplayDisplayable;
import cm.aptoide.pt.store.view.GridStoreDisplayable;
import cm.aptoide.pt.store.view.GridStoreMetaDisplayable;
import cm.aptoide.pt.store.view.GridStoreMetaWidget;
import cm.aptoide.pt.store.view.GridStoreWidget;
import cm.aptoide.pt.store.view.LoginWidget;
import cm.aptoide.pt.store.view.StoreAddCommentDisplayable;
import cm.aptoide.pt.store.view.StoreGridHeaderDisplayable;
import cm.aptoide.pt.store.view.StoreGridHeaderWidget;
import cm.aptoide.pt.store.view.StoreLatestCommentsDisplayable;
import cm.aptoide.pt.store.view.featured.AppBrickDisplayable;
import cm.aptoide.pt.store.view.featured.AppBrickListDisplayable;
import cm.aptoide.pt.store.view.featured.AppBrickListWidget;
import cm.aptoide.pt.store.view.featured.AppBrickWidget;
import cm.aptoide.pt.store.view.home.AdultRowDisplayable;
import cm.aptoide.pt.store.view.home.AdultRowWidget;
import cm.aptoide.pt.store.view.my.MyStoreDisplayable;
import cm.aptoide.pt.store.view.my.MyStoreWidget;
import cm.aptoide.pt.store.view.recommended.RecommendedStoreDisplayable;
import cm.aptoide.pt.store.view.recommended.RecommendedStoreWidget;
import cm.aptoide.pt.timeline.view.displayable.FollowStoreDisplayable;
import cm.aptoide.pt.timeline.view.displayable.FollowUserDisplayable;
import cm.aptoide.pt.timeline.view.displayable.TimeLineStatsDisplayable;
import cm.aptoide.pt.timeline.view.displayable.TimeLineStatsWidget;
import cm.aptoide.pt.timeline.view.follow.FollowStoreWidget;
import cm.aptoide.pt.timeline.view.follow.FollowUserWidget;
import cm.aptoide.pt.timeline.view.login.TimelineLoginDisplayable;
import cm.aptoide.pt.timeline.view.login.TimelineLoginWidget;
import cm.aptoide.pt.updates.view.excluded.ExcludedUpdateDisplayable;
import cm.aptoide.pt.updates.view.excluded.ExcludedUpdateWidget;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.EmptyDisplayable;
import cm.aptoide.pt.view.recycler.displayable.FooterRowDisplayable;
import cm.aptoide.pt.view.recycler.displayable.GridAdDisplayable;
import cm.aptoide.pt.view.recycler.displayable.MessageWhiteBgDisplayable;
import cm.aptoide.pt.view.recycler.displayable.ProgressBarDisplayable;
import cm.aptoide.pt.view.recycler.widget.EmptyWidget;
import cm.aptoide.pt.view.recycler.widget.FooterRowWidget;
import cm.aptoide.pt.view.recycler.widget.GridAdWidget;
import cm.aptoide.pt.view.recycler.widget.GridAppCoinsRewardAppsWidget;
import cm.aptoide.pt.view.recycler.widget.GridDisplayWidget;
import cm.aptoide.pt.view.recycler.widget.MessageWhiteBgWidget;
import cm.aptoide.pt.view.recycler.widget.ProgressBarWidget;
import cm.aptoide.pt.view.recycler.widget.RowReviewWidget;
import cm.aptoide.pt.view.recycler.widget.Widget;
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

  protected DisplayableWidgetMapping() {
    parseMappings(createMapping());
  }

  public DisplayableWidgetMapping(Class<? extends Widget> widgetClass,
      Class<? extends Displayable> displayableClass) {
    this.displayableClass = displayableClass;
    this.widgetClass = widgetClass;
  }

  public static DisplayableWidgetMapping getInstance() {
    return instance;
  }

  protected void parseMappings(@NonNull List<DisplayableWidgetMapping> mapping) {
    for (DisplayableWidgetMapping displayableWidgetMapping : mapping) {
      viewTypeMapping.put(displayableWidgetMapping.newDisplayable()
          .getViewLayout(), displayableWidgetMapping);
    }
  }

  protected List<DisplayableWidgetMapping> createMapping() {

    LinkedList<DisplayableWidgetMapping> displayableWidgetMappings = new LinkedList<>();

    // empty widget
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(EmptyWidget.class, EmptyDisplayable.class));

    // common widgets / displayables
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppBrickWidget.class, AppBrickDisplayable.class));
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
    displayableWidgetMappings.add(new DisplayableWidgetMapping(GridAppCoinsRewardAppsWidget.class,
        GridAppCoinsRewardAppsDisplayable.class));

    // Multi Layout
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(GridAppListWidget.class, GridAppListDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppBrickListWidget.class, AppBrickListDisplayable.class));

    // Updates
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ExcludedUpdateWidget.class, ExcludedUpdateDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AdultRowWidget.class, AdultRowDisplayable.class));

    // Loading
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(ProgressBarWidget.class, ProgressBarDisplayable.class));

    // appView widgets / displayables
    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewDescriptionWidget.class,
        AppViewDescriptionDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(AppViewInstallWidget.class, AppViewInstallDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping((AppViewRewardAppWidget.class),
        AppViewRewardAppDisplayable.class));

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

    displayableWidgetMappings.add(new DisplayableWidgetMapping(AppViewSuggestedAdWidget.class,
        AppViewSuggestedAdDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(OtherVersionWidget.class, OtherVersionDisplayable.class));

    displayableWidgetMappings.add(new DisplayableWidgetMapping(RateAndReviewCommentWidget.class,
        RateAndReviewCommentDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(RowReviewWidget.class, RowReviewDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(CommentWidget.class, CommentDisplayable.class));
    displayableWidgetMappings.add(
        new DisplayableWidgetMapping(StoreCommentWidget.class, StoreCommentDisplayable.class));

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

    displayableWidgetMappings.add(new DisplayableWidgetMapping((ReviewsLanguageFilterWidget.class),
        ReviewsLanguageFilterDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((ReviewsRatingWidget.class), ReviewsRatingDisplayable.class));

    displayableWidgetMappings.add(
        new DisplayableWidgetMapping((LoginWidget.class), LoginDisplayable.class));
    return displayableWidgetMappings;
  }

  @Nullable public Displayable newDisplayable() {
    try {
      return displayableClass.newInstance();
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
      String errMsg =
          String.format("Error instantiating displayable '%s'", displayableClass.getName());
      Logger.getInstance()
          .e(TAG, errMsg, e);
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
      return widgetClass.getDeclaredConstructor(cArg)
          .newInstance(view);
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
      String errMsg = String.format("Error instantiating widget '%s'", widgetClass.getName());
      Logger.getInstance()
          .e(TAG, errMsg, e);
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
  public Class<? extends Displayable> getDisplayableClass() {
    return displayableClass;
  }

  /**
   * needed in the partners to get the widgetClass
   *
   * @return widgetClass
   */
  public Class<? extends Widget> getWidgetClass() {
    return widgetClass;
  }

  /**
   * needed in partners to add it's own displayables/widgets
   *
   * @return Map of widgets and displayables
   */
  protected Map<Integer, DisplayableWidgetMapping> getViewTypeMapping() {
    return viewTypeMapping;
  }
}
