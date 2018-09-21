package cm.aptoide.pt.comment;

import java.util.Collections;
import java.util.List;
import rx.Single;

public class CommentsListManager {

  public CommentsListManager() {
  }

  public Single<List<String>> loadComments() {
    return Single.just(Collections.emptyList());
  }
}
