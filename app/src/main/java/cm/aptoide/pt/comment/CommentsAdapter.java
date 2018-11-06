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
import rx.subjects.PublishSubject;

public class CommentsAdapter extends RecyclerView.Adapter<AbstractCommentViewHolder> {
  private static final int COMMENT = 1;
  private static final int LOADING = 2;
  private static final int ADD_COMMENT = 3;
  private final AptoideUtils.DateTimeU dateUtils;
  private final Comment progressComment;
  private final PublishSubject<Comment> commentClickEvent;
  private final PublishSubject<Comment> postComment;
  private final int commentViewId;
  private List<Comment> comments;

  public CommentsAdapter(List<Comment> comments, AptoideUtils.DateTimeU dateUtils,
      PublishSubject<Comment> commentClickEvent, int commentItemId,
      PublishSubject<Comment> postComment) {
    this.dateUtils = dateUtils;
    this.comments = comments;
    this.progressComment = new CommentLoading();
    this.commentClickEvent = commentClickEvent;
    this.commentViewId = commentItemId;
    this.postComment = postComment;
  }

  @Override public AbstractCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case COMMENT:
        return new CommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(commentViewId, parent, false), dateUtils, commentClickEvent);
      case LOADING:
        return new LoadingCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.progress_item, parent, false));
      case ADD_COMMENT:
        return new SubmitCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.add_comment_item, parent, false), postComment);
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
    } else if (comment instanceof SubmitComment) {
      return ADD_COMMENT;
    } else {
      return COMMENT;
    }
  }

  @Override public int getItemCount() {
    return comments.size();
  }

  public void setComments(List<Comment> comments, SubmitComment submitComment) {
    this.comments = comments;
    this.comments.add(0, submitComment);
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
