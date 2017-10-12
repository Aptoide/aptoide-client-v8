package cm.aptoide.pt.view.store.my;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.view.store.MetaStoresBaseWidget;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreWidget extends MetaStoresBaseWidget<MyStoreDisplayable> {

  private View storeLayout;
  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;
  private TextView suggestionMessage;
  private StoreAnalytics storeAnalytics;
  private TextView followers;
  private TextView following;

  public MyStoreWidget(View itemView) {
    super(itemView);
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext()), Analytics.getInstance());
  }

  @Override protected void assignViews(View itemView) {
    storeLayout = itemView.findViewById(R.id.store_layout);
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
    @ColorInt int color = getColorOrDefault(StoreTheme.get(storeTheme), context);
    Drawable exploreButtonBackground = exploreButton.getBackground();
    exploreButtonBackground.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.my_store_background);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      storeLayout.setBackground(d);
      exploreButton.setBackground(exploreButtonBackground);
    } else {
      Drawable d = context.getResources()
          .getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      storeLayout.setBackgroundDrawable(d);
      exploreButton.setBackgroundDrawable(exploreButtonBackground);
    }
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(store.getAvatar(), storeIcon);

    storeName.setText(store.getName());
    compositeSubscription.add(RxView.clicks(exploreButton)
        .subscribe(click -> {
          getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
              .newStoreFragment(store.getName(), storeTheme), true);
          storeAnalytics.sendStoreTabInteractEvent("View Store");
          storeAnalytics.sendStoreOpenEvent("View Own Store", store.getName());
        }));

    String followersText = String.format(getContext().getString(R.string.storetab_short_followers),
        String.valueOf(displayable.getFollowers()));
    followers.setText(new SpannableFactory().createColorSpan(followersText, color,
        String.valueOf(displayable.getFollowers())));

    String followingText = String.format(getContext().getString(R.string.storetab_short_followings),
        String.valueOf(displayable.getFollowings()));
    following.setText(new SpannableFactory().createColorSpan(followingText, color,
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

  private int getColorOrDefault(StoreTheme theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(theme.getPrimaryColor(), context.getTheme());
    } else {
      return context.getResources()
          .getColor(theme.getPrimaryColor());
    }
  }
}
