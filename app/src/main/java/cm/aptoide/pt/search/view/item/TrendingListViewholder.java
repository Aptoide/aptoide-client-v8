package cm.aptoide.pt.search.view.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;

/**
 * Created by franciscocalado on 10/31/17.
 */

public class TrendingListViewholder extends RecyclerView.ViewHolder implements
    View.OnClickListener{

  public interface TrendingClickListener{

    void onClick(View v, int position);
  }

  private final TextView name;
  private final TrendingClickListener listener;
  public TrendingListViewholder(View itemView, TrendingClickListener listener) {
    super(itemView);
    this.listener=listener;
    this.name = (TextView) itemView.findViewById(R.id.app_name);
    itemView.setOnClickListener(this);
  }

  public void setItem(String name){
    this.name.setText(name);
  }

  @Override public void onClick(View view) {
    listener.onClick(view,getAdapterPosition());
  }
}
