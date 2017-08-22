/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.view.store;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

public class GridStoreWidget extends Widget<GridStoreDisplayable> {

  private ImageView storeAvatar;
  private TextView storeName;
  private LinearLayout storeLayout;

  public GridStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    storeName = (TextView) itemView.findViewById(R.id.store_name_row);
    storeLayout = (LinearLayout) itemView.findViewById(R.id.store_main_layout_row);
  }

  @Override public void bindView(GridStoreDisplayable gridStoreDisplayable) {

    final Store store = gridStoreDisplayable.getPojo();

    storeName.setText(store.getName());

    storeLayout.setBackgroundColor(Color.WHITE);

    final Action1<Void> handleStoreClick = v -> getFragmentNavigator().navigateTo(
        V8Engine.getFragmentProvider()
            .newStoreFragment(gridStoreDisplayable.getPojo()
                .getName(), store.getAppearance()
                .getTheme()));
    compositeSubscription.add(RxView.clicks(storeLayout)
        .subscribe(handleStoreClick));

    final FragmentActivity context = getContext();
    if (store.getId() == -1 || TextUtils.isEmpty(store.getAvatar())) {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(R.drawable.ic_avatar_apps, storeAvatar,
              StoreTheme.get(store)
                  .getStoreHeaderColorResource(context.getResources(), context.getTheme()));
    } else {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(store.getAvatar(), storeAvatar, StoreTheme.get(store)
              .getStoreHeaderColorResource(context.getResources(), context.getTheme()));
    }
  }
}
