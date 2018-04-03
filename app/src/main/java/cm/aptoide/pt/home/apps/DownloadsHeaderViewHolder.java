package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;

/**
 * Created by filipegoncalves on 3/27/18.
 */

class DownloadsHeaderViewHolder extends AppsViewHolder {

  private TextView title;

  public DownloadsHeaderViewHolder(View itemView) {
    super(itemView);
    title = (TextView) itemView.findViewById(R.id.apps_downloads_header_title);
  }

  @Override public void setApp(App app) {
    title.setText(R.string.apps_title_downloads_header);
  }
}
