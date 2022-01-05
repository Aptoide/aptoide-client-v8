package cm.aptoide.pt.view.app;

/**
 * Created by D01 on 21/05/2018.
 */

public class RatingVote {

  private final int count;
  private final int value;

  public RatingVote(int count, int value) {

    this.count = count;
    this.value = value;
  }

  public int getCount() {
    return count;
  }

  public int getValue() {
    return value;
  }
}
