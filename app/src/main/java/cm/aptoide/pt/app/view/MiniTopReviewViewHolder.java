package cm.aptoide.pt.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.AppReview;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.request.target.Target;

/**
 * Created by franciscocalado on 10/05/18.
 */

public class MiniTopReviewViewHolder extends RecyclerView.ViewHolder {

  public static final int LAYOUT_ID = R.layout.mini_top_comment;

  private ImageView userIconImageView;
  private RatingBar ratingBar;
  private TextView commentTitle;
  private TextView userName;
  private TextView addedDate;
  private TextView commentText;
  private Target<Drawable> imageLoadingTarget;

  public MiniTopReviewViewHolder(View itemView) {
    super(itemView);
    bindViews(itemView);
  }

  private void bindViews(View view) {
    userIconImageView = (ImageView) view.findViewById(R.id.user_icon);
    ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
    commentTitle = (TextView) view.findViewById(R.id.comment_title);
    userName = (TextView) view.findViewById(R.id.user_name);
    addedDate = (TextView) view.findViewById(R.id.added_date);
    commentText = (TextView) view.findViewById(R.id.comment);
  }

  public void setup(AppReview review) {
    String imageUrl = review.getReviewUser()
        .getAvatar();
    Context context = itemView.getContext();
    imageLoadingTarget = ImageLoader.with(context)
        .loadWithCircleTransformAndPlaceHolderAvatarSize(imageUrl, userIconImageView,
            R.drawable.layer_1);
    userName.setText(review.getReviewUser()
        .getName());
    ratingBar.setRating(review.getReviewStats()
        .getRating());
    commentTitle.setText(review.getTitle());
    commentText.setText(review.getBody());
    addedDate.setText(AptoideUtils.DateTimeU.getInstance(context)
        .getTimeDiffString(review.getAdded()
            .getTime(), context, context.getResources()));
  }

  public void cancelImageLoad() {
    if (imageLoadingTarget != null) {
      ImageLoader.cancel(itemView.getContext()
          .getApplicationContext(), imageLoadingTarget);
    }
  }
}
