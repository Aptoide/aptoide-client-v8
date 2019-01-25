package cm.aptoide.pt.store.view.my;

import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.view.MetaStoresBaseWidget;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.spannable.SpannableFactory;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 05/12/2016.
 */

public class StoreWidget extends MetaStoresBaseWidget<StoreDisplayable> {

  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;
  private TextView suggestionMessage;
  private StoreAnalytics storeAnalytics;
  private TextView firstStat;
  private TextView secondStat;

  public StoreWidget(View itemView) {
    super(itemView);
    NavigationTracker navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    AnalyticsManager analyticsManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAnalyticsManager();
    storeAnalytics = new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected void assignViews(View itemView) {
    storeIcon = itemView.findViewById(R.id.store_icon);
    storeName = itemView.findViewById(R.id.store_name);
    suggestionMessage = itemView.findViewById(R.id.create_store_text);
    firstStat = itemView.findViewById(R.id.first_stat);
    secondStat = itemView.findViewById(R.id.second_stat);
    exploreButton = itemView.findViewById(R.id.explore_button);
  }

  @Override public void bindView(StoreDisplayable displayable) {
    final FragmentActivity context = getContext();
    Store store = displayable.getStore();
    showStoreDescription(displayable, context);
    exploreButton.setText(displayable.getExploreButtonText());
    String storeTheme = store.getAppearance()
        .getTheme();
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(store.getAvatar(), storeIcon);

    storeName.setText(store.getName());
    compositeSubscription.add(RxView.clicks(exploreButton)
        .subscribe(click -> {
          getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
              .newStoreFragment(store.getName(), storeTheme), true);
          storeAnalytics.sendStoreTabInteractEvent("View Store", false);
          storeAnalytics.sendStoreOpenEvent("View Own Store", store.getName(), false);
        }));

    showStats(displayable);
    if (displayable.isStatsClickable()) {
      compositeSubscription.add(RxView.clicks(firstStat)
          .subscribe(click -> {
            storeAnalytics.sendFollowersStoresInteractEvent();
            getFragmentNavigator().navigateTo(
                TimeLineFollowersFragment.newInstanceUsingUser(storeTheme,
                    AptoideUtils.StringU.getFormattedString(
                        R.string.social_timeline_followers_fragment_title,
                        getContext().getResources(), displayable.getFirstStatsNumber()),
                    displayable.getStoreContext()), true);
          }));

      compositeSubscription.add(RxView.clicks(secondStat)
          .subscribe(click -> {
            storeAnalytics.sendFollowingStoresInteractEvent();
            getFragmentNavigator().navigateTo(
                TimeLineFollowingFragment.newInstanceUsingUser(storeTheme,
                    AptoideUtils.StringU.getFormattedString(
                        R.string.social_timeline_following_fragment_title,
                        getContext().getResources(), displayable.getSecondStatsNumber()),
                    displayable.getStoreContext()), true);
          }));
    }
  }

  private void showStoreDescription(StoreDisplayable displayable, FragmentActivity context) {
    String message = displayable.getSuggestionMessage(context);
    if (message.isEmpty()) {
      this.suggestionMessage.setVisibility(View.GONE);
    } else {
      this.suggestionMessage.setText(message);
      this.suggestionMessage.setVisibility(View.VISIBLE);
    }
  }

  private void showStats(StoreDisplayable displayable) {
    SpannableFactory spannableFactory = new SpannableFactory();
    ParcelableSpan[] textStyle = {
        new StyleSpan(android.graphics.Typeface.BOLD), new ForegroundColorSpan(getTextColor())
    };
    String firstStatsText = String.format(getContext().getString(displayable.getFirstStatsLabel()),
        String.valueOf(displayable.getFirstStatsNumber()));
    firstStat.setText(spannableFactory.createMultiSpan(firstStatsText, textStyle,
        String.valueOf(displayable.getFirstStatsNumber())));

    String secondStatsText =
        String.format(getContext().getString(displayable.getSecondStatsLabel()),
            String.valueOf(displayable.getSecondStatsNumber()));
    secondStat.setText(spannableFactory.createMultiSpan(secondStatsText, textStyle,
        String.valueOf(displayable.getSecondStatsNumber())));
  }

  private @ColorInt int getTextColor() {
    Context context = getContext();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(R.color.default_color, context.getTheme());
    } else {
      return context.getResources()
          .getColor(R.color.default_color);
    }
  }
}
