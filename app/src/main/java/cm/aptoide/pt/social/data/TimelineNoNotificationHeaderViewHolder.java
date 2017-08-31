package cm.aptoide.pt.social.data;

import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.view.viewholder.PostViewHolder;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 31/08/2017.
 */

class TimelineNoNotificationHeaderViewHolder extends PostViewHolder<TimelineNoNotificationHeader> {

  private final View notificationCenterButton;
  private final View addFriendsButton;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;

  public TimelineNoNotificationHeaderViewHolder(View itemView,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(itemView, cardTouchEventPublishSubject);
    notificationCenterButton = itemView.findViewById(R.id.notification_button);
    addFriendsButton = itemView.findViewById(R.id.add_friends);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
  }

  @Override public void setPost(TimelineNoNotificationHeader card, int position) {
    notificationCenterButton.setOnClickListener(view -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.NOTIFICATION_CENTER)));
    addFriendsButton.setOnClickListener(view -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.ADD_FRIEND)));
  }
}
