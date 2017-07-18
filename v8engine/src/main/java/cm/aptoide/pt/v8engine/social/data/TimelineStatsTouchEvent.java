package cm.aptoide.pt.v8engine.social.data;

/**
 * Created by jdandrade on 05/07/2017.
 */

public class TimelineStatsTouchEvent extends CardTouchEvent {
  private final ButtonClicked buttonClicked;

  public TimelineStatsTouchEvent(Post card, ButtonClicked buttonClicked, Type actionType) {
    super(card, actionType);
    this.buttonClicked = buttonClicked;
  }

  public ButtonClicked getButtonClicked() {
    return buttonClicked;
  }

  public enum ButtonClicked {
    FOLLOWERS, FOLLOWING, FOLLOWFRIENDS
  }
}
