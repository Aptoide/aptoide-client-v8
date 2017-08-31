package cm.aptoide.pt.social.data;

/**
 * Created by jdandrade on 25/06/2017.
 */

public class AppUpdateCardTouchEvent extends CardTouchEvent {

  public AppUpdateCardTouchEvent(AppUpdate card, Type touchType, int cardPosition) {
    super(card, cardPosition, touchType);
  }
}
