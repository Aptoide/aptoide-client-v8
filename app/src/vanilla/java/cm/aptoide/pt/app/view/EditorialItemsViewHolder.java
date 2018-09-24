package cm.aptoide.pt.app.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import com.jaedongchicken.ytplayer.YoutubePlayerView;
import com.jaedongchicken.ytplayer.model.PlaybackQuality;
import com.jaedongchicken.ytplayer.model.YTParams;
import java.text.DecimalFormat;
import java.util.ArrayList;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsViewHolder extends RecyclerView.ViewHolder {
  private final ImageView appCardImage;
  private final TextView appCardName;
  private final TextView appCardRating;
  private final View appCardRatingLayout;
  private final DecimalFormat oneDecimalFormat;
  private TextView description;
  private View itemText;
  private View title;
  private TextView firstTitle;
  private TextView secondaryTitle;
  private TextView message;
  private View media;
  private ImageView image;
  private FrameLayout videoThumbnailContainer;
  private YoutubePlayerView videoPlayer;
  private RecyclerView mediaList;
  private View appCard;
  private MediaBundleAdapter mediaBundleAdapter;

  public EditorialItemsViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<String> editorialMediaClicked) {
    super(view);
    itemText = view.findViewById(R.id.editorial_item_text);
    title = view.findViewById(R.id.editorial_item_title);
    firstTitle = (TextView) view.findViewById(R.id.editorial_item_first_title);
    secondaryTitle = (TextView) view.findViewById(R.id.editorial_item_secondary_title);
    message = (TextView) view.findViewById(R.id.editorial_item_message);
    media = view.findViewById(R.id.editorial_item_media);
    image = (ImageView) view.findViewById(R.id.editorial_image);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
    description = (TextView) view.findViewById(R.id.editorial_image_description);
    mediaList = (RecyclerView) view.findViewById(R.id.editoral_image_list);
    appCard = view.findViewById(R.id.app_cardview);
    this.oneDecimalFormat = oneDecimalFormat;
    appCardImage = (ImageView) appCard.findViewById(R.id.app_icon_imageview);
    appCardName = (TextView) appCard.findViewById(R.id.app_title_textview);
    appCardRating = (TextView) appCard.findViewById(R.id.rating_label);
    appCardRatingLayout = appCard.findViewById(R.id.rating_layout);
    videoPlayer = view.findViewById(R.id.video_view);
    mediaBundleAdapter = new MediaBundleAdapter(new ArrayList<>(), editorialMediaClicked);
    LinearLayoutManager layoutManager =
        new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
    mediaList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        int margin = AptoideUtils.ScreenU.getPixelsForDip(6, view.getResources());
        outRect.set(0, margin, margin, margin);
      }
    });
    mediaList.setLayoutManager(layoutManager);
    mediaList.setAdapter(mediaBundleAdapter);
  }

  public void setVisibility(EditorialContent editorialItem, int position) {
    if (editorialItem.hasTitle() || editorialItem.hasMessage()) {
      itemText.setVisibility(View.VISIBLE);
      manageTitleVisibility(editorialItem, position);
      manageMessageVisibility(editorialItem);
    }
    manageMediaVisibility(editorialItem);
    /*TODO: Add this code back once the transition is completed
      if (editorialItem.isPlaceHolderType()) {
      ImageLoader.with(itemView.getContext())
          .load(editorialItem.getIcon(), appCardImage);
      appCardImage.setVisibility(View.VISIBLE);
      appCardName.setText(editorialItem.getAppName());
      appCardName.setVisibility(View.VISIBLE);
      if (editorialItem.getRating() == 0) {
        appCardRating.setText(R.string.appcardview_title_no_stars);
      } else {
        appCardRating.setText(oneDecimalFormat.format(editorialItem.getRating()));
      }
      appCardRatingLayout.setVisibility(View.VISIBLE);
      appCard.setVisibility(View.VISIBLE);
    }*/
  }

  private void manageTitleVisibility(EditorialContent editorialItem, int position) {
    if (editorialItem.hasTitle()) {
      title.setVisibility(View.VISIBLE);
      if (position == 0) {
        firstTitle.setText(editorialItem.getTitle());
        firstTitle.setVisibility(View.VISIBLE);
      } else {
        secondaryTitle.setText(editorialItem.getTitle());
        secondaryTitle.setVisibility(View.VISIBLE);
      }
    }
  }

  private void manageMessageVisibility(EditorialContent editorialItem) {
    if (editorialItem.hasMessage()) {
      message.setText(editorialItem.getMessage());
      message.setVisibility(View.VISIBLE);
    }
  }

  private void manageMediaVisibility(EditorialContent editorialItem) {
    if (editorialItem.hasMedia()) {
      media.setVisibility(View.VISIBLE);
      if (editorialItem.hasListOfMedia()) {
        mediaBundleAdapter.add(editorialItem.getMedia());
        mediaList.setVisibility(View.VISIBLE);
      } else {
        EditorialMedia editorialMedia = editorialItem.getMedia()
            .get(0);
        if (editorialMedia.isImage()) {
          ImageLoader.with(itemView.getContext())
              .load(editorialMedia.getUrl(), image);
          image.setVisibility(View.VISIBLE);
        }
        if (editorialMedia.isVideo() && editorialMedia.hasUrl()) {
          videoThumbnailContainer.setVisibility(View.VISIBLE);
          setupVideo(editorialMedia.getUrl());
        }
      }

      EditorialMedia editorialMedia = editorialItem.getMedia()
          .get(0);
      if (editorialMedia.hasDescription()) {
        description.setText(editorialMedia.getDescription());
        description.setVisibility(View.VISIBLE);
      }
    }
  }

  private void setupVideo(String url) {
    String[] splitUrl = url.split("=");
    YTParams params = new YTParams();
    params.setVolume(100);
    params.setPlaybackQuality(PlaybackQuality.small);

    videoPlayer.initializeWithCustomURL(splitUrl[1], params,
        new YoutubePlayerView.YouTubeListener() {
          @Override public void onReady() {
            videoPlayer.postDelayed(() -> {
              videoPlayer.setVisibility(View.VISIBLE);
              videoThumbnailContainer.setVisibility(View.GONE);
            }, 100);
          }

          @Override public void onStateChange(YoutubePlayerView.STATE state) {

          }

          @Override public void onPlaybackQualityChange(String arg) {

          }

          @Override public void onPlaybackRateChange(String arg) {

          }

          @Override public void onError(String arg) {

          }

          @Override public void onApiChange(String arg) {

          }

          @Override public void onCurrentSecond(double second) {

          }

          @Override public void onDuration(double duration) {

          }

          @Override public void logs(String log) {

          }
        });
  }
}
