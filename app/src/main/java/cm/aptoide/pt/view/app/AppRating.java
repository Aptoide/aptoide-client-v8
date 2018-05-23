package cm.aptoide.pt.view.app;

import java.util.List;

/**
 * Created by D01 on 21/05/2018.
 */

public class AppRating {
  private final float average;
  private final int total;
  private final List<RatingVote> votes;

  public AppRating(float average, int total, List<RatingVote> votes) {

    this.average = average;
    this.total = total;
    this.votes = votes;
  }

  public float getAverage() {
    return average;
  }

  public int getTotal() {
    return total;
  }

  public List<RatingVote> getVotes() {
    return votes;
  }
}
