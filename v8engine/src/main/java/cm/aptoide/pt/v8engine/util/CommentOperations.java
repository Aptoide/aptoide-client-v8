package cm.aptoide.pt.v8engine.util;

import android.support.v4.util.LongSparseArray;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.viewRateAndCommentReviews.CommentNode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class CommentOperations {
  /**
   * Uses depth-first search to transverse and flatten all the {@link CommentNode}
   *
   * @param comments a list of {@link CommentNode}
   *
   * @return list of all {@link CommentNode} and children, sorted by depth
   */
  public List<CommentNode> flattenByDepth(List<CommentNode> comments) {
    List<CommentNode> sortedByDepthComments = new LinkedList<>();
    Stack<CommentNode> commentStack = new Stack<>();
    commentStack.addAll(comments);
    while (!commentStack.isEmpty()) {
      CommentNode commentNode = commentStack.pop();
      sortedByDepthComments.add(commentNode);

      if (commentNode.hasChild()) {
        for (CommentNode node : commentNode.getChildComments()) {
          node.setLevel(commentNode.getLevel() + 1);
          commentStack.push(node);
        }
      }
    }

    return sortedByDepthComments;
  }

  /**
   * Transforms a list of comments in a list of {@link CommentNode}. Each {@link CommentNode}
   * contains it's own {@link Comment} and list of child {@link CommentNode}.
   *
   * @return a root {@link List} of type {@link CommentNode}
   */
  public List<CommentNode> transform(List<Comment> list) {
    LongSparseArray<CommentNode> commentMap = new LongSparseArray<>();

    for (Comment comment : list) {
      Comment.Parent commentParent = comment.getParent();
      if (commentParent != null) {
        //
        // has parent
        //
        CommentNode commentNode = commentMap.get(commentParent.getId());
        if (commentNode == null) {
          commentNode = new CommentNode();
        }
        commentNode.addChild(new CommentNode(comment));
        commentMap.append(commentParent.getId(), commentNode);
      } else {
        //
        // is a root node
        //
        CommentNode commentNode = commentMap.get(comment.getId());
        if (commentNode == null) {
          commentMap.append(comment.getId(), new CommentNode(comment));
        } else {
          commentNode.setComment(comment);
        }
      }
    }

    ArrayList<CommentNode> commentNodes = new ArrayList<>(commentMap.size());
    for (int i = 0; i < commentMap.size(); i++) {
      commentNodes.add(commentMap.valueAt(i));
    }
    return commentNodes;
  }
}
