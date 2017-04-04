package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.timeline.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class FeatureWidget extends Widget<FeatureDisplayable> {

  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private TextView articleTitle;
  private ImageView thumbnail;
  private View url;
  //private Button getAppButton;
  private CardView cardView;

  public FeatureWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    title = (TextView) itemView.findViewById(R.id.card_title);
    subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    image = (ImageView) itemView.findViewById(R.id.card_image);
    articleTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    //getAppButton =
    //    (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
  }

  @Override public void bindView(FeatureDisplayable displayable) {
    final FragmentActivity context = getContext();
    title.setText(displayable.getTitle(context));
    subtitle.setText(displayable.getTimeSinceLastUpdate(context));
    articleTitle.setText(displayable.getTitleResource());
    setCardviewMargin(displayable);
    ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getAvatarResource(), image);
    ImageLoader.with(context).load(displayable.getThumbnailUrl(), thumbnail);

    compositeSubscription.add(RxView.clicks(url)
        .subscribe(v -> context.startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(displayable.getUrl()))),
            throwable -> CrashReport.getInstance().log(throwable)));
  }

  private void setCardviewMargin(FeatureDisplayable displayable) {
    CardView.LayoutParams layoutParams =
        new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
            CardView.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(displayable.getMarginWidth(getContext(),
        getContext().getResources().getConfiguration().orientation), 0,
        displayable.getMarginWidth(getContext(),
            getContext().getResources().getConfiguration().orientation), 30);
    cardView.setLayoutParams(layoutParams);
  }
}
