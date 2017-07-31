package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;

class RecommendationPostShareDialog extends BaseShareDialog<Recommendation> {

  @LayoutRes static final int LAYOUT_ID = R.layout.timeline_recommendation_preview;

  RecommendationPostShareDialog(RxAlertDialog dialog) {
    super(dialog);
  }

  @Override void setupView(View view, Recommendation post) {
    final Context context = view.getContext();
    ImageView appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    TextView appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    TextView getApp = (TextView) view.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

    ImageLoader.with(context)
        .load(post.getAppIcon(), appIcon);
    appName.setText(post.getAppName());
    ratingBar.setRating(post.getAppAverageRating());
    SpannableFactory spannableFactory = new SpannableFactory();

    getApp.setText(spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
  }

  public static class Builder extends BaseShareDialog.Builder {

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      super(context, sharePostViewSetup, account, LAYOUT_ID);
    }

    public RecommendationPostShareDialog build() {
      return new RecommendationPostShareDialog(buildRxAlertDialog());
    }
  }
}
