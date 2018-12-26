package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.app.DownloadModel;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  static final int UPDATE = 0;
  static final int DOWNLOAD = 1;
  static final int DOWNLOADING = 2;
  static final int INSTALL = 3;
  static final int CLAIM = 4;
  static final int CLAIMED = 5;
  static final int DOWNGRADE = 6;

  private List<PromotionViewApp> appsList;
  private PromotionsViewHolderFactory viewHolderFactory;
  private boolean isWalletInstalled;

  public PromotionsAdapter(List<PromotionViewApp> appsList,
      PromotionsViewHolderFactory viewHolderFactory) {
    this.appsList = appsList;
    this.viewHolderFactory = viewHolderFactory;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return viewHolderFactory.createViewHolder(parent, viewType);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof PromotionAppDownloadingViewHolder) {
      ((PromotionAppDownloadingViewHolder) holder).setApp(appsList.get(position));
    } else if (holder instanceof PromotionAppViewHolder) {
      ((PromotionAppViewHolder) holder).setApp(appsList.get(position), isWalletInstalled);
    } else {
      throw new IllegalStateException("Invalid type of ViewHolder");
    }
  }

  @Override public int getItemViewType(int position) {
    PromotionViewApp app = appsList.get(position);
    int state;
    if (app.isClaimed()) {
      return CLAIMED;
    } else {
      DownloadModel downloadModel = app.getDownloadModel();

      if (downloadModel.isDownloading()) {
        return DOWNLOADING;
      } else {
        switch (downloadModel.getAction()) {
          case DOWNGRADE:
            state = DOWNGRADE;
            break;
          case INSTALL:
            state = INSTALL;
            break;
          case OPEN:
            state = CLAIM;
            break;
          case UPDATE:
            state = UPDATE;
            break;
          default:
            throw new IllegalArgumentException("Invalid type of download action");
        }
        return state;
      }
    }
  }

  @Override public int getItemCount() {
    return appsList.size();
  }

  public void setPromotionApp(PromotionViewApp promotionViewApp) {
    int index = this.appsList.indexOf(promotionViewApp);
    if (index != -1) {
      this.appsList.set(index, promotionViewApp);
      notifyItemChanged(index);
    } else {
      this.appsList.add(promotionViewApp);
      notifyDataSetChanged();
    }
  }

  public void isWalletInstalled(boolean isWalletInstalled) {
    this.isWalletInstalled = isWalletInstalled;
    notifyDataSetChanged();
  }

  public void updateClaimStatus(String packageName) {
    for (PromotionViewApp promotionViewApp : appsList) {
      if (promotionViewApp.getPackageName()
          .equals(packageName)) {
        promotionViewApp.setClaimed();
        notifyDataSetChanged();
      }
    }
  }
}
