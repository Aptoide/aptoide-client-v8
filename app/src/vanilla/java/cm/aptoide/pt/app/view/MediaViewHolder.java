package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import com.jaedongchicken.ytplayer.YoutubePlayerView;
import com.jaedongchicken.ytplayer.model.PlaybackQuality;
import com.jaedongchicken.ytplayer.model.YTParams;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 29/08/2018.
 */

class MediaViewHolder extends RecyclerView.ViewHolder {

  private ImageView image;
  private FrameLayout videoThumbnailContainer;
  private YoutubePlayerView videoPlayer;
  private PublishSubject<String> editorialMediaClicked;

  public MediaViewHolder(View view, PublishSubject<String> editorialMediaClicked) {
    super(view);
    this.editorialMediaClicked = editorialMediaClicked;

    image = view.findViewById(R.id.image_item);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
    videoPlayer = view.findViewById(R.id.video_view);
  }

  public void setVisibility(EditorialMedia editorialMedia) {
    if (editorialMedia.hasUrl()) {
      if (editorialMedia.isVideo() && editorialMedia.hasUrl()) {
        videoThumbnailContainer.setVisibility(View.VISIBLE);
        setupVideo(editorialMedia.getUrl());
      } else {
        ImageLoader.with(itemView.getContext())
            .load(editorialMedia.getUrl(), image);
        image.setVisibility(View.VISIBLE);
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
