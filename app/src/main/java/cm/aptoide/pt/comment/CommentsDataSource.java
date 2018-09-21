package cm.aptoide.pt.comment;

import java.util.List;
import rx.Single;

public interface CommentsDataSource {

  Single<List<String>> loadComments();
}
