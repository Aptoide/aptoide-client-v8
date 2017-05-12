package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialVideoDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideoWidget extends SocialCardWidget<SocialVideoDisplayable> {

  private TextView title;
  private TextView subtitle;
  private TextView videoTitle;
  private ImageView thumbnail;
  private ImageView play_button;
  private FrameLayout mediaLayout;
  private CardView cardView;
  private View videoHeader;
  private TextView relatedTo;
  private String appName;
  private String packageName;
  private RatingBar ratingBar;

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
    thumbnail = (ImageView) itemView.findViewById(R.id.featured_graphic);
    cardView = (CardView) itemView.findViewById(R.id.card);
    videoHeader = itemView.findViewById(R.id.social_header);
    relatedTo = (TextView) itemView.findViewById(R.id.app_name);
    ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
  }

  @Override public void bindView(SocialVideoDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    if (displayable.getStore() != null) {
      title.setVisibility(View.VISIBLE);
      title.setText(displayable.getStyledTitle(context, displayable.getStore()
          .getName()));
      storeAvatar.setVisibility(View.VISIBLE);
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(displayable.getStore()
              .getAvatar(), storeAvatar);
      if (displayable.getUser() != null) {
        subtitle.setVisibility(View.VISIBLE);
        subtitle.setText(displayable.getUser()
            .getName());
        userAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), userAvatar);
      } else {
        subtitle.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);
      }
    } else {
      subtitle.setVisibility(View.GONE);
      userAvatar.setVisibility(View.GONE);
      if (displayable.getUser() != null) {
        title.setVisibility(View.VISIBLE);
        title.setText(displayable.getStyledTitle(context, displayable.getUser()
            .getName()));
        storeAvatar.setVisibility(View.VISIBLE);
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(displayable.getUser()
                .getAvatar(), storeAvatar);
      }
    }

    ratingBar.setVisibility(View.INVISIBLE);
    videoTitle.setText(displayable.getVideoTitle());
    setCardViewMargin(displayable, cardView);
    ImageLoader.with(context)
        .load(displayable.getThumbnailUrl(), thumbnail);
    thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
    play_button.setVisibility(View.VISIBLE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mediaLayout.setForeground(context.getResources()
          .getDrawable(R.color.overlay_black, context.getTheme()));
    } else {
      mediaLayout.setForeground(context.getResources()
          .getDrawable(R.color.overlay_black));
    }

    compositeSubscription.add(RxView.clicks(mediaLayout)
        .subscribe(v -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              displayable.getVideoTitle(), displayable.getTitle(),
              Analytics.AppsTimeline.OPEN_VIDEO);
          displayable.getLink()
              .launch(context);
          displayable.sendOpenVideoEvent(packageName);
        }));

    compositeSubscription.add(displayable.getRelatedToApplication()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(installedList -> {
          if (installedList != null && !installedList.isEmpty()) {
            appName = installedList.get(0)
                .getName();
            packageName = installedList.get(0)
                .getPackageName();
          } else {
            setAppNameToFirstLinkedApp(displayable);
          }
          if (appName != null) {
            relatedTo.setTextSize(11);
            relatedTo.setText(displayable.getAppRelatedText(context, appName));
          }
        }, throwable -> {
          setAppNameToFirstLinkedApp(displayable);
          if (appName != null) {
            relatedTo.setTextSize(11);
            relatedTo.setText(displayable.getAppRelatedText(context, appName));
          }
          throwable.printStackTrace();
        }));

    compositeSubscription.add(RxView.clicks(videoHeader)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          displayable.getBaseLink()
              .launch(context);
          Analytics.AppsTimeline.clickOnCard(getCardTypeName(), Analytics.AppsTimeline.BLANK,
              displayable.getVideoTitle(), displayable.getTitle(),
              Analytics.AppsTimeline.OPEN_VIDEO_HEADER);
          displayable.sendOpenChannelEvent(packageName);
        }));
  }

  @Override String getCardTypeName() {
    return SocialVideoDisplayable.CARD_TYPE_NAME;
  }

  private void setAppNameToFirstLinkedApp(SocialVideoDisplayable displayable) {
    if (!displayable.getRelatedToAppsList()
        .isEmpty()) {
      appName = displayable.getRelatedToAppsList()
          .get(0)
          .getName();
    }
  }
}
