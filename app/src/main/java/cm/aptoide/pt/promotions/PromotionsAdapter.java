package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<GeneralPromotionAppsViewHolder> {

  static final int UPDATE = 0;
  static final int DOWNLOAD = 1;
  static final int DOWNLOADING = 2;
  static final int INSTALL = 3;
  static final int CLAIM = 4;
  static final int CLAIMED = 5;

  private List<PromotionApp> appsList;
  private PromotionsViewHolderFactory viewHolderFactory;

  public PromotionsAdapter(List<PromotionApp> appsList,
      PromotionsViewHolderFactory viewHolderFactory) {
    this.appsList = appsList;
    this.viewHolderFactory = viewHolderFactory;
  }

  @Override
  public GeneralPromotionAppsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return viewHolderFactory.createViewHolder(parent, viewType);
  }

  @Override public void onBindViewHolder(GeneralPromotionAppsViewHolder holder, int position) {
    holder.setApp(appsList.get(position));
  }

  @Override public int getItemViewType(int position) {
    PromotionApp app = appsList.get(position);
    int state;
    switch (app.getState()) {
      case UPDATE:
        state = UPDATE;
        break;
      case DOWNLOAD:
        state = DOWNLOAD;
        break;
      case DOWNLOADING:
        state = DOWNLOADING;
        break;
      case INSTALL:
        state = INSTALL;
        break;
      case CLAIM:
        state = CLAIM;
        break;
      case CLAIMED:
        state = CLAIMED;
        break;
      default:
        throw new IllegalArgumentException("Invalid type of app state");
    }
    return state;
  }

  @Override public int getItemCount() {
    return appsList.size();
  }

  public void setPromotionApps(List<PromotionApp> appsList) {
    this.appsList = appsList;
    notifyDataSetChanged();
  }
}
