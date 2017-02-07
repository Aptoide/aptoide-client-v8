package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.List;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreWidget extends Widget<MyStoreDisplayable> {

  private LinearLayout widgetLayout;
  private LinearLayout socialChannelsLayout;
  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;
  private TextView suggestionMessage;
  private TextView createStoreText;

  public MyStoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    widgetLayout = (LinearLayout) itemView.findViewById(R.id.widgetLayout);
    socialChannelsLayout = (LinearLayout) itemView.findViewById(R.id.social_channels);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_icon);
    storeName = (TextView) itemView.findViewById(R.id.store_name);
    suggestionMessage = (TextView) itemView.findViewById(R.id.create_store_text);
    createStoreText = (TextView) itemView.findViewById(R.id.created_store_text);
    exploreButton = (Button) itemView.findViewById(R.id.explore_button);
  }

  @Override public void bindView(MyStoreDisplayable displayable) {

    FragmentActivity context = getContext();
    Store store = displayable.getMeta().getData();
    suggestionMessage.setText(displayable.getSuggestionMessage(context));
    createStoreText.setText(displayable.getCreateStoreText());
    createStoreText.setVisibility(displayable.getCreateStoreTextViewVisibility());
    exploreButton.setText(displayable.getExploreButtonText());
    String storeTheme = store.getAppearance().getTheme();
    @ColorInt int color = getColorOrDefault(StoreThemeEnum.get(storeTheme), context);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackground(d);
    } else {
      Drawable d = context.getResources().getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackgroundDrawable(d);
    }
    exploreButton.setTextColor(color);

    ImageLoader.loadWithShadowCircleTransform(store.getAvatar(), storeIcon);

    storeName.setText(store.getName());
    compositeSubscription.add(RxView.clicks(exploreButton)
        .subscribe(click -> ((FragmentShower) context).pushFragmentV4(
            V8Engine.getFragmentProvider().newStoreFragment(store.getName(), storeTheme))));

    setupSocialLinks(displayable.getSocialChannels());
  }

  private int getColorOrDefault(StoreThemeEnum theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources().getColor(theme.getStoreHeader(), context.getTheme());
    } else {
      return context.getResources().getColor(theme.getStoreHeader());
    }
  }

  private void setupSocialLinks(List<Store.SocialChannel> socialChannels) {
    socialChannelsLayout.removeAllViews();
    LayoutInflater layoutInflater = getContext().getLayoutInflater();
    ImageButton imageButton;
    for (int i = 0; i < 1; i++) {
      Store.SocialChannel socialChannel = socialChannels.get(i);
      layoutInflater.inflate(R.layout.social_button_layout, socialChannelsLayout);
      imageButton = ((ImageButton) socialChannelsLayout.getChildAt(i));
      switch (socialChannel.getType()) {
        case FACEBOOK:
          imageButton.setImageDrawable(getDrawable(R.drawable.facebook_logo));
          break;
        case TWITTER:
          imageButton.setImageDrawable(getDrawable(R.drawable.twitter_logo));
          break;
        case YOUTUBE:
          imageButton.setImageDrawable(getDrawable(R.drawable.youtube_logo));
          break;
        case TWITCH:
          imageButton.setImageDrawable(getDrawable(R.drawable.twitch_logo));
          break;
      }
      imageButton.setOnClickListener(view -> sendEvent(socialChannel.getUrl()));
    }
  }

  private Drawable getDrawable(@DrawableRes int drawable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return getContext().getDrawable(drawable);
    } else {
      return getContext().getResources().getDrawable(drawable);
    }
  }

  public void sendEvent(String url) {
    if (!TextUtils.isEmpty(url)) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      getContext().startActivity(intent);
    }
  }
}
