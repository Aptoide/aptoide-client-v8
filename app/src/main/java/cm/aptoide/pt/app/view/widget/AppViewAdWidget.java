package cm.aptoide.pt.app.view.widget;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.GridAdDisplayable;
import cm.aptoide.pt.view.recycler.widget.GridAdWidget;

/**
 * Created by neuro on 01-08-2017.
 */

public class AppViewAdWidget extends GridAdWidget {

  private TextView ratingBar;

  public AppViewAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);

    ratingBar = (TextView) itemView.findViewById(R.id.rating_label);
  }

  @Override public void bindView(GridAdDisplayable displayable) {
    super.bindView(displayable);

    ratingBar.setText(displayable.getPojo()
        .getStars() + "");
  }
}
