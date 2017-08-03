package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.RatedRecommendation;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;

class RatedRecommendationPostShareDialog extends BaseShareDialog<RatedRecommendation> {

  @LayoutRes static final int LAYOUT_ID = R.layout.timeline_recommendation_preview;

  RatedRecommendationPostShareDialog(RxAlertDialog dialog) {
    super(dialog);
  }

  void setupView(View view, RatedRecommendation post) {
    final Context context = view.getContext();
    ImageView appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    TextView appName =
        (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);

    TextView getApp = (TextView) view.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    ImageLoader.with(context)
        .load(post.getAppIcon(), appIcon);
    appName.setText(post.getAppName());

    SpannableFactory spannableFactory = new SpannableFactory();

    getApp.setText(spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
  }

  public static class Builder extends BaseShareDialog.Builder {

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account) {
      super(context, sharePostViewSetup, account, LAYOUT_ID);
    }

    public RatedRecommendationPostShareDialog build() {
      return new RatedRecommendationPostShareDialog(buildRxAlertDialog());
    }
  }
}
