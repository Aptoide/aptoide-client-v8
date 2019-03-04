package cm.aptoide.pt.home;

public class ActionItemHomeEvent extends HomeEvent {

  private String cardId;

  public ActionItemHomeEvent(HomeBundle bundle, int bundlePosition, Type clickType, String cardId) {
    super(bundle, bundlePosition, clickType);
    this.cardId = cardId;
  }

  public String getCardId() {
    return cardId;
  }
}
