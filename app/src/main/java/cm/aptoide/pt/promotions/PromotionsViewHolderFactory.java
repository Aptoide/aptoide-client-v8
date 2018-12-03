package cm.aptoide.pt.promotions;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOADING;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionsViewHolderFactory {

  public GeneralPromotionAppsViewHolder createViewHolder(ViewGroup parent, int viewType) {
    GeneralPromotionAppsViewHolder promotionAppViewHolder;

    switch (viewType) {
      case UPDATE:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), UPDATE);
        break;
      case DOWNLOAD:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), DOWNLOAD);
        break;
      case INSTALL:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), INSTALL);
        break;
      case CLAIM:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIM);
        break;
      case CLAIMED:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIMED);
        break;
      case DOWNLOADING:
        promotionAppViewHolder = new PromotionAppDownloadingViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotion_app_active_download, parent, false));
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return promotionAppViewHolder;
  }
}
