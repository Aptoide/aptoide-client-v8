package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import java.util.List;
import rx.Single;

public interface CommentsDataSource {

  Single<List<Comment>> loadComments();
}
