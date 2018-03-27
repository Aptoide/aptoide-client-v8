package cm.aptoide.pt.home.apps;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/15/18.
 */

class UpdatesHeaderViewHolder extends AppsViewHolder {

  private TextView title;
  private TextView updateAllButton;
  private PublishSubject<Void> updateAllApps;

  public UpdatesHeaderViewHolder(View itemView, PublishSubject<Void> updateAllApps) {
    super(itemView);
    title = (TextView) itemView.findViewById(R.id.apps_updates_header_title);
    updateAllButton = (TextView) itemView.findViewById(R.id.apps_updates_update_all_button);
    this.updateAllApps = updateAllApps;
  }

  @Override public void setApp(App app) {
    title.setText(R.string.apps_title_updates_header);
    updateAllButton.setOnClickListener(click -> updateAllApps.onNext(null));
  }
}
