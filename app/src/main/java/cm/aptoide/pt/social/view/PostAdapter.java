package cm.aptoide.pt.social.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.CardViewHolderFactory;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.view.viewholder.PostViewHolder;
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
    Post post = posts.get(position);
    if (post instanceof TimelineUser) {
      TimelineUser user = (TimelineUser) post;
      if (user.hasUserStats()) {
        user.setCardType(CardType.TIMELINE_STATS);
      } else if (!user.isLoggedIn()) {
        user.setCardType(CardType.LOGIN);
      } else if (user.hasNotification()) {
        user.setCardType(CardType.NOTIFICATIONS);
      } else {
        user.setCardType(CardType.NO_NOTIFICATIONS);
      }
    }
    return post.getType()
        .ordinal();
  }

  @Override public int getItemCount() {
    return this.posts.size();
  }

  public void updatePosts(List<Post> cards) {
    if (hasUser()) {
      if (posts.size() > 1) {
        posts.subList(1, posts.size() - 1)
            .clear();
      }
      posts.addAll(cards);
    } else {
      this.posts = cards;
    }
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

  public void clearPosts() {
    posts.clear();
    notifyDataSetChanged();
  }

  public void removePost(int postPosition) {
    posts.remove(postPosition);
    notifyItemRemoved(postPosition);
  }

  public Post getPost(int position) {
    return posts.get(position);
  }

  public void showUser(Post post) {
    if (hasUser()) {
      posts.set(0, post);
      notifyItemChanged(0);
    } else {
      posts.add(0, post);
      notifyItemInserted(0);
    }
  }

  private boolean hasUser() {
    return !posts.isEmpty() && (posts.get(0) instanceof TimelineUser || posts.get(
        0) instanceof ProgressCard);
  }
}
