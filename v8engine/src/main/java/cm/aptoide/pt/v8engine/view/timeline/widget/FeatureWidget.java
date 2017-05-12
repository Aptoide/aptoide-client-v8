package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.timeline.FeatureDisplayable;
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
  }

  @Override public void bindView(FeatureDisplayable displayable) {
    final FragmentActivity context = getContext();
    title.setText(displayable.getTitle(context));
    subtitle.setText(displayable.getTimeSinceLastUpdate(context));
    articleTitle.setText(displayable.getTitleResource());
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getAvatarResource(), image);
    ImageLoader.with(context)
        .load(displayable.getThumbnailUrl(), thumbnail);

    compositeSubscription.add(RxView.clicks(url)
        .subscribe(v -> context.startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(displayable.getUrl()))),
            throwable -> CrashReport.getInstance()
                .log(throwable)));
  }
}
