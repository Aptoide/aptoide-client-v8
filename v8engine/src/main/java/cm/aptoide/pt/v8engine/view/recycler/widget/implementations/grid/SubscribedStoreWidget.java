/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 11-05-2016. //todo: código duplicado, se cair a reflexão, deixa de o ser.
 */
@Displayables({ SubscribedStoreDisplayable.class }) public class SubscribedStoreWidget
    extends Widget<SubscribedStoreDisplayable> {

  private static final String TAG = SubscribedStoreWidget.class.getSimpleName();

  private ImageView storeAvatar;
  private TextView storeName;
  //private TextView storeUnsubscribe;
  private LinearLayout storeLayout;
  //private View infoLayout;

  public SubscribedStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    storeAvatar = (ImageView) itemView.findViewById(R.id.store_avatar_row);
    storeName = (TextView) itemView.findViewById(R.id.store_name_row);
    //storeUnsubscribe = (TextView) itemView.findViewById(R.id.store_unsubscribe_row);
    storeLayout = (LinearLayout) itemView.findViewById(R.id.store_main_layout_row);
    //infoLayout = itemView.findViewById(R.id.store_layout_subscribers);
    //storeUnsubscribe.setText(R.string.unfollow);
  }

  @Override public void unbindView() {

  }

  @Override public void bindView(SubscribedStoreDisplayable displayable) {

    final Context context = itemView.getContext();
    final Store store = displayable.getPojo();

    storeName.setText(store.getStoreName());
    //infoLayout.setVisibility(View.GONE);

    @ColorInt int color =
        context.getResources().getColor(StoreThemeEnum.get(store.getTheme()).getStoreHeader());
    storeLayout.setBackgroundColor(Color.WHITE);
    storeLayout.setOnClickListener(
        v -> FragmentUtils.replaceFragmentV4((FragmentActivity) v.getContext(),
            V8Engine.getFragmentProvider()
                .newStoreFragment(displayable.getPojo().getStoreName(),
                    displayable.getPojo().getTheme())));

    if (store.getStoreId() == -1 || TextUtils.isEmpty(store.getIconPath())) {
      ImageLoader.loadWithShadowCircleTransform(R.drawable.ic_avatar_apps, storeAvatar,
          StoreThemeEnum.get(store.getTheme()).getStoreHeaderInt());
    } else {
      ImageLoader.loadWithShadowCircleTransform(store.getIconPath(), storeAvatar,
          StoreThemeEnum.get(store.getTheme()).getStoreHeaderInt());
    }

    //storeUnsubscribe.setOnClickListener(v -> {
    //  compositeSubscription.add(
    //      GenericDialogs.createGenericYesNoCancelMessage(itemView.getContext(),
    //          displayable.getPojo().getStoreName(),
    //          AptoideUtils.StringU.getFormattedString(R.string.unfollow_yes_no))
    //          .subscribe(eResponse -> {
    //            switch (eResponse) {
    //              case YES:
    //
    //                if (AptoideAccountManager.getInstance().isLoggedIn()) {
    //                  AptoideAccountManager.getInstance().unsubscribeStore(store.getStoreName());
    //                }
    //
    //                //@Cleanup Realm realm = DeprecatedDatabase.get();
    //                //DeprecatedDatabase.StoreQ.delete(store.getStoreId(), realm);
    //                StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    //                storeAccessor.remove(store.getStoreId());
    //
    //                break;
    //            }
    //          }, e -> {
    //            Logger.e(TAG, e);
    //            CrashReport.getInstance().log(e);
    //          }));
    //});
  }
}
