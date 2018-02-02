package cm.aptoide.pt.store.view.recommended;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreWidget extends Widget<RecommendedStoreDisplayable> {

  private TextView storeName;
  private TextView followingUsers;
  private TextView numberStoreApps;
  private ImageView storeIcon;
  private AppCompatButton followButton;
  private StoreAnalytics storeAnalytics;

  public RecommendedStoreWidget(View itemView) {
    super(itemView);
    NavigationTracker navigationTracker = ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    AnalyticsManager analyticsManager = ((AptoideApplication) getContext().getApplicationContext()).getAnalyticsManager();
    storeAnalytics =
        new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected void assignViews(View itemView) {
    storeName = (TextView) itemView.findViewById(R.id.recommended_store_name);
    followingUsers = (TextView) itemView.findViewById(R.id.recommended_store_users);
    numberStoreApps = (TextView) itemView.findViewById(R.id.recommended_store_apps);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    followButton = (AppCompatButton) itemView.findViewById(R.id.recommended_store_action);
  }

  @Override public void bindView(RecommendedStoreDisplayable displayable) {
    Store store = displayable.getPojo();
    storeName.setText(store.getName());
    followingUsers.setText(String.valueOf(store.getStats()
        .getSubscribers()));
    numberStoreApps.setText(String.valueOf(store.getStats()
        .getApps()));
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(store.getAvatar(), storeIcon, StoreTheme.get(store)
            .getStoreHeaderColorResource(context.getResources(), context.getTheme()));
    setFollowButtonListener(displayable);
    setButtonText(displayable);
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(click -> {
          displayable.openStoreFragment(getFragmentNavigator());
          if (!displayable.getOrigin()
              .isEmpty()) {
            storeAnalytics.sendStoreOpenEvent(displayable.getOrigin(), store.getName(), false);
            storeAnalytics.sendStoreTabInteractEvent("More Recommended Stores", false);
          } else {
            storeAnalytics.sendStoreOpenEvent("Recommended Stores", store.getName(), false);
            storeAnalytics.sendStoreTabInteractEvent("Open a Recommended Store", false);
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void setFollowButtonListener(RecommendedStoreDisplayable displayable) {
    compositeSubscription.add(RxView.clicks(followButton)
        .flatMap(click -> {
          followButton.setEnabled(false);
          storeAnalytics.sendStoreTabInteractEvent("Follow a Recommended Store",
              displayable.getPojo()
                  .getStats()
                  .getApps(), displayable.getPojo()
                  .getStats()
                  .getSubscribers());
          return displayable.isFollowing()
              .first()
              .observeOn(Schedulers.computation())
              .map(isSubscribed -> {
                if (isSubscribed) {
                  displayable.unsubscribeStore(getContext().getApplicationContext());
                } else {
                  displayable.subscribeStore(getContext());
                }
                return !isSubscribed;
              });
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribing -> {
          followButton.setEnabled(true);
          int message;
          if (isSubscribing) {
            message = R.string.store_followed;
          } else {
            message = R.string.unfollowing_store_message;
          }
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(message, getContext().getResources(),
                  displayable.getPojo()
                      .getName()));
        }, throwable -> {
          CrashReport.getInstance()
              .log(throwable);
          ShowMessage.asSnack(itemView, R.string.error_occured);
        }));
  }

  private void setButtonText(RecommendedStoreDisplayable displayable) {
    followButton.setVisibility(View.GONE);
    compositeSubscription.add(displayable.isFollowing()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          int message;
          if (isSubscribed) {
            message = R.string.followed;
          } else {
            message = R.string.follow;
          }
          followButton.setText(
              AptoideUtils.StringU.getFormattedString(message, getContext().getResources(),
                  displayable.getPojo()
                      .getName()));
          followButton.setVisibility(View.VISIBLE);
        }));
  }
}
