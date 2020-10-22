package cm.aptoide.pt.dataprovider.ws.v7.home;

import cm.aptoide.pt.bonus.BonusAppcModel;

public class EditorialActionItem {

  private ActionItemResponse actionItemResponse;
  private BonusAppcModel bonusAppcModel;

  public EditorialActionItem(ActionItemResponse actionItemResponse, BonusAppcModel bonusAppcModel) {
    this.actionItemResponse = actionItemResponse;
    this.bonusAppcModel = bonusAppcModel;
  }

  public ActionItemResponse getActionItemResponse() {
    return actionItemResponse;
  }

  public BonusAppcModel getBonusAppcModel() {
    return bonusAppcModel;
  }
}
