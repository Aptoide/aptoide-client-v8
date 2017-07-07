package cm.aptoide.pt.v8engine.notification.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import rx.subjects.PublishSubject;

/**
 * Created by pedroribeiro on 16/05/17.
 */

class InboxViewHolder extends RecyclerView.ViewHolder {

  private final TextView title;
  private PublishSubject<AptoideNotification> notificationSubject;
  private TextView body;
  private ImageView userAvatar;

  protected InboxViewHolder(View itemView,
      PublishSubject<AptoideNotification> notificationSubject) {
    super(itemView);
    this.notificationSubject = notificationSubject;
    title = (TextView) itemView.findViewById(R.id.fragment_inbox_list_title);
    body = (TextView) itemView.findViewById(R.id.fragment_inbox_list_body);
    userAvatar = (ImageView) itemView.findViewById(R.id.fragment_inbox_list_item_image);
  }

  public void setNotification(AptoideNotification notification) {
    title.setText(notification.getTitle());
    body.setText(notification.getBody());
    ImageLoader.with(itemView.getContext())
        .loadWithShadowCircleTransform(notification.getImg(), userAvatar);
    itemView.setOnClickListener(v -> {
      notificationSubject.onNext(notification);
    });
  }
}
