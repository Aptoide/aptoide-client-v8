package cm.aptoide.pt.timeline.view.follow;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cm.aptoide.pt.R;
import cm.aptoide.pt.store.view.AddStoreDialog;
import cm.aptoide.pt.timeline.view.displayable.FollowStoreDisplayable;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by pedroribeiro on 22/02/17.
 */

public class FollowStoreWidget extends Widget<FollowStoreDisplayable> {

  private ImageView storeIcon;
  private LinearLayout storeLayout;

  public FollowStoreWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeIcon = (ImageView) itemView.findViewById(R.id.follow_store_image);
    storeLayout = (LinearLayout) itemView.findViewById(R.id.store_tab_follow_store_layout);
  }

  @Override public void bindView(FollowStoreDisplayable displayable) {
    FragmentManager fragmentManager = getContext().getSupportFragmentManager();
    compositeSubscription.add(RxView.clicks(storeLayout)
        .subscribe(click -> new AddStoreDialog().show(fragmentManager, "addStoreDialog")));
  }
}
