package cm.aptoide.pt.view.app;

import java.util.List;

/**
 * Created by D01 on 16/05/2018.
 */

public class AppFlags {

  private final String review;
  private final List<FlagsVote> votes;

  public AppFlags(String review, List<FlagsVote> votes) {
    this.review = review;
    this.votes = votes;
  }

  public List<FlagsVote> getVotes() {
    return votes;
  }

  public String getReview() {
    return review;
  }
}
