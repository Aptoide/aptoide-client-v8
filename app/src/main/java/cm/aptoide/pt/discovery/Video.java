package cm.aptoide.pt.discovery;

public class Video {

  private String videoUrl;
  private String videoDescription;


  public Video(String videoUrl, String videoDescription) {
    this.videoUrl = videoUrl;
    this.videoDescription = videoDescription;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public String getVideoDescription() {
    return videoDescription;
  }
}