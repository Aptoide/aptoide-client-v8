package cm.aptoide.pt.v8engine.notification.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import rx.subjects.PublishSubject;

/**
 * Created by pedroribeiro on 16/05/17.
 */

class InboxViewHolder extends RecyclerView.ViewHolder {

  private final TextView message;
  private PublishSubject<AptoideNotification> notificationSubject;

  protected InboxViewHolder(View itemView,
      PublishSubject<AptoideNotification> notificationSubject) {
    super(itemView);
    this.notificationSubject = notificationSubject;
    message = (TextView) itemView.findViewById(R.id.fragment_inbox_list_message);
  }

  public void setNotification(AptoideNotification notification) {
    message.setText(notification.getTitle());
    itemView.setOnClickListener(v -> {
      notificationSubject.onNext(notification);
    });
  }
}
