package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.BaseBodyDecorator;
import cm.aptoide.pt.v8engine.activity.CreateStoreActivity;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreMetaDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import rx.functions.Action1;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaWidget extends MetaStoresBaseWidget<GridStoreMetaDisplayable> {

  private AptoideAccountManager accountManager;
  private View containerLayout;
  private View descriptionContentLayout;
  private LinearLayout socialChannelsLayout;
  private ImageView image;
  private TextView name;
  private TextView description;
  private Button subscribeButton;
  private Button editStoreButton;
  private TextView subscribersCount;
  private TextView appsCount;
  private TextView downloadsCount;
  private StoreUtilsProxy storeUtilsProxy;

  public GridStoreMetaWidget(View itemView) {
    super(itemView);
  }

  @NonNull @Override LinearLayout getSocialLayout() {
    return socialChannelsLayout;
  }

  @Override protected void assignViews(View itemView) {
    containerLayout = itemView.findViewById(R.id.outter_layout);
    descriptionContentLayout = itemView.findViewById(R.id.descriptionContent);
    socialChannelsLayout = (LinearLayout) itemView.findViewById(R.id.social_channels);
    image = (ImageView) itemView.findViewById(R.id.image);
    name = (TextView) itemView.findViewById(R.id.name);
    description = (TextView) itemView.findViewById(R.id.description);
    subscribeButton = (Button) itemView.findViewById(R.id.follow_btn);
    editStoreButton = (Button) itemView.findViewById(R.id.edit_store_btn);
    subscribersCount = (TextView) itemView.findViewById(R.id.subscribers);
    appsCount = (TextView) itemView.findViewById(R.id.apps);
    downloadsCount = (TextView) itemView.findViewById(R.id.downloads);
  }

  @Override public void bindView(GridStoreMetaDisplayable displayable) {

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final AptoideClientUUID aptoideClientUUID = new IdsRepositoryImpl
        (SecurePreferencesImplementation.getInstance(), getContext());
    storeUtilsProxy = new StoreUtilsProxy(accountManager,
        new BaseBodyDecorator(aptoideClientUUID.getUniqueIdentifier(), accountManager),
        new StoreCredentialsProviderImpl());
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
        .subscribe(handleSubscriptionLogic(new StoreWrapper(store, isStoreSubscribed)), err -> {
          CrashReport.getInstance().log(err);
        }));

    List<cm.aptoide.pt.model.v7.store.Store.SocialChannel> socialChannels =
        store.getSocialChannels();
    setupSocialLinks(displayable.getSocialLinks());

    // if there is no channels nor description, hide that area
    if (socialChannels == null || socialChannels.isEmpty()) {
      if (TextUtils.isEmpty(store.getAppearance().getDescription())) {
        descriptionContentLayout.setVisibility(View.GONE);
      }
      this.socialChannelsLayout.setVisibility(View.GONE);
    }

    final Account account = accountManager.getAccount();
    if (account != null && !TextUtils.isEmpty(account.getStore())) {
      if (account.getStore().equals(store.getName())) {
        descriptionContentLayout.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(store.getAppearance().getDescription())) {
          description.setText("Add a description to your store by editing it.");
        }
        editStoreButton.setVisibility(View.VISIBLE);
        compositeSubscription.add(RxView.clicks(editStoreButton)
            .subscribe(click -> editStore(store.getId(), store.getAppearance().getTheme(),
                store.getAppearance().getDescription(), store.getAvatar())));
        subscribeButton.setVisibility(View.GONE);
      }
    }
  }

  private void showStoreImage(cm.aptoide.pt.model.v7.store.Store store, Context context) {
    if (TextUtils.isEmpty(store.getAvatar())) {
      ImageLoader.with(context).loadUsingCircleTransform(R.drawable.ic_avatar_apps, image);
    } else {
      ImageLoader.with(context).loadUsingCircleTransform(store.getAvatar(), image);
    }
  }

  private void showStoreData(cm.aptoide.pt.model.v7.store.Store store, StoreThemeEnum theme,
      Context context) {

    @ColorInt int color = getColorOrDefault(theme, context);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      containerLayout.setBackground(d);
    } else {
      Drawable d = context.getResources().getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      containerLayout.setBackgroundDrawable(d);
    }
    subscribeButton.setTextColor(color);
    editStoreButton.setTextColor(color);

    name.setText(store.getName());
    String descriptionText = store.getAppearance().getDescription();
    if (TextUtils.isEmpty(descriptionText)) {
      description.setVisibility(View.GONE);
    } else {
      description.setText(descriptionText);
      description.setVisibility(View.VISIBLE);
    }
    appsCount.setText(
        NumberFormat.getNumberInstance(Locale.getDefault()).format(store.getStats().getApps()));
    downloadsCount.setText(AptoideUtils.StringU.withSuffix(store.getStats().getDownloads()));
    subscribersCount.setText(AptoideUtils.StringU.withSuffix(store.getStats().getSubscribers()));
  }

  private void updateSubscribeButtonText(boolean isStoreSubscribed) {
    subscribeButton.setText(isStoreSubscribed ? itemView.getContext().getString(R.string.followed)
        : itemView.getContext().getString(R.string.follow));
  }

  private Action1<Void> handleSubscriptionLogic(final StoreWrapper storeWrapper) {
    return aVoid -> {
      if (storeWrapper.isStoreSubscribed()) {
        storeWrapper.setStoreSubscribed(false);
        if (accountManager.isLoggedIn()) {
          accountManager.unsubscribeStore(storeWrapper.getStore().getName());
        }
        StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
        storeAccessor.remove(storeWrapper.getStore().getId());
        ShowMessage.asSnack(itemView,
            AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                storeWrapper.getStore().getName()));
      } else {
        storeWrapper.setStoreSubscribed(true);
        storeUtilsProxy.subscribeStore(storeWrapper.getStore().getName(), subscribedStoreMeta -> {
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed,
                  subscribedStoreMeta.getData().getName()));
        }, err -> {
          CrashReport.getInstance().log(err);
        }, accountManager);
      }
      updateSubscribeButtonText(storeWrapper.isStoreSubscribed());
    };
  }

  private void editStore(long storeId, String storeTheme, String storeDescription,
      String storeAvatar) {
    Intent intent = new Intent(getContext(), CreateStoreActivity.class);
    intent.putExtra("storeId", storeId);
    intent.putExtra("storeTheme", storeTheme);
    intent.putExtra("storeDescription", storeDescription);
    intent.putExtra("storeAvatar", storeAvatar);
    intent.putExtra("from", "store");
    getContext().startActivity(intent);
  }

  private int getColorOrDefault(StoreThemeEnum theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources().getColor(theme.getStoreHeader(), context.getTheme());
    } else {
      return context.getResources().getColor(theme.getStoreHeader());
    }
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
