package cm.aptoide.pt.social.commentslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.List;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentViewHolder> {

  private static final int PARENT = R.layout.parent_comment_item;
  private static final int CHILD = R.layout.child_comment_item;
  private List<Comment> comments;

  public PostCommentsAdapter(List<Comment> comments) {
    this.comments = comments;
  }

  @Override public PostCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case PARENT:
        return new ParentCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(PARENT, parent, false));
      case CHILD:
        return new ChildCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(CHILD, parent, false));
      default:
        throw new IllegalStateException("Invalid comment view type");
    }
  }

  @Override public void onBindViewHolder(PostCommentViewHolder holder, int position) {
    holder.setComment(comments.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    Comment comment = comments.get(position);
    if (comment.getParent() == null) {
      return PARENT;
    } else {
      return CHILD;
    }
  }

  @Override public int getItemCount() {
    return comments.size();
  }

  public void addComments(List<Comment> commentList) {
    this.comments.addAll(commentList);
    notifyDataSetChanged();
  }
}
