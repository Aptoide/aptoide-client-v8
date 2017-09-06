package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.view.TimelineUser;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 30/08/2017.
 */

public class Notifications extends PostViewHolder<TimelineUser> {

  private final ImageView notificationImage;
  private final TextView notificationBody;
  private final View goToNotificationsButton;
  private final View addFriendsButton;
  private final PublishSubject<CardTouchEvent> publishSubject;
  private final ImageLoader imageLoader;

  public Notifications(View itemView, PublishSubject<CardTouchEvent> publishSubject,
      ImageLoader imageLoader) {
    super(itemView, publishSubject);
    this.publishSubject = publishSubject;
    this.imageLoader = imageLoader;
    notificationImage = ((ImageView) itemView.findViewById(R.id.image));
    notificationBody = (TextView) itemView.findViewById(R.id.notification_text);
    goToNotificationsButton = itemView.findViewById(R.id.notification_button);
    addFriendsButton = itemView.findViewById(R.id.add_friends);
  }

  @Override public void setPost(TimelineUser card, int position) {
    imageLoader.loadUsingCircleTransform(card.getImage(), notificationImage);
    notificationBody.setText(card.getBodyMessage());
    goToNotificationsButton.setOnClickListener(view -> publishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.NOTIFICATION_CENTER)));
    addFriendsButton.setOnClickListener(
        view -> publishSubject.onNext(new CardTouchEvent(card, CardTouchEvent.Type.ADD_FRIEND)));
    notificationImage.setOnClickListener(
        view -> publishSubject.onNext(new CardTouchEvent(card, CardTouchEvent.Type.NOTIFICATION)));
    notificationBody.setOnClickListener(
        view -> publishSubject.onNext(new CardTouchEvent(card, CardTouchEvent.Type.NOTIFICATION)));
  }
}
