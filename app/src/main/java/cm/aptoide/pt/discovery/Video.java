package cm.aptoide.pt.discovery;

public class Video {

  private String videoUrl;
  private String videoDescription;
  private double rating = 0.0;
  private String imageUrl;

  public Video(String videoUrl, String videoDescription, double score, String imageUrl) {
    this.videoUrl = videoUrl;
    this.videoDescription = videoDescription;
    this.rating = score;
    this.imageUrl = imageUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public String getVideoDescription() {
    return videoDescription;
  }

  public double getRating() {
    return rating;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}