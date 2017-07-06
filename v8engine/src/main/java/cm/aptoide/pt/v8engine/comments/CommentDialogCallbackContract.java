package cm.aptoide.pt.v8engine.comments;

import cm.aptoide.pt.model.v7.base.BaseV7Response;

/**
 * Created by jdandrade on 03/02/2017.
 */
public interface CommentDialogCallbackContract {
  void okSelected(BaseV7Response inputText, long longAsId, Long previousCommentId,
      String idAsString);
}
