package cm.aptoide.pt.app.view;

import android.widget.ImageView;
import android.widget.VideoView;

/**
 * Created by D01 on 29/08/2018.
 */

class EditorialMedia {
  private ImageView image;
  private VideoView video;

  public EditorialMedia(ImageView image) {
    this.image = image;
  }

  public EditorialMedia(VideoView video) {
    this.video = video;
  }

  public ImageView getImage() {
    return image;
  }

  public VideoView getVideo() {
    return video;
  }

  public boolean isVideo() {
    return video != null;
  }

  public boolean isImage() {
    return image != null;
  }
}
