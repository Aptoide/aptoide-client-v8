package cm.aptoide.pt.home.bundles.editorial;

import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;

/**
 * Created by franciscocalado on 04/09/2018.
 */

public class EditorialHomeEvent extends HomeEvent {

  private final String cardId;
  private final String groupId;

  public EditorialHomeEvent(String cardId, String groupId, HomeBundle bundle, int bundlePosition,
      Type clickType) {
    super(bundle, bundlePosition, clickType);
    this.cardId = cardId;
    this.groupId = groupId;
  }

  public String getCardId() {
    return cardId;
  }

  public String getGroupId() {
    return groupId;
  }
}
