package cm.aptoide.pt.app.view;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

/**
 * Created by franciscocalado on 03/10/2018.
 */

public class EditorialItemsLegacyViewHolder extends EditorialItemsViewHolder {

  YouTubePlayerView youTubePlayerView;
  private ImageView videoThumbnail;
  private FrameLayout videoThumbnailContainer;

  public EditorialItemsLegacyViewHolder(View view, DecimalFormat oneDecimalFormat,
      PublishSubject<EditorialEvent> uiEventListener) {
    super(view, oneDecimalFormat, uiEventListener);
    videoThumbnail = view.findViewById(R.id.editorial_video_thumbnail);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
    ((AppCompatActivity) view.getContext()).getLifecycle();
  }

  @Override void handleVideo(EditorialMedia editorialMedia) {
    if (editorialMedia.getThumbnail() != null) {
      ImageLoader.with(itemView.getContext())
          .load(editorialMedia.getThumbnail(), videoThumbnail);
    }
    if (editorialMedia.hasUrl()) {
      videoThumbnailContainer.setVisibility(View.VISIBLE);
      videoThumbnailContainer.setOnClickListener(v -> uiEventListener.onNext(
          new EditorialEvent(EditorialEvent.Type.MEDIA, editorialMedia.getUrl())));
    }
  }
}
