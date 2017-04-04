/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.VideoDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 8/10/16.
 */
public class VideoWidget extends CardWidget<VideoDisplayable> {

  private TextView title;
  private TextView subtitle;
  private ImageView image;
  private TextView videoTitle;
  private ImageView thumbnail;
  private View url;
  private Button getAppButton;
  private ImageView play_button;
  private FrameLayout media_layout;
  private CardView cardView;
  private View videoHeader;
  private TextView relatedTo;
  private String appName;
  private String packageName;

  public VideoWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    title = (TextView) itemView.findViewById(R.id.card_title);
    subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    image = (ImageView) itemView.findViewById(R.id.card_image);
    play_button = (ImageView) itemView.findViewById(R.id.play_button);
    media_layout = (FrameLayout) itemView.findViewById(R.id.media_layout);
    videoTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    getAppButton =
        (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    videoHeader = itemView.findViewById(R.id.displayable_social_timeline_video_header);
    relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
  }

  @Override public void bindView(VideoDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    Typeface typeFace =
        Typeface.createFromAsset(context.getAssets(), "fonts/DroidSerif-Regular.ttf");
    title.setText(displayable.getTitle());
    subtitle.setText(displayable.getTimeSinceLastUpdate(context));
    videoTitle.setTypeface(typeFace);
    videoTitle.setText(displayable.getVideoTitle());
    setCardViewMargin(displayable, cardView);
    ImageLoader.with(context).loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
    ImageLoader.with(context).load(displayable.getThumbnailUrl(), thumbnail);
    play_button.setVisibility(View.VISIBLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      media_layout.setForeground(
          context.getResources().getDrawable(R.color.overlay_black, context.getTheme()));
    } else {
      media_layout.setForeground(context.getResources().getDrawable(R.color.overlay_black));
    }

    media_layout.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(VideoDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getVideoTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_VIDEO);
      displayable.getLink().launch(context);
      displayable.sendOpenVideoEvent(packageName);
    });

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installeds -> {
          if (installeds != null && !installeds.isEmpty()) {
            appName = installeds.get(0).getName();
            packageName = installeds.get(0).getPackageName();
          } else {
            setAppNameToFirstLinkedApp(displayable);
          }
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(context, appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp(displayable);
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(context, appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(videoHeader).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getBaseLink().launch(context);
      Analytics.AppsTimeline.clickOnCard(VideoDisplayable.CARD_TYPE_NAME,
          Analytics.AppsTimeline.BLANK, displayable.getVideoTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
      displayable.sendOpenChannelEvent(packageName);
    }));
  }

  @Override String getCardTypeName() {
    return VideoDisplayable.CARD_TYPE_NAME;
  }

  private void setAppNameToFirstLinkedApp(VideoDisplayable displayable) {
    if (!displayable.getRelatedToAppsList().isEmpty()) {
      appName = displayable.getRelatedToAppsList().get(0).getName();
    }
  }
}
