package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 16/05/2018.
 */

public class FlagsVote {

  private final int count;
  private final VoteType voteType;

  public FlagsVote(int count, VoteType voteType) {
    this.count = count;
    this.voteType = voteType;
  }

  public int getCount() {
    return count;
  }

  public VoteType getVoteType() {
    return voteType;
  }

  public enum VoteType {
    FAKE, FREEZE, GOOD, LICENSE, VIRUS
  }
}
