package cm.aptoide.pt.v8engine.view.reviews;

import android.view.View;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.ProgressAndTextLayout;

public class RatingBarsLayout {

  private ProgressAndTextLayout[] progressAndTextLayouts;

  public RatingBarsLayout(View view) {
    progressAndTextLayouts = new ProgressAndTextLayout[5];
    progressAndTextLayouts[0] =
        new ProgressAndTextLayout(R.id.one_rate_star_progress, R.id.one_rate_star_count, view);
    progressAndTextLayouts[1] =
        new ProgressAndTextLayout(R.id.two_rate_star_progress, R.id.two_rate_star_count, view);
    progressAndTextLayouts[2] =
        new ProgressAndTextLayout(R.id.three_rate_star_progress, R.id.three_rate_star_count, view);
    progressAndTextLayouts[3] =
        new ProgressAndTextLayout(R.id.four_rate_star_progress, R.id.four_rate_star_count, view);
    progressAndTextLayouts[4] =
        new ProgressAndTextLayout(R.id.five_rate_star_progress, R.id.five_rate_star_count, view);
  }

  public void setup(GetAppMeta.App data) {
    GetAppMeta.Stats.Rating rating = data.getStats().getRating();
    final int total = rating.getTotal();
    for (final GetAppMeta.Stats.Rating.Vote vote : rating.getVotes()) {
      progressAndTextLayouts[vote.getValue() - 1].setup(total, vote.getCount());
    }
  }
}
