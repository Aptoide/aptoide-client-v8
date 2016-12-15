package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 06/12/2016.
 */

public class RecommendedStoreWidget extends Widget<RecommendedStoreDisplayable> {

  private final CompositeSubscription subscriptions;
  private TextView storeName;
  private TextView followingUsers;
  private TextView numberStoreApps;
  private ImageView storeIcon;
  private AppCompatButton followButton;

  public RecommendedStoreWidget(View itemView) {
    super(itemView);
    subscriptions = new CompositeSubscription();
  }

  @Override protected void assignViews(View itemView) {
    storeName = (TextView) itemView.findViewById(R.id.recommended_store_name);
    followingUsers = (TextView) itemView.findViewById(R.id.recommended_store_users);
    numberStoreApps = (TextView) itemView.findViewById(R.id.recommended_store_apps);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    followButton = (AppCompatButton) itemView.findViewById(R.id.recommended_store_action);
  }

  @Override public void bindView(RecommendedStoreDisplayable displayable) {
    Store store = displayable.getPojo();
    storeName.setText(store.getName());
    followingUsers.setText(String.valueOf(store.getStats().getSubscribers()));
    numberStoreApps.setText(String.valueOf(store.getStats().getApps()));
    ImageLoader.loadWithShadowCircleTransform(store.getAvatar(), storeIcon,
        StoreThemeEnum.get(store).getStoreHeaderInt());
    subscriptions.add(RxView.clicks(followButton).map(click -> {
      followButton.setEnabled(false);
      StoreUtilsProxy.subscribeStore(store.getName());
      return null;
    }).subscribe(storeSubscribed -> {
      followButton.setEnabled(true);
      //followButton.setText();
    }));
    followButton.setSupportBackgroundTintList(
        ColorStateList.valueOf(StoreThemeEnum.get(store).getStoreHeaderInt()));
  }

  @Override public void unbindView() {
    subscriptions.clear();
    super.unbindView();
  }
}
