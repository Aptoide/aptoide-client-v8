/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.RecommendationDisplayable;
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
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentsReadMoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ExcludedUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppGraphicDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.InstalledAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OtherVersionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RateAndReviewCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RollbackDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ScheduledDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.UpdatesHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;
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
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AppUpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ArticleWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CommentWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CommentsReadMoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.CompletedDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ExcludedUpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FeatureWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterRowWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAdWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppListWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridDisplayWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridStoreMetaWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.InstalledAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.OtherVersionWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ProgressBarWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RateAndReviewCommentWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RecommendationWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RollbackWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.RowReviewWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.ScheduledDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SearchAdWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SearchWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.StoreGridHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.StoreLatestAppsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SubscribedStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.UpdateWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.UpdatesHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.VideoWidget;

/**
 * Created by neuro on 11-05-2016.
 */
public enum DisplayableType {

	// empty widget
	EMPTY(EmptyWidget.class, EmptyDisplayable.class),

	// common widgets / displayables
	ADD_MORE_STORES(AddMoreStoresWidget.class, AddMoreStoresDisplayable.class),
	APP_BRICK(AppBrickWidget.class, AppBrickDisplayable.class),
	FOOTER(FooterWidget.class, FooterDisplayable.class),
	SUBSCRIBED_STORE(SubscribedStoreWidget.class, SubscribedStoreDisplayable.class),

	// grid widgets / displayables
	GRID_APP(GridAppWidget.class, GridAppDisplayable.class),
	GRID_DISPLAY(GridDisplayWidget.class, GridDisplayDisplayable.class),
	GRID_HEADER(StoreGridHeaderWidget.class, StoreGridHeaderDisplayable.class),
	FOOTER_ROW(FooterRowWidget.class, FooterRowDisplayable.class),
	GRID_STORE(GridStoreWidget.class, GridStoreDisplayable.class),
	STORE_META(GridStoreMetaWidget.class, GridStoreMetaDisplayable.class),
	ADS(GridAdWidget.class, GridAdDisplayable.class),

	// Multi Layout
	APPS_GROUP_LIST(GridAppListWidget.class, GridAppListDisplayable.class),
	APPS_GROUP_GRAPHIC(AppBrickListWidget.class, GridAppGraphicDisplayable.class),

	// Updates
	INSTALLED_APP(InstalledAppWidget.class, InstalledAppDisplayable.class),
	UPDATE(UpdateWidget.class, UpdateDisplayable.class),
	EXCLUDED_UPDATE(ExcludedUpdateWidget.class, ExcludedUpdateDisplayable.class),
	UPDATES_HEADER(UpdatesHeaderWidget.class, UpdatesHeaderDisplayable.class),

	// Social Timeline
	SOCIAL_TIMELINE_ARTICLE(ArticleWidget.class, ArticleDisplayable.class),
	SOCIAL_TIMELINE_FEATURE(FeatureWidget.class, FeatureDisplayable.class),
	SOCIAL_TIMELINE_STORE_LATEST_APPS(StoreLatestAppsWidget.class, StoreLatestAppsDisplayable.class),
	SOCIAL_TIMELINE_STORE_APP_UPDATE(AppUpdateWidget.class, AppUpdateDisplayable.class),
	SOCIAL_TIMELINE_RECOMMENDATION(RecommendationWidget.class, RecommendationDisplayable.class),
	SOCIAL_TIMELINE_VIDEO(VideoWidget.class, VideoDisplayable.class),

	ROLLBACK(RollbackWidget.class, RollbackDisplayable.class),

	// Search
	SEARCH(SearchWidget.class, SearchDisplayable.class),
	SEARCH_AD(SearchAdWidget.class, SearchAdDisplayable.class),
	ADULT_ROW_SWITCH(AdultRowWidget.class, AdultRowDisplayable.class),

	// Loading
	PROGRESS(ProgressBarWidget.class, ProgressBarDisplayable.class),

	// appView widgets / displayables
	APP_VIEW_COMMENTS(AppViewCommentsWidget.class, AppViewCommentsDisplayable.class),

	APP_VIEW_DESCRIPTION(AppViewDescriptionWidget.class, AppViewDescriptionDisplayable.class),

	APP_VIEW_DEVELOPER(AppViewDeveloperWidget.class, AppViewDeveloperDisplayable.class),

	APP_VIEW_SCREENSHOTS(AppViewScreenshotsWidget.class, AppViewScreenshotsDisplayable.class),

	APP_VIEW_INSTALL(AppViewInstallWidget.class, AppViewInstallDisplayable.class),

	APP_VIEW_RATE_AND_COMMENTS(AppViewRateAndReviewsWidget.class, AppViewRateAndCommentsDisplayable.class),

	APP_VIEW_FLAG_THIS(AppViewFlagThisWidget.class, AppViewFlagThisDisplayable.class),

	APP_VIEW_OTHER_VERSIONS(AppViewOtherVersionsWidget.class, AppViewOtherVersionsDisplayable.class),

	APP_VIEW_RATE_RESULTS(AppViewRateResultsWidget.class, AppViewRateResultsDisplayable.class),

	//APP_VIEW_RATING(AppViewRateThisWidget.class, AppViewRateThisDisplayable.class),

	APP_VIEW_SUBSCRIPTION(AppViewStoreWidget.class, AppViewStoreDisplayable.class),

