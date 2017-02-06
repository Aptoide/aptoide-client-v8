package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created by jdandrade on 03/02/2017.
 */
public interface CommentDialogCallbackContract {
  void okSelected(String text, long idAsLong, Long previousCommentId, String inputText);
}
