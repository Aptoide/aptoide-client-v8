package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.networking.image.ImageLoader;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 29/08/2018.
 */

class MediaViewHolder extends RecyclerView.ViewHolder {

  private ImageView image;
  private ImageView videoThumbnail;
  private FrameLayout videoThumbnailContainer;
  private PublishSubject<String> editorialMediaClicked;

  public MediaViewHolder(View view, PublishSubject<String> editorialMediaClicked) {
    super(view);
    this.editorialMediaClicked = editorialMediaClicked;

    image = view.findViewById(R.id.image_item);
    videoThumbnail = view.findViewById(R.id.editorial_video_thumbnail);
    videoThumbnailContainer = view.findViewById(R.id.editorial_video_thumbnail_container);
  }

  public void setVisibility(EditorialMedia editorialMedia) {
    if (editorialMedia.hasUrl()) {
      if (editorialMedia.isVideo()) {
        if (editorialMedia.getThumbnail() != null) {
          ImageLoader.with(itemView.getContext())
              .load(editorialMedia.getThumbnail(), videoThumbnail);
        }
          videoThumbnailContainer.setVisibility(View.VISIBLE);
          videoThumbnailContainer.setOnClickListener(
              v -> editorialMediaClicked.onNext(editorialMedia.getUrl()));
      } else {
        ImageLoader.with(itemView.getContext())
            .load(editorialMedia.getUrl(), image);
        image.setVisibility(View.VISIBLE);
      }
    }
  }
}
