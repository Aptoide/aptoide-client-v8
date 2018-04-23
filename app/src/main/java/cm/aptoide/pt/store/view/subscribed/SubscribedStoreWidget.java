package cm.aptoide.pt.store.view.subscribed;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class SubscribedStoreWidget extends Widget<SubscribedStoreDisplayable> {

  private ImageView storeAvatar;
  private TextView storeName;
  private LinearLayout storeLayout;

  public SubscribedStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    storeName = (TextView) itemView.findViewById(R.id.store_name_row);
    storeLayout = (LinearLayout) itemView.findViewById(R.id.store_main_layout_row);
  }

  @Override public void bindView(SubscribedStoreDisplayable displayable) {

    final Store store = displayable.getPojo();

    storeName.setText(store.getStoreName());

    storeLayout.setBackgroundColor(Color.WHITE);
    compositeSubscription.add(RxView.clicks(storeLayout)
        .subscribe(__ -> {
          final Fragment fragment = AptoideApplication.getFragmentProvider()
              .newStoreFragment(displayable.getPojo()
                  .getStoreName(), displayable.getPojo()
                  .getTheme());
          getFragmentNavigator().navigateTo(fragment, true);
        }));

    final Context context = getContext();
    if (store.getStoreId() == -1 || TextUtils.isEmpty(store.getIconPath())) {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(R.drawable.ic_avatar_apps, storeAvatar,
              StoreTheme.get(store.getTheme())
                  .getStoreHeaderColorResource(context.getResources(), context.getTheme()));
    } else {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(store.getIconPath(), storeAvatar,
              StoreTheme.get(store.getTheme())
                  .getStoreHeaderColorResource(context.getResources(), context.getTheme()));
    }
  }
}
