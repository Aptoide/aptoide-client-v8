package cm.aptoide.pt.v8engine.view.store.subscribed;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by neuro on 11-05-2016. //todo: código duplicado, se cair a reflexão, deixa de o ser.
 */
@Displayables({ SubscribedStoreDisplayable.class }) public class SubscribedStoreWidget
    extends Widget<SubscribedStoreDisplayable> {

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
    compositeSubscription.add(RxView.clicks(storeLayout).subscribe(__ -> {
      final Fragment fragment = V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getPojo().getStoreName(), displayable.getPojo().getTheme());
      getFragmentNavigator().navigateTo(fragment);
    }));

    final Context context = getContext();
    if (store.getStoreId() == -1 || TextUtils.isEmpty(store.getIconPath())) {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(R.drawable.ic_avatar_apps, storeAvatar,
              StoreThemeEnum.get(store.getTheme()).getStoreHeaderInt());
    } else {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(store.getIconPath(), storeAvatar,
              StoreThemeEnum.get(store.getTheme()).getStoreHeaderInt());
    }
  }
}
