package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsWidget extends Widget<TimeLineStatsDisplayable> {

  private Button followers;
  private Button following;
  private Button followFriends;
  private View rightSeparator;

  public TimeLineStatsWidget(View itemView) {
    super(itemView);
  }

  @UiThread @Override protected void assignViews(View itemView) {
    followers = (Button) itemView.findViewById(R.id.followers);
    following = (Button) itemView.findViewById(R.id.following);
    followFriends = (Button) itemView.findViewById(R.id.follow_friends_button);
    rightSeparator = itemView.findViewById(R.id.rightSeparator);
  }

  @UiThread @Override public void bindView(TimeLineStatsDisplayable displayable) {
    followers.setText(displayable.getFollowersText(getContext()));
    following.setText(displayable.getFollowingText(getContext()));

    Observable<Void> followersClick =
        RxView.clicks(followers).doOnNext(__ -> displayable.followersClick(getFragmentNavigator()));

    Observable<Void> followingClick =
        RxView.clicks(following).doOnNext(__ -> displayable.followingClick(getFragmentNavigator()));

    Observable<Void> followFriendsClick = RxView.clicks(followFriends)
        .doOnNext(__ -> displayable.followFriendsClick(getFragmentNavigator()));

    compositeSubscription.add(Observable.merge(followersClick, followingClick, followFriendsClick)
        .doOnError((throwable) -> CrashReport.getInstance().log(throwable))
        .subscribe());

    if (!displayable.isShouldShowAddFriends()) {
      rightSeparator.setVisibility(View.GONE);
      followFriends.setVisibility(View.GONE);
    }
  }
}
