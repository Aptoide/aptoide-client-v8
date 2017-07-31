package cm.aptoide.pt.v8engine.social.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.social.data.CardViewHolderFactory;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.view.viewholder.PostViewHolder;
import java.util.List;

/**
 * Created by jdandrade on 2/06/17.
 */

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

  private final CardViewHolderFactory cardViewHolderFactory;
  private List<Post> posts;
  private ProgressCard progressCard;

  public PostAdapter(List<Post> posts, CardViewHolderFactory cardViewHolderFactory,
      ProgressCard progressCard) {
    this.posts = posts;
    this.cardViewHolderFactory = cardViewHolderFactory;
    this.progressCard = progressCard;
  }

  @Override public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return cardViewHolderFactory.createViewHolder(viewType, parent);
  }

  @Override public void onBindViewHolder(PostViewHolder holder, int position) {
    holder.setPost(posts.get(position), position);
  }

  @Override public int getItemViewType(int position) {
    return posts.get(position)
        .getType()
        .ordinal();
  }

  @Override public int getItemCount() {
    return this.posts.size();
  }

  public void updatePosts(List<Post> cards) {
    this.posts = cards;
    notifyDataSetChanged();
  }

  public void addPosts(List<Post> cards) {
    this.posts.addAll(cards);
    notifyDataSetChanged();
  }

  public void addLoadMoreProgress() {
    if (!this.posts.contains(progressCard)) {
      this.posts.add(progressCard);
      notifyDataSetChanged();
    }
  }

  public void removeLoadMoreProgress() {
    this.posts.remove(progressCard);
    notifyDataSetChanged();
  }

  public void updatePost(int postPosition) {
    notifyItemChanged(postPosition);
  }

  public void swapPost(Post post, int postPosition) {
    posts.set(postPosition, post);
    notifyItemChanged(postPosition);
  }
}
