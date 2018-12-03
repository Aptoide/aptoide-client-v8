package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PromotionAppsViewHolder extends RecyclerView.ViewHolder {

  private int appState;

  public PromotionAppsViewHolder(View itemView, int appState) {
    super(itemView);
    this.appState = appState;
  }

  public void setApp(PromotionApp app) {

  }
}
