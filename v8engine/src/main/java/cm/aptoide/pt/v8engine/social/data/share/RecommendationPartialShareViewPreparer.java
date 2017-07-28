package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.social.data.Recommendation;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;

class RecommendationPartialShareViewPreparer implements PartialShareViewPreparer {
  @Override public View prepareViewForPostType(Post post, Context context, LayoutInflater factory) {
    View view = factory.inflate(R.layout.timeline_recommendation_preview, null);
    ImageView appIcon =
        (ImageView) view.findViewById(R.id.displayable_social_timeline_recommendation_icon);
    TextView appName = (TextView) view.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
    TextView getApp = (TextView) view.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

    ImageLoader.with(context)
        .load(((Recommendation) post).getAppIcon(), appIcon);
    appName.setText(((Recommendation) post).getAppName());
    ratingBar.setRating(((Recommendation) post).getAppAverageRating());
    SpannableFactory spannableFactory = new SpannableFactory();

    getApp.setText(spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), ""));
    return view;
  }
}