	APP_VIEW_SUGGESTED_APPS(AppViewSuggestedAppsWidget.class, AppViewSuggestedAppsDisplayable.class),
	APP_VIEW_SUGGESTED_APP(AppViewSuggestedAppWidget.class, AppViewSuggestedAppDisplayable.class),

	OTHER_VERSION(OtherVersionWidget.class, OtherVersionDisplayable.class),
	RATE_AND_REVIEW(RateAndReviewCommentWidget.class, RateAndReviewCommentDisplayable.class),
	SCHEDULED_DOWNLOAD(ScheduledDownloadWidget.class, ScheduledDownloadDisplayable.class),
	COMPLETED_DOWNLOAD(CompletedDownloadWidget.class, CompletedDownloadDisplayable.class),
	ACTIVE_DOWNLOAD(ActiveDownloadWidget.class, ActiveDownloadDisplayable.class),
	ACTIVE_DOWNLOAD_HEADER(ActiveDownloadsHeaderWidget.class, ActiveDownloadsHeaderDisplayable.class),
	REVIEWS_GROUP(RowReviewWidget.class, RowReviewDisplayable.class),
	APP_COMMENT_TO_REVIEW(CommentWidget.class, CommentDisplayable.class),
	READ_MORE_COMMENTS(CommentsReadMoreWidget.class, CommentsReadMoreDisplayable.class);

	private static final String TAG = DisplayableType.class.getName();
	private static List<Displayable> cachedDisplayables;
	private Displayable displayable;
	//private Widget widget;
	private Class<? extends Displayable> displayableClass;
	private Class<? extends Widget> widgetClass;

	DisplayableType(Class<? extends Widget> widgetClass, Class<? extends Displayable> displayableClass) {
		this.displayableClass = displayableClass;
		this.widgetClass = widgetClass;

		displayable = newDisplayable();
		//widget = newWidget(new View(V8Engine.getContext()));
	}

	public static List<Displayable> newDisplayables(Group group) {
		ArrayList<Displayable> displayables = new ArrayList<>(group.displayableTypes.length);
		for (int i = 0 ; i < group.displayableTypes.length ; i++) {
			displayables.add(group.displayableTypes[i].newDisplayable());
		}
		return displayables;
	}

	public static <T> List<DisplayablePojo> newDisplayables(Group group, T pojo) {
		ArrayList<DisplayablePojo> displayablePojos = new ArrayList<>(group.displayableTypes.length);

		for (int i = 0 ; i < group.displayableTypes.length ; i++) {
			displayablePojos.add(((DisplayablePojo) group.displayableTypes[i].newDisplayable()).setPojo(pojo));
		}
		return displayablePojos;
	}

	public static Displayable newDisplayable(Type type, App app) {
		return ((DisplayablePojo) newDisplayable(type)).setPojo(app);
	}

	public static Displayable newDisplayable(Type type, Store store) {
		return ((DisplayablePojo) newDisplayable(type)).setPojo(store);
	}

	public static Displayable newDisplayable(Type type, GetAdsResponse.Ad ad) {
		return ((DisplayablePojo) newDisplayable(type)).setPojo(ad);
	}

	public static Displayable newDisplayable(Type type) {
		for (DisplayableType displayableType2 : values()) {
			if (displayableType2.displayable.getType() == type) {
				return displayableType2.newDisplayable();
			}
		}

		throw new IllegalStateException(String.format("There is no displayable for '%s' type", type));
	}

	public static Widget newWidget(View view, int viewType) {
		for (DisplayableType displayableType2 : values()) {
			if (displayableType2.displayable.getViewLayout() == viewType) {
				return displayableType2.newWidget(view);
			}
		}

		throw new IllegalStateException(String.format("There's no widget for '%s' viewType", viewType) + "\nDid you forget to add the mapping to " +
				"DisplayableType enum??");
	}

	public static List<Displayable> getCachedDisplayables() {
		if (cachedDisplayables == null) {
			List<Displayable> tmp = new LinkedList<>();

			for (DisplayableType displayableType2 : values()) {
				tmp.add(displayableType2.newDisplayable());
			}
			cachedDisplayables = Collections.unmodifiableList(tmp);
		}
		return cachedDisplayables;
	}

	@Nullable
	private Widget newWidget(View view) {
		Class[] cArg = new Class[1];
		cArg[0] = View.class;
		try {
			return widgetClass.getDeclaredConstructor(cArg).newInstance(view);
		} catch (Exception e) {
			String errMsg = String.format("Error instantiating widget '%s'", widgetClass.getName());
			Logger.e(TAG, errMsg, e);
			throw new RuntimeException(errMsg);
		}
	}

	@Nullable
	public Displayable newDisplayable() {
		try {
			return displayableClass.newInstance();
		} catch (Exception e) {
			String errMsg = String.format("Error instantiating displayable '%s'", displayableClass.getName());
			Logger.e(TAG, errMsg, e);
			throw new RuntimeException(errMsg);
		}
	}

	public enum Group {
		APP_VIEW(APP_VIEW_INSTALL,
				//				APP_VIEW_SUBSCRIPTION,
				APP_VIEW_DESCRIPTION, APP_VIEW_SCREENSHOTS,
				//              APP_VIEW_RATING,
				//				APP_VIEW_RATE_RESULTS,
				APP_VIEW_COMMENTS,
				//				APP_VIEW_OTHER_VERSIONS,
				APP_VIEW_DEVELOPER);

		public final DisplayableType[] displayableTypes;

		Group(DisplayableType... displayableTypes) {
			this.displayableTypes = displayableTypes;
		}
	}
}
