package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.presenter.View;
import java.util.List;

public interface CommentsView extends View {

  void showComments(List<Comment> comments);

  void showLoading();

  void hideLoading();

  void showGeneralError();
}
