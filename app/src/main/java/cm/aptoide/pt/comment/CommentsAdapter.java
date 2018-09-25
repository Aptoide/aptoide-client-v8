package cm.aptoide.pt.comment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.data.CommentLoading;
import cm.aptoide.pt.comment.view.AbstractCommentViewHolder;
import cm.aptoide.pt.comment.view.CommentViewHolder;
import cm.aptoide.pt.comment.view.LoadingCommentViewHolder;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;

class CommentsAdapter extends RecyclerView.Adapter<AbstractCommentViewHolder> {
  private static final int LOADING = R.layout.progress_item;
  private static final int COMMENT = R.layout.comment_item;
  private final AptoideUtils.DateTimeU dateUtils;
  private final Comment progressComment;
  private List<Comment> comments;

  CommentsAdapter(List<Comment> comments, AptoideUtils.DateTimeU dateUtils) {
    this.dateUtils = dateUtils;
    this.comments = comments;
    progressComment = new CommentLoading();
  }

  @Override public AbstractCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case COMMENT:
        return new CommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(COMMENT, parent, false), dateUtils);
      case LOADING:
        return new LoadingCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(LOADING, parent, false));
      default:
        throw new IllegalStateException("Invalid comment view type");
    }
  }

  @Override public void onBindViewHolder(AbstractCommentViewHolder viewHolder, int position) {
    viewHolder.setComment(comments.get(position));
  }

  @Override public int getItemViewType(int position) {
    Comment comment = comments.get(position);
    if (comment instanceof CommentLoading) {
      return LOADING;
    } else {
      return COMMENT;
    }
  }

  @Override public int getItemCount() {
    return comments.size();
  }

  void setComments(List<Comment> comments) {
    this.comments = comments;
    notifyDataSetChanged();
  }

  public void addComments(List<Comment> comments) {
    this.comments.addAll(comments);
    notifyDataSetChanged();
  }

  public void addLoadMore() {
    if (getLoadingPosition() < 0) {
      comments.add(progressComment);
      notifyItemInserted(comments.size() - 1);
    }
  }

  public void removeLoadMore() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      comments.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  private int getLoadingPosition() {
    for (int i = comments.size() - 1; i >= 0; i--) {
      Comment comment = comments.get(i);
      if (comment instanceof CommentLoading) {
        return i;
      }
    }
    return -1;
  }
}
