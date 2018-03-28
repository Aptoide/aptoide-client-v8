package cm.aptoide.pt.app.view.widget;

import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.widget.GridAdWidget;

/**
 * Created by neuro on 01-08-2017.
 */

public class AppViewAdWidget extends GridAdWidget {

  public AppViewAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);

    itemView.findViewById(R.id.i_ad)
        .setVisibility(View.VISIBLE);
    itemView.findViewById(R.id.tv_ad)
        .setVisibility(View.GONE);
  }
}
