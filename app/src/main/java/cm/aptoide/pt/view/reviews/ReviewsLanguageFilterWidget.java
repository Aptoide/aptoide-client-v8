package cm.aptoide.pt.view.reviews;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Spinner;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.widget.Widget;

/**
 * Created by neuro on 04-09-2017.
 */
public class ReviewsLanguageFilterWidget extends Widget<ReviewsLanguageFilterDisplayable> {

  private Spinner spinner;

  public ReviewsLanguageFilterWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    spinner = (Spinner) itemView.findViewById(R.id.comments_filter_language_spinner);
  }

  @Override public void bindView(ReviewsLanguageFilterDisplayable displayable) {
    displayable.setup(spinner);
  }
}
