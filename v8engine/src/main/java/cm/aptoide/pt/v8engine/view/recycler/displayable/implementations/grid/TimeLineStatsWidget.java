package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by trinkes on 15/12/2016.
 */

public class TimeLineStatsWidget extends Widget<TimeLineStatsDisplayable> {

  private TextView followers;
  private TextView following;

  public TimeLineStatsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    followers = (TextView) itemView.findViewById(R.id.followers);
    following = (TextView) itemView.findViewById(R.id.following);
  }

  @Override public void bindView(TimeLineStatsDisplayable displayable) {
    followers.setText(displayable.getFollowersText(getContext()));
    following.setText(displayable.getFollowingText(getContext()));
  }
}
