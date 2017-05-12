package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.timeline.displayable.PopularAppDisplayable;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by jdandrade on 27/04/2017.
 */

public class PopularAppWidget extends CardWidget<PopularAppDisplayable> {

  private final LayoutInflater inflater;
  private TextView title;
  private TextView timestamp;
  private LinearLayout usersContainer;
  private ImageView appIcon;
  private TextView appName;
  private CardView cardView;
  private RatingBar ratingBar;
  private Button getApp;

  public PopularAppWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    title = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_popular_app_card_header_title);
    timestamp = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_popular_app_card_timestamp);
    usersContainer = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_popular_app_users_container);
    appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_popular_app_icon);
    appName =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_popular_app_body_title);
    cardView = (CardView) itemView.findViewById(R.id.card);
    ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
    getApp =
        (Button) itemView.findViewById(R.id.displayable_social_timeline_popular_app_get_app_button);
  }

  @Override public void bindView(PopularAppDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    setCardViewMargin(displayable, cardView);

    title.setText(displayable.getCardTitleText(context));
    timestamp.setText(displayable.getTimeSinceLastUpdate(context));
    appName.setText(displayable.getAppName());
    ratingBar.setRating(displayable.getAppAverageRating());

    ImageLoader.with(context)
        .load(displayable.getAppIcon(), appIcon);

    buildFriendsIcons(displayable, context);

    RxView.clicks(getApp)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(PopularAppDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK,
              displayable.getStoreName(), Analytics.AppsTimeline.OPEN_APP_VIEW);
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  @Override String getCardTypeName() {
    return PopularAppDisplayable.CARD_TYPE_NAME;
  }

  private void buildFriendsIcons(PopularAppDisplayable displayable, FragmentActivity context) {
    usersContainer.removeAllViews();
    View friendView;
    ImageView friendAvatar;
    for (Comment.User friend : displayable.getFriends()) {
      friendView = inflater.inflate(R.layout.social_timeline_friend, usersContainer, false);
      friendAvatar = (ImageView) friendView.findViewById(R.id.social_timeline_friend_avatar);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(friend.getAvatar(), friendAvatar);
      usersContainer.addView(friendView);
    }
  }
}
