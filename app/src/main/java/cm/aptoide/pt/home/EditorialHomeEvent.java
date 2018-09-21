package cm.aptoide.pt.home;

/**
 * Created by franciscocalado on 04/09/2018.
 */

public class EditorialHomeEvent extends HomeEvent {

  private String cardId;

  public EditorialHomeEvent(String cardId, HomeBundle bundle, int bundlePosition, Type clickType) {
    super(bundle, bundlePosition, clickType);
    this.cardId = cardId;
  }

  public String getCardId() {
    return cardId;
  }
}
