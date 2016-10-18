/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.CircleTransform;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaWidget extends Widget<GridStoreMetaDisplayable> {

  private ImageView avatar;
  private ImageView ivSubscribe;
  private TextView name;
  private TextView description;
  private TextView subscribed;
  private LinearLayout subscribeButtonLayout;
  private TextView subscribersCount;
  private TextView appsCount;
  private TextView downloadsCount;

  private StoreThemeEnum theme;
  private boolean subscribedBool;

  public GridStoreMetaWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    avatar = (ImageView) itemView.findViewById(R.id.store_avatar_storehome);
    ivSubscribe = (ImageView) itemView.findViewById(R.id.iv_subscribed_icon);
    name = (TextView) itemView.findViewById(R.id.store_name_home_row);
    description = (TextView) itemView.findViewById(R.id.store_description_storehome);
    subscribed = (TextView) itemView.findViewById(R.id.store_subscribed_storehome);
    subscribeButtonLayout = (LinearLayout) itemView.findViewById(R.id.subscribe_button_layout);
    subscribersCount = (TextView) itemView.findViewById(R.id.store_subscribers_count);
    appsCount = (TextView) itemView.findViewById(R.id.store_apps_count);
    downloadsCount = (TextView) itemView.findViewById(R.id.store_downloads_count);
  }

  @Override public void bindView(GridStoreMetaDisplayable displayable) {

    GetStoreMeta getStoreMeta = displayable.getPojo();

    //@Cleanup Realm realm = DeprecatedDatabase.get();
    //subscribedBool = DeprecatedDatabase.StoreQ.get(getStoreMeta.getData().getId(), realm) != null;

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    subscribedBool = storeAccessor.get(getStoreMeta.getData().getId()).toBlocking().firstOrDefault(null) != null;

    this.theme = StoreThemeEnum.get(getStoreMeta.getData().getAppearance().getTheme());

    final Context context = itemView.getContext();
    if (TextUtils.isEmpty(getStoreMeta.getData().getAvatar())) {
      Glide.with(context)
          .fromResource()
          .load(R.drawable.ic_avatar_apps)
          .transform(new CircleTransform(context))
          .into(avatar);
    } else {
      Glide.with(context)
          .load(getStoreMeta.getData().getAvatar())
          .transform(new CircleTransform(context))
          .into(avatar);
    }

    @ColorInt int color = context.getResources().getColor(theme.getStoreHeader());
    name.setText(getStoreMeta.getData().getName());
    name.setTextColor(color);
    description.setText(getStoreMeta.getData().getAppearance().getDescription());
    appsCount.setText(NumberFormat.getNumberInstance(Locale.getDefault())
        .format(getStoreMeta.getData().getStats().getApps()));
    downloadsCount.setText(
        AptoideUtils.StringU.withSuffix(getStoreMeta.getData().getStats().getDownloads()));
    subscribersCount.setText(
        AptoideUtils.StringU.withSuffix(getStoreMeta.getData().getStats().getSubscribers()));
    subscribeButtonLayout.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

    handleSubscriptionLogic(getStoreMeta);
  }

  @Override public void onViewAttached() {

  }

  @Override public void onViewDetached() {

  }

  private void handleSubscriptionLogic(final GetStoreMeta getStoreMeta) {
    if (subscribedBool) {

      ivSubscribe.setImageResource(R.drawable.ic_check_white);
      subscribed.setText(itemView.getContext().getString(R.string.followed));

      subscribeButtonLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          subscribedBool = false;
          if (AptoideAccountManager.isLoggedIn()) {
            AptoideAccountManager.unsubscribeStore(getStoreMeta.getData().getName());
          }

          //@Cleanup Realm realm = DeprecatedDatabase.get();
          //DeprecatedDatabase.StoreQ.delete(getStoreMeta.getData().getId(), realm);
          StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
          storeAccessor.remove(getStoreMeta.getData().getId());

          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                  getStoreMeta.getData().getName()));
          handleSubscriptionLogic(getStoreMeta);
        }
      });
    } else {
      ivSubscribe.setImageResource(R.drawable.ic_plus_white);
      subscribed.setText(
          itemView.getContext().getString(R.string.appview_follow_store_button_text));
      subscribed.setCompoundDrawables(null, null, null, null);
          /*Drawable drawableLeft = itemView.getContext().getResources().getDrawable(R.drawable.ic_action_cancel_small_dark);
            if (drawableLeft != null) {
                drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
                subscribed.setCompoundDrawables(drawableLeft, null, null, null);
            }*/

      subscribeButtonLayout.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (!subscribedBool) {
            subscribedBool = true;
            StoreUtilsProxy.subscribeStore(getStoreMeta.getData().getName(), getStoreMeta -> {
              ShowMessage.asSnack(itemView,
                  AptoideUtils.StringU.getFormattedString(R.string.store_followed,
                      getStoreMeta.getData().getName()));
            }, Throwable::printStackTrace);
            handleSubscriptionLogic(getStoreMeta);
          }
        }
      });
    }
  }
}
