package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.TimelineLoginPost;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineLoginPostViewHolder extends CardViewHolder<TimelineLoginPost> {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private Button button;

  public TimelineLoginPostViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.button = (Button) itemView.findViewById(R.id.login_button);
  }

  @Override public void setCard(TimelineLoginPost card, int position) {
    this.button.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, CardTouchEvent.Type.LOGIN)));
  }
}
