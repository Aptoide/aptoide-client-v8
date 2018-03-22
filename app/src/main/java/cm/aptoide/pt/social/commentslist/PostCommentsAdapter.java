package cm.aptoide.pt.social.commentslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentViewHolder> {

  private static final int PARENT = R.layout.parent_comment_item;
  private static final int CHILD = R.layout.child_comment_item;
  private static final int LOADING = R.layout.progress_item;
  private final PublishSubject<Long> replyEventPublishSubject;
  private final ProgressComment progressComment;
  private List<Comment> comments;

  PostCommentsAdapter(List<Comment> comments, ProgressComment progressComment,
      PublishSubject<Long> replyEventPublishSubject) {
    this.comments = comments;
    this.progressComment = progressComment;
    this.replyEventPublishSubject = replyEventPublishSubject;
  }

  @Override public PostCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case PARENT:
        return new ParentCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(PARENT, parent, false), replyEventPublishSubject);
      case CHILD:
        return new ChildCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(CHILD, parent, false));
      case LOADING:
        return new LoadingCommentViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(LOADING, parent, false));
      default:
        throw new IllegalStateException("Invalid comment view type");
    }
  }

  @Override public void onBindViewHolder(PostCommentViewHolder holder, int position) {
    holder.setComment(comments.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    Comment comment = comments.get(position);

    if (comment instanceof ProgressComment) {
      return LOADING;
    }

    if (comment.getParent() == null) {
      return PARENT;
    } else {
      return CHILD;
    }
  }

  @Override public int getItemCount() {
    return comments.size();
  }

  public void updateComments(List<Comment> comments) {
    this.comments = comments;
    notifyDataSetChanged();
  }

  public void addComments(List<Comment> newComments) {
    int commentsSize = this.comments.size();
    this.comments.addAll(newComments);
    notifyItemRangeInserted(commentsSize, newComments.size());
  }

  void addLoadMoreProgress() {
    if (!comments.contains(progressComment)) {
      this.comments.add(progressComment);
      notifyItemInserted(comments.size() - 1);
    }
  }

  void removeLoadMoreProgress() {
    this.comments.remove(progressComment);
    notifyDataSetChanged();
  }

  void addNewComment(Comment newComment) {
    int position = 0;
    Comment.Parent parent = newComment.getParent();
    if (parent != null) {
      for (Comment comment : comments) {
        if (parent.getId() == comment.getId()) {
          position = comments.indexOf(comment) + 1;
          break;
        }
      }
    }
    this.comments.add(position, newComment);
    notifyItemInserted(position);
  }
}
