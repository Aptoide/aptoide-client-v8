package cm.aptoide.pt.promotions;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsViewHolder> {

  private List<PromotionApp> appsList;
  private PromotionsViewHolderFactory viewHolderFactory;

  public PromotionsAdapter(List<PromotionApp> appsList,
      PromotionsViewHolderFactory viewHolderFactory) {
    this.appsList = appsList;
    this.viewHolderFactory = viewHolderFactory;
  }

  @Override public PromotionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return viewHolderFactory.createViewHolder(parent, viewType);
  }

  @Override public void onBindViewHolder(PromotionsViewHolder holder, int position) {
    holder.setApp(appsList.get(position));
  }

  @Override public int getItemCount() {
    return appsList.size();
  }
}
