package cm.aptoide.pt.social.commentslist;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.ArrayList;
import java.util.List;

class CommentsSorter {
  List<Comment> sort(List<Comment> comments) {
    List<Comment> sortedComments = new ArrayList<>(comments.size());
    for (Comment comment : comments) {
      Comment.Parent parent = comment.getParent();
      if (parent != null) { // is child
        for (Comment commentInOrder : sortedComments) {
          if (commentInOrder.getId() == parent.getId()) {
            sortedComments.add(sortedComments.indexOf(commentInOrder) + 1, comment);
            break;
          }
        }
      } else { // is parent
        sortedComments.add(comment);
      }
    }
    return sortedComments;
  }
}