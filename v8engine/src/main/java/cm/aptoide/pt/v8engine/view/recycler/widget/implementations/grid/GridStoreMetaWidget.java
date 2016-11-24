package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.CircleTransform;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaWidget extends Widget<GridStoreMetaDisplayable> {

  private View containerLayout;
  private ImageView image;
  private TextView name;
  private TextView description;
  private Button subscribeButton;
  private TextView subscribersCount;
  private TextView appsCount;
  private TextView downloadsCount;
  private ImageButton youtubeButton;
  private ImageButton twitchButton;
  private ImageButton twitterButton;

  public GridStoreMetaWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    containerLayout = itemView.findViewById(R.id.outter_layout);
    image = (ImageView) itemView.findViewById(R.id.image);
    name = (TextView) itemView.findViewById(R.id.name);
    description = (TextView) itemView.findViewById(R.id.description);
    subscribeButton = (Button) itemView.findViewById(R.id.follow_btn);
    subscribersCount = (TextView) itemView.findViewById(R.id.subscribers);
    appsCount = (TextView) itemView.findViewById(R.id.apps);
    downloadsCount = (TextView) itemView.findViewById(R.id.downloads);
    youtubeButton = (ImageButton) itemView.findViewById(R.id.youtube_button);
    twitchButton = (ImageButton) itemView.findViewById(R.id.twitch_button);
    twitterButton = (ImageButton) itemView.findViewById(R.id.twitter_button);
  }

  private void showStoreData(cm.aptoide.pt.model.v7.store.Store store, StoreThemeEnum theme,
      Context context) {

    @ColorInt int color = context.getResources().getColor(theme.getStoreHeader());
    containerLayout.setBackgroundColor(color);
    subscribeButton.setTextColor(color);

    name.setText(store.getName());
    description.setText(store.getAppearance().getDescription());
    appsCount.setText(
        NumberFormat.getNumberInstance(Locale.getDefault()).format(store.getStats().getApps()));
    downloadsCount.setText(AptoideUtils.StringU.withSuffix(store.getStats().getDownloads()));
    subscribersCount.setText(AptoideUtils.StringU.withSuffix(store.getStats().getSubscribers()));
  }

  private void showStoreImage(cm.aptoide.pt.model.v7.store.Store store, Context context) {
    if (TextUtils.isEmpty(store.getAvatar())) {
      Glide.with(context)
          .fromResource()
          .load(R.drawable.ic_avatar_apps)
          .transform(new CircleTransform(context))
          .into(image);
    } else {
      Glide.with(context)
          .load(store.getAvatar())
          .transform(new CircleTransform(context))
          .into(image);
    }
  }

  private void setupSocialChannelButtons(cm.aptoide.pt.model.v7.store.Store store) {
    List<cm.aptoide.pt.model.v7.store.Store.SocialChannel> socialChannels =
        store.getSocialChannels();
    if (socialChannels == null || socialChannels.isEmpty()) return;

    compositeSubscription.add(
        Observable.from(socialChannels).flatMap( socialChannel -> {
          Event.Name eventName = socialChannel.getEvent().getName();

          switch (eventName) {
            // uncomment for further development
            //case facebook:
            //  youtubeButton.setBackgroundResource( ?? );
            //  youtubeButton.setClickable(true);
            //  youtubeButton.setVisibility(View.VISIBLE);
            //  return RxView.clicks(youtubeButton).map(aVoid -> socialChannel);

            case youtube:
              youtubeButton.setClickable(true);
              youtubeButton.setVisibility(View.VISIBLE);
              return RxView.clicks(youtubeButton).map(aVoid -> socialChannel);

            case twitch:
              twitchButton.setClickable(true);
              twitchButton.setVisibility(View.VISIBLE);
              return RxView.clicks(twitchButton).map(aVoid -> socialChannel);

            case twitter:
              twitterButton.setClickable(true);
              twitterButton.setVisibility(View.VISIBLE);
              return RxView.clicks(twitterButton).map(aVoid -> socialChannel);

            default:
              return Observable.empty();
          }
        }).subscribe(socialChannel -> {
          handleEvent(socialChannel);
        })
    );
  }

  private Action1<Void> handleEvent(
      cm.aptoide.pt.model.v7.store.Store.SocialChannel socialChannel) {
    final String action = socialChannel.getEvent().getAction();
    if (!TextUtils.isEmpty(action)) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(action));
      itemView.getContext().startActivity(i);
    }
    return null;
  }

  @Override public void bindView(GridStoreMetaDisplayable displayable) {

    final GetStoreMeta getStoreMeta = displayable.getPojo();
    final cm.aptoide.pt.model.v7.store.Store store = getStoreMeta.getData();
    final StoreThemeEnum theme = StoreThemeEnum.get(store.getAppearance().getTheme());
    final Context context = itemView.getContext();

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    boolean isStoreSubscribed =
        storeAccessor.get(store.getId()).toBlocking().firstOrDefault(null) != null;

    showStoreImage(store, context);
    showStoreData(store, theme, context);

    updateSubscribeButtonText(isStoreSubscribed);
    compositeSubscription.add(RxView.clicks(subscribeButton)
        .subscribe(handleSubscriptionLogic(new StoreWrapper(store, isStoreSubscribed))));

    setupSocialChannelButtons(store);
  }

  @Override public void unbindView() {

  }

  private void updateSubscribeButtonText(boolean isStoreSubscribed) {
    subscribeButton.setText(isStoreSubscribed ? itemView.getContext().getString(R.string.followed)
        : itemView.getContext().getString(R.string.appview_follow_store_button_text));
  }

  private Action1<Void> handleSubscriptionLogic(final StoreWrapper storeWrapper) {
    return aVoid -> {
      if (storeWrapper.isStoreSubscribed()) {
        storeWrapper.setStoreSubscribed(false);
        if (AptoideAccountManager.isLoggedIn()) {
          AptoideAccountManager.unsubscribeStore(storeWrapper.getStore().getName());
        }
        StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
        storeAccessor.remove(storeWrapper.getStore().getId());
        ShowMessage.asSnack(itemView,
            AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                storeWrapper.getStore().getName()));
      } else {
        storeWrapper.setStoreSubscribed(true);
        StoreUtilsProxy.subscribeStore(storeWrapper.getStore().getName(), subscribedStoreMeta -> {
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed,
                  subscribedStoreMeta.getData().getName()));
        }, err -> {
          CrashReports.logException(err);
        });
      }
      updateSubscribeButtonText(storeWrapper.isStoreSubscribed());
    };
  }

  private static class StoreWrapper {
    @Getter private final cm.aptoide.pt.model.v7.store.Store store;
    @Getter @Setter private boolean storeSubscribed;

    StoreWrapper(cm.aptoide.pt.model.v7.store.Store store, boolean isStoreSubscribed) {
      this.store = store;
      this.storeSubscribed = isStoreSubscribed;
    }
  }
}
