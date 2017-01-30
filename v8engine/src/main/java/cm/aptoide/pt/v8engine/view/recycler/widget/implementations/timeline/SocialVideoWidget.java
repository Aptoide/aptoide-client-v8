package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

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
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineClickEvent;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialVideoDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideoWidget extends SocialCardWidget<SocialVideoDisplayable> {

  private static final String CARD_TYPE_NAME = "SOCIAL_VIDEO";

  private TextView title;
  private TextView subtitle;
  private ImageView storeAvatar;
  private ImageView userAvatar;
  private TextView videoTitle;
  private ImageView thumbnail;
  private View url;
  private Button getAppButton;
  private ImageView play_button;
  private FrameLayout mediaLayout;
  private CardView cardView;
  private View videoHeader;
  private TextView relatedTo;
  private String appName;
  private String packageName;
  //private TextView sharedBy;

  public SocialVideoWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    title = (TextView) itemView.findViewById(R.id.card_title);
    subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
    storeAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    userAvatar = (ImageView) itemView.findViewById(R.id.card_user_avatar);
    play_button = (ImageView) itemView.findViewById(R.id.play_button);
    mediaLayout = (FrameLayout) itemView.findViewById(R.id.media_layout);
    videoTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    getAppButton =
        (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    videoHeader = itemView.findViewById(R.id.social_header);
    relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
    //sharedBy = (TextView) itemView.findViewById(R.id.social_shared_by);
  }

  @Override public void bindView(SocialVideoDisplayable displayable) {
    super.bindView(displayable);
    Typeface typeFace =
        Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
    //title.setText(displayable.getTitle());
    //subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
    if (displayable.getStore() != null) {
      title.setVisibility(View.VISIBLE);
      title.setText(displayable.getStore().getName());
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.loadWithShadowCircleTransform(displayable.getStore().getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(displayable.getUser().getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
      } else {
        subtitle.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      subtitle.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        title.setVisibility(View.VISIBLE);
        title.setText(displayable.getUser().getName());
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), storeAvatar);
      }
    }
    videoTitle.setTypeface(typeFace);
    videoTitle.setText(displayable.getVideoTitle());
    setCardViewMargin(displayable, cardView);
    ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);
    play_button.setVisibility(View.VISIBLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mediaLayout.setForeground(
          getContext().getResources().getDrawable(R.color.overlay_black, getContext().getTheme()));
    } else {
      mediaLayout.setForeground(getContext().getResources().getDrawable(R.color.overlay_black));
    }

    compositeSubscription.add(RxView.clicks(mediaLayout).subscribe(v -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          displayable.getVideoTitle(), displayable.getTitle(), Analytics.AppsTimeline.OPEN_VIDEO);
      displayable.getLink().launch(getContext());
      displayable.sendOpenVideoEvent(SendEventRequest.Body.Data.builder()
          .cardType(getCardTypeName())
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getLink().getUrl())
              .app(packageName)
              .build())
          .build(), TimelineClickEvent.OPEN_VIDEO);
    }));

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installedList -> {
          if (installedList != null && !installedList.isEmpty()) {
            appName = installedList.get(0).getName();
            packageName = installedList.get(0).getPackageName();
          } else {
            setAppNameToFirstLinkedApp(displayable);
          }
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(getContext(), appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp(displayable);
          if (appName != null) {
            relatedTo.setText(displayable.getAppRelatedText(getContext(), appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(videoHeader).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.getBaseLink().launch(getContext());
      Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
          displayable.getVideoTitle(), displayable.getTitle(),
          Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
      displayable.sendOpenVideoEvent(SendEventRequest.Body.Data.builder()
          .cardType(getCardTypeName())
          .source(displayable.getTitle())
          .specific(SendEventRequest.Body.Specific.builder()
              .url(displayable.getBaseLink().getUrl())
              .app(packageName)
              .build())
          .build(), TimelineClickEvent.OPEN_CHANNEL);
    }));
  }

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
  }

  private void setAppNameToFirstLinkedApp(SocialVideoDisplayable displayable) {
    if (!displayable.getRelatedToAppsList().isEmpty()) {
      appName = displayable.getRelatedToAppsList().get(0).getName();
    }
  }
}
