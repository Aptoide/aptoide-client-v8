package cm.aptoide.pt.view.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;

/**
 * Created by D01 on 16/05/2018.
 */

public class FlagsVote {

  private final int count;
  private final VoteType voteType;

  public FlagsVote(int count, GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
    this.count = count;
    this.voteType = mapToFlagsVoteType(type);
  }

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

  private VoteType mapToFlagsVoteType(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
    VoteType flagsVoteVoteType = null;
    switch (type) {
      case FAKE:
        flagsVoteVoteType = VoteType.FAKE;
      case GOOD:
        flagsVoteVoteType = VoteType.GOOD;
      case VIRUS:
        flagsVoteVoteType = VoteType.VIRUS;
      case FREEZE:
        flagsVoteVoteType = VoteType.FREEZE;
      case LICENSE:
        flagsVoteVoteType = VoteType.LICENSE;
      default:
    }
    return flagsVoteVoteType;
  }

  public enum VoteType {
    FAKE, FREEZE, GOOD, LICENSE, VIRUS
  }
}
