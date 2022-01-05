package cm.aptoide.pt.app;

/**
 * Created by D01 on 22/05/2018.
 */

public class ReviewStats {

  private final long comments;
  private final long likes;
  private final long points;
  private final float rating;

  public ReviewStats(long comments, long likes, long points, float rating) {

    this.comments = comments;
    this.likes = likes;
    this.points = points;
    this.rating = rating;
  }

  public long getComments() {
    return comments;
  }

  public long getLikes() {
    return likes;
  }

  public long getPoints() {
    return points;
  }

  public float getRating() {
    return rating;
  }
}
