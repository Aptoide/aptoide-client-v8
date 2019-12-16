package cm.aptoide.pt.editorial;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 29/08/2018.
 */

class MediaViewHolder extends RecyclerView.ViewHolder {

  private TextView description;
  private ImageView image;
  private ImageView videoThumbnail;
  private FrameLayout videoThumbnailContainer;
  private PublishSubject<EditorialEvent> uiEventListener;

  public MediaViewHolder(View view, PublishSubject<EditorialEvent> uiEventListener) {
    super(view);
    this.uiEventListener = uiEventListener;

    image = view.findViewById(R.id.image_item);
    videoThumbnail = view.findViewById(R.id.editorial_video_thumbnail);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
    description = (TextView) view.findViewById(R.id.editorial_image_description);
  }

  public void setVisibility(EditorialMedia editorialMedia) {
    if (editorialMedia.hasUrl()) {
      if (editorialMedia.isVideo()) {
        if (editorialMedia.getThumbnail() != null) {
          ImageLoader.with(itemView.getContext())
              .load(editorialMedia.getThumbnail(), videoThumbnail);
        }
        videoThumbnailContainer.setVisibility(View.VISIBLE);
        videoThumbnailContainer.setOnClickListener(v -> uiEventListener.onNext(
            new EditorialEvent(EditorialEvent.Type.MEDIA, editorialMedia.getUrl())));
      } else {
        ImageLoader.with(itemView.getContext())
            .load(editorialMedia.getUrl(), image);
        image.setVisibility(View.VISIBLE);
      }
      description.setText(editorialMedia.getDescription());
    }
  }

  public void setDescriptionVisible() {
    description.setVisibility(View.VISIBLE);
  }
}
