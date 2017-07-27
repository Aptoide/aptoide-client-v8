package cm.aptoide.pt.v8engine.social.view;

public class Direction {

  private static final int left = 0x1;
  private static final int right = 0x2;
  private static final int top = 0x4;
  private static final int bottom = 0x8;

  private final int direction;

  public Direction(int dx, int dy) {
    int temp = 0;
    if (dx > 0) {
      temp |= right;
    } else if (dx < 0) {
      temp |= left;
    }

    if (dy > 0) {
      temp |= bottom;
    } else if (dy < 0) {
      temp |= top;
    }

    this.direction = temp;
  }

  public boolean left() {
    return ((direction & left) == left);
  }

  public boolean right() {
    return ((direction & right) == right);
  }

  public boolean top() {
    return ((direction & top) == top);
  }

  public boolean bottom() {
    return ((direction & bottom) == bottom);
  }
}
