package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.User;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

  public CommentMapper() {
  }

  public List<Comment> map(List<cm.aptoide.pt.dataprovider.model.v7.Comment> networkComments) {
    List<Comment> comments = new ArrayList<>();

    for (cm.aptoide.pt.dataprovider.model.v7.Comment networkComment : networkComments) {
      comments.add(new Comment(networkComment.getId(), networkComment.getBody(), new User(
          networkComment.getUser()
              .getId(), networkComment.getUser()
          .getAvatar(), networkComment.getUser()
          .getName()), networkComment.getStats()
          .getComments(), networkComment.getAdded()));
    }

    return comments;
  }
}
