package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.Post;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 08/08/2017.
 */

public abstract class SocialPostViewHolder<T extends Post> extends PostViewHolder<T> {

  private final TextView numberComments;
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;

  public SocialPostViewHolder(View itemView,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(itemView, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
  }

  @Override public void setPost(T card, int position) {
    this.numberComments.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(card, position, CardTouchEvent.Type.COMMENT_NUMBER)));
  }

  @Override protected void handleCommentsInformation(Post post, int position) {
    super.handleCommentsInformation(post, position);
    if (post.getCommentsNumber() > 0) {
      numberComments.setText(itemView.getContext()
          .getResources()
          .getQuantityString(R.plurals.timeline_short_comment, (int) post.getCommentsNumber(),
              (int) post.getCommentsNumber()));
    } else {
      numberComments.setVisibility(View.INVISIBLE);
    }
  }
}
