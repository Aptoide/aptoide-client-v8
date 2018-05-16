package cm.aptoide.pt.view.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta.GetAppMetaFile.Flags.Vote;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D01 on 16/05/2018.
 */

public class AppFlags {

  private final String review;
  private final List<FlagsVote> votes;

  public AppFlags(String review, List<Vote> votes) {

    this.review = review;
    this.votes = mapToFlagsVote(votes);
  }

  public AppFlags(String review, ArrayList<FlagsVote> votes) {
    this.review = review;
    this.votes = votes;
  }

  private List<FlagsVote> mapToFlagsVote(List<Vote> votes) {
    List<FlagsVote> flagsVotes = new ArrayList<>();
    if (votes != null) {
      for (Vote vote : votes) {
        flagsVotes.add(new FlagsVote(vote.getCount(), vote.getType()));
      }
    }
    return flagsVotes;
  }

  public List<FlagsVote> getVotes() {
    return votes;
  }

  public String getReview() {
    return review;
  }
}
