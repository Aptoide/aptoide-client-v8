package cm.aptoide.pt.promotions;

import android.view.View;
import cm.aptoide.pt.home.apps.App;

public class PromotionAppViewHolder extends GeneralPromotionAppsViewHolder {

  private int appState;

  public PromotionAppViewHolder(View itemView, int appState) {
    super(itemView);
    this.appState = appState;
  }

  @Override public void setApp(PromotionApp app) {

  }
}
