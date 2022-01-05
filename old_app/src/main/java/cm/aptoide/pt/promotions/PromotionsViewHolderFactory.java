package cm.aptoide.pt.promotions;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.themes.ThemeManager;
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
  private final ThemeManager themeManager;

  public PromotionsViewHolderFactory(PublishSubject<PromotionAppClick> promotionAppClick,
      ThemeManager themeManager) {
    this.promotionAppClick = promotionAppClick;
    this.themeManager = themeManager;
  }

  public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder promotionAppViewHolder;

    switch (viewType) {
      case DOWNGRADE:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), DOWNGRADE, promotionAppClick,
            themeManager);
        break;
      case UPDATE:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), UPDATE, promotionAppClick,
            themeManager);
        break;
      case DOWNLOAD:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), DOWNLOAD, promotionAppClick,
            themeManager);
        break;
      case INSTALL:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), INSTALL, promotionAppClick,
            themeManager);
        break;
      case CLAIM:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIM, promotionAppClick,
            themeManager);
        break;
      case CLAIMED:
        promotionAppViewHolder = new PromotionAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.promotion_app_inactive, parent, false), CLAIMED, promotionAppClick,
            themeManager);
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
