/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.VideoDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 8/10/16.
 */
public class VideoWidget extends Widget<VideoDisplayable> {

  private String cardType = "Video";
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
  private VideoDisplayable displayable;
  private View videoHeader;
  private TextView relatedTo;
  private String appName;
  private String packageName;

  public VideoWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
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
    this.displayable = displayable;
    Typeface typeFace =
        Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
    title.setText(displayable.getTitle());
    subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
    videoTitle.setTypeface(typeFace);
    videoTitle.setText(displayable.getVideoTitle());
    setCardviewMargin(displayable);
    ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);
    ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);
    play_button.setVisibility(View.VISIBLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      media_layout.setForeground(
          getContext().getResources().getDrawable(R.color.overlay_black, getContext().getTheme()));
    } else {
      media_layout.setForeground(getContext().getResources().getDrawable(R.color.overlay_black));
    }

    media_layout.setOnClickListener(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(cardType, Analytics.AppsTimeline.BLANK,
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard("Video", Analytics.AppsTimeline.BLANK,
          displayable.getVideoTitle(), displayable.getTitle(), Analytics.AppsTimeline.OPEN_VIDEO);
      displayable.getLink().launch(getContext());
      displayable.sendOpenVideoEvent(SendEventRequest.Body.Data.builder()
          .cardType(cardType)
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getLink().getUrl())
              .app(packageName)
              .build())
          .build(), AptoideAnalytics.OPEN_VIDEO);
    });

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installeds -> {
          if (installeds != null && !installeds.isEmpty()) {
            appName = installeds.get(0).getName();
            packageName = installeds.get(0).getPackageName();
          } else {
            setAppNameToFirstLinkedApp();
          }
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(getContext(), appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp();
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(getContext(), appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(videoHeader).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getBaseLink().launch(getContext());
      Analytics.AppsTimeline.clickOnCard(cardType, Analytics.AppsTimeline.BLANK,
          displayable.getVideoTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
      displayable.sendOpenVideoEvent(SendEventRequest.Body.Data.builder()
          .cardType(cardType)
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getBaseLink().getUrl())
              .app(packageName)
              .build())
          .build(), AptoideAnalytics.OPEN_CHANNEL);
    }));
  }

  //// TODO: 31/08/16 refactor this out of here
  private void knockWithSixpackCredentials(String url) {
    if (url == null) {
      return;
    }

    String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url).addHeader("authorization", credential).build();

    client.newCall(click).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Logger.d(this.getClass().getSimpleName(), "sixpack request fail " + call.toString());
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        Logger.d(this.getClass().getSimpleName(), "knock success");
        response.body().close();
      }
    });
  }

  private void setCardviewMargin(VideoDisplayable displayable) {
    CardView.LayoutParams layoutParams =
        new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
            CardView.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(displayable.getMarginWidth(getContext(),
        getContext().getResources().getConfiguration().orientation), 0,
        displayable.getMarginWidth(getContext(),
            getContext().getResources().getConfiguration().orientation), 30);
    cardView.setLayoutParams(layoutParams);
  }

  private void setAppNameToFirstLinkedApp() {
    if (!displayable.getRelatedToAppsList().isEmpty()) {
      appName = displayable.getRelatedToAppsList().get(0).getName();
    }
  }
}
