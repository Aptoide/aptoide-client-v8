package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.model.v7.Review;

/**
 * Created by franciscocalado on 10/05/18.
 */

public class TopReviewsAdapter extends RecyclerView.Adapter<MiniTopReviewViewHolder> {

  private final Review[] reviews;

  public TopReviewsAdapter() {
    this(null);
  }

  public TopReviewsAdapter(Review[] reviews) {
    this.reviews = reviews;
  }

  @Override public MiniTopReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return new MiniTopReviewViewHolder(
        inflater.inflate(MiniTopReviewViewHolder.LAYOUT_ID, parent, false));
  }

  @Override public void onBindViewHolder(MiniTopReviewViewHolder holder, int position) {
    holder.setup(reviews[position]);
  }

  @Override public int getItemCount() {
    return reviews == null ? 0 : reviews.length;
  }

  @Override public void onViewRecycled(MiniTopReviewViewHolder holder) {
    holder.cancelImageLoad();
    super.onViewRecycled(holder);
  }
}
