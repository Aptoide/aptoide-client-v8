package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 28/09/2017.
 */

public interface PostCommentsView extends View {

  Observable<Void> reachesBottom();

  Observable<Void> refreshes();

  void showLoadMoreProgressIndicator();

  void hideLoadMoreProgressIndicator();

  void showComments(List<Comment> comments);

  void hideRefresh();
}
