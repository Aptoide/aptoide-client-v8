package cm.aptoide.pt.discovery;

import android.net.Uri;
import android.widget.MediaController;

public class VideoObject {

  private String videoUrl;
  private MediaController controller;


  public VideoObject() {
  }

  public VideoObject(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }
}
