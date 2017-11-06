package cm.aptoide.pt.search.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.view.item.TrendingListViewholder;
import java.util.List;

/**
 * Created by franciscocalado on 10/31/17.
 */

public class TrendingListAdapter extends RecyclerView.Adapter<TrendingListViewholder> {

  private TrendingListViewholder.TrendingClickListener clickListener;
  private List<String> entries;

  public TrendingListAdapter(TrendingListViewholder.TrendingClickListener listener) {
  }

  @Override public TrendingListViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new TrendingListViewholder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.trending_list_item, parent, false), clickListener);
  }

  @Override public void onBindViewHolder(TrendingListViewholder holder, int position) {
    holder.setItem(entries.get(position));
  }

  @Override public int getItemCount() {
    return entries.size();
  }

  public void updateEntries(List<String> apps) {
    this.entries = apps;
    notifyDataSetChanged();
  }

  public String getEntry(int position){
    return entries.get(position);
  }

}
