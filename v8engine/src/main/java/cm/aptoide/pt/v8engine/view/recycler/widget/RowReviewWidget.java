package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.reviews.RowReviewDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;

public class RowReviewWidget extends Widget<RowReviewDisplayable> {

  public ImageView appIcon;
  public TextView rating;
  public TextView appName;
  private ImageView avatar;
  private TextView reviewer;
  private TextView reviewBody;

  public RowReviewWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
    rating = (TextView) itemView.findViewById(R.id.rating);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    avatar = (ImageView) itemView.findViewById(R.id.avatar);
    reviewer = (TextView) itemView.findViewById(R.id.reviewer);
    reviewBody = (TextView) itemView.findViewById(R.id.description);
  }

  @Override public void bindView(RowReviewDisplayable displayable) {
    final FragmentActivity context = getContext();

    FullReview review = displayable.getPojo();
    GetAppMeta.App app = review.getData()
        .getApp();

    if (app != null) {
      appName.setText(app.getName());
      ImageLoader.with(context)
          .load(app.getIcon(), appIcon);
    } else {
      appName.setVisibility(View.INVISIBLE);
      appIcon.setVisibility(View.INVISIBLE);
    }

    reviewBody.setText(review.getBody());
    reviewer.setText(AptoideUtils.StringU.getFormattedString(R.string.reviewed_by, review.getUser()
        .getName()));

    rating.setText(String.format(Locale.getDefault(), "%d", (long) review.getStats()
        .getRating()));
    ImageLoader.with(context)
        .loadWithCircleTransformAndPlaceHolderAvatarSize(review.getUser()
            .getAvatar(), avatar, R.drawable.layer_1);

    final FragmentNavigator navigator = getFragmentNavigator();
    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(aVoid -> {
          navigator.navigateTo(V8Engine.getFragmentProvider()
              .newRateAndReviewsFragment(app.getId(), app.getName(), app.getStore()
                  .getName(), app.getPackageName(), review.getId()));
        }));
  }
}
