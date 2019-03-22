package cm.aptoide.pt.reactions.network;

import cm.aptoide.pt.reactions.TopReaction;
import java.util.Collections;
import java.util.List;

public class LoadReactionModel {

  private final List<TopReaction> topReactionList;
  private final int total;
  private final String myReaction;

  public LoadReactionModel(int total, String myReaction, List<TopReaction> topReactionList) {
    this.topReactionList = topReactionList;
    this.total = total;
    this.myReaction = myReaction;
  }

  public LoadReactionModel() {
    total = -1;
    myReaction = "";
    topReactionList = Collections.emptyList();
  }

  public List<TopReaction> getTopReactionList() {
    return topReactionList;
  }

  public int getTotal() {
    return total;
  }

  public String getMyReaction() {
    return myReaction;
  }
}
