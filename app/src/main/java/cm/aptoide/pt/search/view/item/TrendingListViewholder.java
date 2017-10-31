package cm.aptoide.pt.search.view.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;

/**
 * Created by franciscocalado on 10/31/17.
 */

public class TrendingListViewholder extends RecyclerView.ViewHolder{

  private final TextView name;

  public TrendingListViewholder(View itemView) {
    super(itemView);
    this.name = (TextView) itemView.findViewById(R.id.app_name);
  }

  public void setItem(String name){
    this.name.setText(name);
  }
}
