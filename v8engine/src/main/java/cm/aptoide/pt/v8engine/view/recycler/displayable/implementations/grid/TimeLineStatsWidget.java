package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsWidget extends Widget<TimeLineStatsDisplayable> {

  private TextView followers;
  private TextView following;
  private TextView followFriends;
  private View addFriendsLayout;

  public TimeLineStatsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    followers = (TextView) itemView.findViewById(R.id.followers);
    following = (TextView) itemView.findViewById(R.id.following);
    followFriends = (TextView) itemView.findViewById(R.id.follow_friends_button);
    addFriendsLayout = itemView.findViewById(R.id.add_friends_layout);
  }

  @Override public void bindView(TimeLineStatsDisplayable displayable) {
    followers.setText(displayable.getFollowersText(getContext()));
    following.setText(displayable.getFollowingText(getContext()));
    compositeSubscription.add(RxView.clicks(followers)
        .subscribe(click -> displayable.followersClick(getNavigationManager()), err -> {
          CrashReport.getInstance().log(err);
        }));
    compositeSubscription.add(RxView.clicks(following)
        .subscribe(click -> displayable.followingClick(getNavigationManager()), err -> {
          CrashReport.getInstance().log(err);
        }));
    compositeSubscription.add(RxView.clicks(followFriends)
        .subscribe(click -> displayable.followFriendsClick(getNavigationManager()), err -> {
          CrashReport.getInstance().log(err);
        }));
    if (!displayable.isShouldShowAddFriends()) {
      addFriendsLayout.setVisibility(View.GONE);
    }
  }
}
