package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class GeneralPromotionAppsViewHolder extends RecyclerView.ViewHolder {
  public GeneralPromotionAppsViewHolder(View itemView) {
    super(itemView);
  }

  public abstract void setApp(PromotionApp app);
}
