package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIM;
import static cm.aptoide.pt.promotions.PromotionsAdapter.CLAIMED;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNGRADE;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOAD;
import static cm.aptoide.pt.promotions.PromotionsAdapter.DOWNLOADING;
import static cm.aptoide.pt.promotions.PromotionsAdapter.INSTALL;
import static cm.aptoide.pt.promotions.PromotionsAdapter.UPDATE;

public class PromotionsViewHolderFactory {

  private final PublishSubject<PromotionAppClick> promotionAppClick;
  private final DecimalFormat decimalFormat;

  public PromotionsViewHolderFactory(PublishSubject<PromotionAppClick> promotionAppClick,
      DecimalFormat decimalFormat) {
    this.promotionAppClick = promotionAppClick;
    this.decimalFormat = decimalFormat;
  }

  public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder promotionAppViewHolder;

    switch (viewType) {
      case DOWNGRADE:
      case UPDATE:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), UPDATE, promotionAppClick,
            decimalFormat);
        break;
      case DOWNLOAD:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), DOWNLOAD, promotionAppClick,
            decimalFormat);
        break;
      case INSTALL:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), INSTALL, promotionAppClick,
            decimalFormat);
        break;
      case CLAIM:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIM, promotionAppClick,
            decimalFormat);
        break;
      case CLAIMED:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIMED, promotionAppClick,
            decimalFormat);
        break;
      case DOWNLOADING:
        promotionAppViewHolder = new PromotionAppDownloadingViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promotion_app_active_download, parent, false), promotionAppClick,
            decimalFormat);
        break;
      default:
        throw new IllegalArgumentException("Wrong view type of promotion app");
    }
    return promotionAppViewHolder;
  }
}
