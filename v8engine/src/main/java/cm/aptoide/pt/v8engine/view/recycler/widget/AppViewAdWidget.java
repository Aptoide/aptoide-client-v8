package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.GridAdDisplayable;

/**
 * Created by neuro on 01-08-2017.
 */

public class AppViewAdWidget extends GridAdWidget {

  private TextView downloadsTextView;
  private RatingBar ratingBar;

  public AppViewAdWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);

    downloadsTextView = (TextView) itemView.findViewById(R.id.downloads);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
  }

  @Override public void bindView(GridAdDisplayable displayable) {
    super.bindView(displayable);

    int downloads = displayable.getPojo()
        .getDownloads();

    downloadsTextView.setText(getContext().getString(R.string.all_short_downloads_count_text,
        AptoideUtils.StringU.withSuffix(downloads)));
    ratingBar.setRating(displayable.getPojo()
        .getStars());
  }
}
