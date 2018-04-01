package cm.aptoide.pt.app.view.widget;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.GridAdDisplayable;
import cm.aptoide.pt.view.recycler.widget.GridAdWidget;
import java.text.DecimalFormat;

/**
 * Created by neuro on 01-08-2017.
 */

public class AppViewSuggestedAdWidget extends GridAdWidget {

  private TextView rating;

  public AppViewSuggestedAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);

    rating = (TextView) itemView.findViewById(R.id.rating_label);
  }

  @Override public void bindView(GridAdDisplayable displayable) {
    super.bindView(displayable);

    try {
      DecimalFormat oneDecimalFormatter = new DecimalFormat("#.#");
      rating.setText(oneDecimalFormatter.format(displayable.getPojo()
          .getStars()));
    } catch (Exception e) {
      rating.setText(R.string.appcardview_title_no_starts);
    }
  }
}
