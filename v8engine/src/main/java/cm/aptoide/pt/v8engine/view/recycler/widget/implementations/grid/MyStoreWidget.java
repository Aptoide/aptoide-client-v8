package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.MyStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreWidget extends Widget<MyStoreDisplayable> {

  private LinearLayout widgetLayout;
  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;

  public MyStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    widgetLayout = (LinearLayout) itemView.findViewById(R.id.widgetLayout);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_icon);
    storeName = (TextView) itemView.findViewById(R.id.store_name);
    exploreButton = (Button) itemView.findViewById(R.id.explore_button);
  }

  @Override public void bindView(MyStoreDisplayable displayable) {

    final FragmentActivity context = getContext();
    Store store = displayable.getMeta().getData();
    String storeTheme = store.getAppearance().getTheme();
    @ColorInt int color = getColorOrDefault(StoreThemeEnum.get(storeTheme), context);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackground(d);
    } else {
      Drawable d = context.getResources().getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackgroundDrawable(d);
    }
    exploreButton.setTextColor(color);

    ImageLoader.with(context).loadWithShadowCircleTransform(store.getAvatar(), storeIcon);

    storeName.setText(store.getName());
    compositeSubscription.add(RxView.clicks(exploreButton)
        .subscribe(click -> ((FragmentShower) context).pushFragment(
            V8Engine.getFragmentProvider().newStoreFragment(store.getName(), storeTheme))));
  }

  private int getColorOrDefault(StoreThemeEnum theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources().getColor(theme.getStoreHeader(), context.getTheme());
    } else {
      return context.getResources().getColor(theme.getStoreHeader());
    }
  }
}
