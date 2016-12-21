package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SocialVideoDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideoWidget extends SocialCardWidget<SocialVideoDisplayable> {

  private static final String CARD_TYPE_NAME = "Social Video";

  private TextView title;
  private TextView subtitle;
  private ImageView storeAvatar;
  private ImageView userAvatar;
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
  private LinearLayout share;
  private LikeButton likeButton;
  private TextView numberLikes;
  private TextView numberComments;

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
    media_layout = (FrameLayout) itemView.findViewById(R.id.media_layout);
    videoTitle = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_title);
    thumbnail = (ImageView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_image);
    url = itemView.findViewById(R.id.partial_social_timeline_thumbnail);
    getAppButton =
        (Button) itemView.findViewById(R.id.partial_social_timeline_thumbnail_get_app_button);
    cardView = (CardView) itemView.findViewById(R.id.card);
    videoHeader = itemView.findViewById(R.id.displayable_social_timeline_video_header);
    relatedTo = (TextView) itemView.findViewById(R.id.partial_social_timeline_thumbnail_related_to);
    share = (LinearLayout) itemView.findViewById(R.id.social_share);
    likeButton = (LikeButton) itemView.findViewById(R.id.social_like_test);
    numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
  }

  @Override public void bindView(SocialVideoDisplayable displayable) {
    super.bindView(displayable);
    Typeface typeFace =
        Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSerif-Regular.ttf");
    title.setText(displayable.getTitle());
    subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
    videoTitle.setTypeface(typeFace);
    videoTitle.setText(displayable.getVideoTitle());
    setCardViewMargin(displayable, cardView);
    ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), storeAvatar);
    ImageLoader.loadWithShadowCircleTransform(displayable.getUser().getAvatar(), userAvatar);
    ImageLoader.load(displayable.getThumbnailUrl(), thumbnail);
    play_button.setVisibility(View.VISIBLE);
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    numberComments.setVisibility(View.VISIBLE);
    numberComments.setText(String.valueOf(displayable.getNumberOfComments()));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      media_layout.setForeground(
          getContext().getResources().getDrawable(R.color.overlay_black, getContext().getTheme()));
    } else {
      media_layout.setForeground(getContext().getResources().getDrawable(R.color.overlay_black));
    }

    media_layout.setOnClickListener(v -> {
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
          .build(), AptoideAnalytics.OPEN_VIDEO);
    });

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
          .build(), AptoideAnalytics.OPEN_CHANNEL);
    }));
    likeButton.setOnLikeListener(new OnLikeListener() {
      @Override public void liked(LikeButton likeButton) {
        likeCard(displayable, 1);
        numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
      }

      @Override public void unLiked(LikeButton likeButton) {
        likeButton.setLiked(true);
        //likeCard(displayable, getCardTypeName(), -1);
        //numberLikes.setText("0");
      }
    });
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
