package cm.aptoide.pt.discovery;

public class Video {

  private String videoUrl;
  private String videoDescription;
  private double score = 0.0;
  private String imageUrl;


  public Video(String videoUrl, String videoDescription, double score, String imageUrl) {
    this.videoUrl = videoUrl;
    this.videoDescription = videoDescription;
    this.score = score;
    this.imageUrl = imageUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public String getVideoDescription() {
    return videoDescription;
  }

  public double getScore() {
    return score;
  }

  public String getImageUrl(){
    return imageUrl;
  }
}