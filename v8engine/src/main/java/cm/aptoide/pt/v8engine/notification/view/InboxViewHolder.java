package cm.aptoide.pt.v8engine.notification.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;

/**
 * Created by pedroribeiro on 16/05/17.
 */

class InboxViewHolder extends RecyclerView.ViewHolder {

  private final TextView message;

  protected InboxViewHolder(View itemView) {
    super(itemView);
    message = (TextView) itemView.findViewById(R.id.fragment_inbox_list_message);
  }

  public void setNotification(AptoideNotification notification) {
    message.setText(notification.getTitle());
  }
}
