package cm.aptoide.pt.social.view.viewholder;

import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.R;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.EmptyStatePost;
import cm.aptoide.pt.social.data.Post;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 26/09/2017.
 */

public class EmptyStateViewHolder extends PostViewHolder {
  private final PublishSubject<CardTouchEvent> cardTouchEventPublishSubject;
  private final Button getStarted;

  public EmptyStateViewHolder(View view,
      PublishSubject<CardTouchEvent> cardTouchEventPublishSubject) {
    super(view, cardTouchEventPublishSubject);
    this.cardTouchEventPublishSubject = cardTouchEventPublishSubject;
    this.getStarted = (Button) view.findViewById(R.id.get_started_button);
  }

  @Override public void setPost(Post post, int position) {
    handleActionButtonVisibility((EmptyStatePost) post);
    getStarted.setOnClickListener(click -> cardTouchEventPublishSubject.onNext(
        new CardTouchEvent(post, position, CardTouchEvent.Type.POST)));
  }

  private void handleActionButtonVisibility(EmptyStatePost post) {
    if (post.getAction() == EmptyStatePost.ACTION) {
      this.getStarted.setVisibility(View.VISIBLE);
    } else {
      this.getStarted.setVisibility(View.GONE);
    }
  }
}
