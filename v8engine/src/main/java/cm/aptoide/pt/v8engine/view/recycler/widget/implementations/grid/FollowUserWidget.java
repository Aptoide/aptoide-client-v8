package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowUserDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserWidget extends Widget<FollowUserDisplayable> {

  private TextView userName;

  public FollowUserWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userName = (TextView) itemView.findViewById(R.id.user_name);
  }

  @Override public void bindView(FollowUserDisplayable displayable) {
    userName.setText(displayable.getUserName());
  }
}
