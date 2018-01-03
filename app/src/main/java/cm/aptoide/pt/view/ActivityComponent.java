package cm.aptoide.pt.view;

import cm.aptoide.pt.account.view.LoginActivity;
import cm.aptoide.pt.app.view.GridAppListWidget;
import cm.aptoide.pt.app.view.OfficialAppWidget;
import cm.aptoide.pt.app.view.widget.AppViewDescriptionWidget;
import cm.aptoide.pt.app.view.widget.AppViewFlagThisWidget;
import cm.aptoide.pt.app.view.widget.AppViewInstallWidget;
import cm.aptoide.pt.app.view.widget.AppViewRateAndReviewsWidget;
import cm.aptoide.pt.app.view.widget.AppViewScreenshotsWidget;
import cm.aptoide.pt.app.view.widget.AppViewStoreWidget;
import cm.aptoide.pt.app.view.widget.AppViewSuggestedAppsWidget;
import cm.aptoide.pt.app.view.widget.OtherVersionWidget;
import cm.aptoide.pt.billing.view.BillingActivity;
import cm.aptoide.pt.comments.view.StoreLatestCommentsWidget;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.permission.PermissionServiceActivity;
import cm.aptoide.pt.spotandshare.view.SpotSharePreviewActivity;
import cm.aptoide.pt.store.view.AddStoreDialog;
import cm.aptoide.pt.store.view.CreateStoreWidget;
import cm.aptoide.pt.store.view.GridStoreMetaWidget;
import cm.aptoide.pt.store.view.GridStoreWidget;
import cm.aptoide.pt.store.view.featured.AppBrickListWidget;
import cm.aptoide.pt.store.view.featured.AppBrickWidget;
import cm.aptoide.pt.store.view.my.MyStoreWidget;
import cm.aptoide.pt.store.view.recommended.RecommendedStoreWidget;
import cm.aptoide.pt.store.view.subscribed.SubscribedStoreWidget;
import cm.aptoide.pt.timeline.post.PostActivity;
import cm.aptoide.pt.timeline.view.displayable.TimeLineStatsWidget;
import cm.aptoide.pt.timeline.view.follow.FollowUserWidget;
import cm.aptoide.pt.updates.view.UpdateWidget;
import cm.aptoide.pt.updates.view.rollback.RollbackWidget;
import cm.aptoide.pt.view.recycler.widget.FooterWidget;
import cm.aptoide.pt.view.recycler.widget.GridAdWidget;
import cm.aptoide.pt.view.recycler.widget.GridDisplayWidget;
import cm.aptoide.pt.view.recycler.widget.RowReviewWidget;
import dagger.Subcomponent;

@ActivityScope @Subcomponent(modules = { ActivityModule.class })
public interface ActivityComponent {

  void inject(MainActivity activity);

  void inject(ActivityResultNavigator activityResultNavigator);

  void inject(PermissionServiceActivity activityResultNavigator);

  void inject(BillingActivity activityResultNavigator);

  void inject(SpotSharePreviewActivity activity);

  void inject(PostActivity activity);

  void inject(LoginActivity activityResultNavigator);

  FragmentComponent plus(FragmentModule fragmentModule);

  void inject(AddStoreDialog dialog);

  void inject(StoreLatestCommentsWidget storeLatestCommentsWidget);

  void inject(AppViewInstallWidget appViewInstallWidget);

  void inject(AppViewRateAndReviewsWidget appViewRateAndReviewsWidget);

  void inject(OtherVersionWidget otherVersionWidget);

  void inject(AppViewScreenshotsWidget appViewScreenshotsWidget);

  void inject(OfficialAppWidget officialAppWidget);

  void inject(GridAppListWidget gridAppListWidget);

  void inject(RollbackWidget rollbackWidget);

  void inject(UpdateWidget updateWidget);

  void inject(GridDisplayWidget gridDisplayWidget);

  void inject(RowReviewWidget rowReviewWidget);

  void inject(FooterWidget footerWidget);

  void inject(GridStoreMetaWidget gridStoreMetaWidget);

  void inject(FollowUserWidget followUserWidget);

  void inject(RecommendedStoreWidget recommendedStoreWidget);

  void inject(SubscribedStoreWidget subscribedStoreWidget);

  void inject(GridStoreWidget gridStoreWidget);

  void inject(MyStoreWidget myStoreWidget);

  void inject(AppBrickWidget appBrickWidget);

  void inject(AppViewStoreWidget appViewStoreWidget);

  void inject(AppBrickListWidget appBrickListWidget);

  void inject(CreateStoreWidget createStoreWidget);

  void inject(TimeLineStatsWidget timeLineStatsWidget);

  void inject(AppViewFlagThisWidget appViewFlagThisWidget);

  void inject(AppViewSuggestedAppsWidget appViewSuggestedAppsWidget);

  void inject(AppViewDescriptionWidget appViewDescriptionWidget);

  void inject(GridAdWidget gridAdWidget);
}
