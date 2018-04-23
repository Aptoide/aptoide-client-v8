package cm.aptoide.pt.timeline.view.follow;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import cm.aptoide.pt.R;
import cm.aptoide.pt.store.view.AddStoreDialog;
import cm.aptoide.pt.timeline.view.displayable.FollowStoreDisplayable;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class FollowStoreWidget extends Widget<FollowStoreDisplayable> {

  private View storeLayout;

  public FollowStoreWidget(@NonNull View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeLayout = itemView.findViewById(R.id.store_tab_follow_store_layout);
  }

  @Override public void bindView(FollowStoreDisplayable displayable) {
    FragmentManager fragmentManager = getContext().getSupportFragmentManager();
    compositeSubscription.add(RxView.clicks(storeLayout)
        .subscribe(click -> new AddStoreDialog().show(fragmentManager, "addStoreDialog")));
  }
}
