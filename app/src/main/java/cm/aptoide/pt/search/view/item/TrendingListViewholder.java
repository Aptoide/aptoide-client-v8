package cm.aptoide.pt.search.view.item;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.view.SearchViewModel;
import com.jakewharton.rxrelay.PublishRelay;

/**
 * Created by franciscocalado on 10/31/17.
 */

public class TrendingListViewholder extends RecyclerView.ViewHolder {


  private final TextView name;
  private final PublishRelay<String> listener;
  public TrendingListViewholder(View itemView, PublishRelay<String> listener) {
    super(itemView);
    this.listener=listener;
    this.name = (TextView) itemView.findViewById(R.id.app_name);
  }

  public void setItem(String name){
    this.name.setText(name);
    itemView.setOnClickListener(view -> listener.call(name));
  }

}
