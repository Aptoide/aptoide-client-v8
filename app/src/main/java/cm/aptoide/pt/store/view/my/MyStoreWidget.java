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
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
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

public class MyStoreWidget extends MetaStoresBaseWidget<MyStoreDisplayable> {

  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;
  private TextView suggestionMessage;
  private StoreAnalytics storeAnalytics;
  private TextView followers;
  private TextView following;

  public MyStoreWidget(View itemView) {
    super(itemView);
    NavigationTracker navigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getNavigationTracker();
    AnalyticsManager analyticsManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAnalyticsManager();
    storeAnalytics = new StoreAnalytics(analyticsManager, navigationTracker);
  }

  @Override protected void assignViews(View itemView) {
    storeIcon = (ImageView) itemView.findViewById(R.id.store_icon);
    storeName = (TextView) itemView.findViewById(R.id.store_name);
    suggestionMessage = (TextView) itemView.findViewById(R.id.create_store_text);
    followers = (TextView) itemView.findViewById(R.id.followers);
    following = (TextView) itemView.findViewById(R.id.following);
    exploreButton = (Button) itemView.findViewById(R.id.explore_button);
  }

  @Override public void bindView(MyStoreDisplayable displayable) {

    final FragmentActivity context = getContext();
    Store store = displayable.getMeta()
        .getData()
        .getStore();
    suggestionMessage.setText(displayable.getSuggestionMessage(context));
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

    SpannableFactory spannableFactory = new SpannableFactory();
    String followersText = String.format(getContext().getString(R.string.storetab_short_followers),
        String.valueOf(displayable.getFollowers()));

    ParcelableSpan[] textStyle = {
        new StyleSpan(android.graphics.Typeface.BOLD), new ForegroundColorSpan(getTextColor())
    };
    followers.setText(spannableFactory.createMultiSpan(followersText, textStyle,
        String.valueOf(displayable.getFollowers())));

    String followingText = String.format(getContext().getString(R.string.storetab_short_followings),
        String.valueOf(displayable.getFollowings()));
    following.setText(spannableFactory.createMultiSpan(followingText, textStyle,
        String.valueOf(displayable.getFollowings())));

    compositeSubscription.add(RxView.clicks(followers)
        .subscribe(click -> getFragmentNavigator().navigateTo(
            TimeLineFollowersFragment.newInstanceUsingUser(storeTheme,
                AptoideUtils.StringU.getFormattedString(
                    R.string.social_timeline_followers_fragment_title, getContext().getResources(),
                    displayable.getFollowers()), displayable.getStoreContext()), true)));

    compositeSubscription.add(RxView.clicks(following)
        .subscribe(click -> getFragmentNavigator().navigateTo(
            TimeLineFollowingFragment.newInstanceUsingUser(storeTheme,
                AptoideUtils.StringU.getFormattedString(
                    R.string.social_timeline_following_fragment_title, getContext().getResources(),
                    displayable.getFollowings()), displayable.getStoreContext()), true)));
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
