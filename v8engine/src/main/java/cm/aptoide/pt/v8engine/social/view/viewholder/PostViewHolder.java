package cm.aptoide.pt.v8engine.social.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.v8engine.social.data.Post;

/**
 * Created by jdandrade on 08/06/2017.
 */

public abstract class PostViewHolder<T extends Post> extends RecyclerView.ViewHolder {
  public PostViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setPost(T card, int position);
}
