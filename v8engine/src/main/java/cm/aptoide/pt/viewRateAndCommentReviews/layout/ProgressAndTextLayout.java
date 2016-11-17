package cm.aptoide.pt.viewRateAndCommentReviews.layout;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.utils.AptoideUtils;

class ProgressAndTextLayout {

  private ProgressBar progressBar;
  private TextView text;

  ProgressAndTextLayout(int progressId, int textId, View view) {
    progressBar = (ProgressBar) view.findViewById(progressId);
    text = (TextView) view.findViewById(textId);
  }

  public void setup(int total, int count) {
    progressBar.setMax(total);
    progressBar.setProgress(count);
    text.setText(AptoideUtils.StringU.withSuffix(count));
  }
}
