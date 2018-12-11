package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOADING;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionsViewHolderFactory {

  private final PublishSubject<PromotionAppClick> promotionAppClick;

  public PromotionsViewHolderFactory(PublishSubject<PromotionAppClick> promotionAppClick) {
    this.promotionAppClick = promotionAppClick;
  }

  public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder promotionAppViewHolder;

    switch (viewType) {
      case UPDATE:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), UPDATE, promotionAppClick);
        break;
      case DOWNLOAD:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), DOWNLOAD, promotionAppClick);
        break;
      case INSTALL:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), INSTALL, promotionAppClick);
        break;
      case CLAIM:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIM, promotionAppClick);
        break;
      case CLAIMED:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIMED, promotionAppClick);
        break;
      case DOWNLOADING:
        promotionAppViewHolder = new PromotionAppDownloadingViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotion_app_active_download, parent, false), promotionAppClick);
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return promotionAppViewHolder;
  }
}
