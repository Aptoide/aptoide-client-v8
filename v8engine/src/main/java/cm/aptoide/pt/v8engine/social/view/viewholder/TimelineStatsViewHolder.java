package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.graphics.Color;
import android.text.ParcelableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.CardTouchEvent;
import cm.aptoide.pt.v8engine.social.data.TimelineStatsPost;
import cm.aptoide.pt.v8engine.social.data.TimelineStatsTouchEvent;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineStatsViewHolder extends PostViewHolder<TimelineStatsPost> {
  private final SpannableFactory spannableFactory;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private Button followers;
  private Button following;
  private Button followFriends;
  private View rightSeparator;

  public TimelineStatsViewHolder(View view, SpannableFactory spannableFactory,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view);
    this.spannableFactory = spannableFactory;
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.followers = (Button) itemView.findViewById(R.id.followers);
    this.following = (Button) itemView.findViewById(R.id.following);
    this.followFriends = (Button) itemView.findViewById(R.id.follow_friends_button);
    this.rightSeparator = itemView.findViewById(R.id.rightSeparator);
  }

  @Override public void setPost(TimelineStatsPost card, int position) {
    followers.setText(spannableFactory.createSpan(itemView.getContext()
            .getString(R.string.timeline_button_followers, card.getFollowers()),
        new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(card.getFollowers()), String.valueOf(card.getFollowers())));
    following.setText(spannableFactory.createSpan(itemView.getContext()
            .getString(R.string.timeline_button_followed, card.getFollowing()),
        new ParcelableSpan[] { new ForegroundColorSpan(Color.BLACK) },
        String.valueOf(card.getFollowing()), String.valueOf(card.getFollowing())));

    followers.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new TimelineStatsTouchEvent(card, TimelineStatsTouchEvent.ButtonClicked.FOLLOWERS,
            CardTouchEvent.Type.TIMELINE_STATS)));
    following.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new TimelineStatsTouchEvent(card, TimelineStatsTouchEvent.ButtonClicked.FOLLOWING,
            CardTouchEvent.Type.TIMELINE_STATS)));
    followFriends.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new TimelineStatsTouchEvent(card, TimelineStatsTouchEvent.ButtonClicked.FOLLOWFRIENDS,
            CardTouchEvent.Type.TIMELINE_STATS)));
  }
}
