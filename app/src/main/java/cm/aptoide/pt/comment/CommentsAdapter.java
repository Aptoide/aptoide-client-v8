package cm.aptoide.pt.comment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comment.data.Comment;
import cm.aptoide.pt.comment.view.CommentViewHolder;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;

class CommentsAdapter extends RecyclerView.Adapter<CommentViewHolder> {
  private final AptoideUtils.DateTimeU dateUtils;
  private List<Comment> comments;

  CommentsAdapter(AptoideUtils.DateTimeU dateUtils, List<Comment> comments) {
    this.dateUtils = dateUtils;
    this.comments = comments;
  }

  @Override public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new CommentViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.comment_item, parent, false), dateUtils);
  }

  @Override public void onBindViewHolder(CommentViewHolder viewHolder, int position) {
    viewHolder.setComment(comments.get(position));
  }

  @Override public int getItemCount() {
    return comments.size();
  }

  void addComments(List<Comment> comments) {
    this.comments = comments;
    notifyDataSetChanged();
  }
}
