package cm.aptoide.pt.v8engine.view.store;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.HomeUser;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreTheme;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by neuro on 04-08-2016.
 */
public class GridStoreMetaWidget extends MetaStoresBaseWidget<GridStoreMetaDisplayable> {

  private AptoideAccountManager accountManager;
  private View containerLayout;
  private LinearLayout socialChannelsLayout;
  private ImageView mainIcon;
  private TextView mainName;
  private TextView description;
  private Button followStoreButton;
  private Button editStoreButton;
  private TextView followersCountTv;
  private TextView appsCountTv;
  private TextView followingCountTv;
  private ImageView secondaryIcon;
  private TextView secondaryName;
  private View backgroundView;
  private StoreUtilsProxy storeUtilsProxy;

  public GridStoreMetaWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    containerLayout = itemView.findViewById(R.id.outter_layout);
    backgroundView = itemView.findViewById(R.id.background_view);
    socialChannelsLayout = (LinearLayout) itemView.findViewById(R.id.social_channels);
    mainIcon = (ImageView) itemView.findViewById(R.id.main_icon);
    mainName = (TextView) itemView.findViewById(R.id.main_name);
    description = (TextView) itemView.findViewById(R.id.description);
    followStoreButton = (Button) itemView.findViewById(R.id.follow_btn);
    editStoreButton = (Button) itemView.findViewById(R.id.edit_store_btn);
    followersCountTv = (TextView) itemView.findViewById(R.id.number_of_followers);
    followingCountTv = (TextView) itemView.findViewById(R.id.number_of_following);
    appsCountTv = (TextView) itemView.findViewById(R.id.number_of_apps);
    secondaryName = (TextView) itemView.findViewById(R.id.secondary_name);
    secondaryIcon = ((ImageView) itemView.findViewById(R.id.secondary_icon));
  }

  @Override public void bindView(GridStoreMetaDisplayable displayable) {

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    storeUtilsProxy = new StoreUtilsProxy(accountManager, bodyInterceptor,
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
            ((V8Engine) getContext().getApplicationContext()
                .getApplicationContext()).getDatabase(), Store.class)),
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), httpClient,
        WebService.getDefaultConverter(),
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    final GetHomeMeta getHomeMeta = displayable.getPojo();
    final cm.aptoide.pt.dataprovider.model.v7.store.Store store = getHomeMeta.getData()
        .getStore();
    HomeUser user = getHomeMeta.getData()
        .getUser();

    if (store != null) {
      final StoreTheme theme = StoreTheme.get(store.getAppearance() == null ? "default"
          : store.getAppearance()
              .getTheme());
      final Context context = itemView.getContext();
      StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(
          ((V8Engine) getContext().getApplicationContext()
              .getApplicationContext()).getDatabase(), Store.class);
      boolean isStoreSubscribed = storeAccessor.get(store.getId())
          .toBlocking()
          .firstOrDefault(null) != null;

      setupMainInfo(store.getName(), theme, context, store.getStats()
          .getApps(), getHomeMeta.getData()
          .getStats()
          .getFollowers(), getHomeMeta.getData()
          .getStats()
          .getFollowing(), true, store.getAvatar(), R.drawable.ic_avatar_apps, R.drawable.ic_store);

      updateSubscribeButtonText(isStoreSubscribed);
      followStoreButton.setVisibility(View.VISIBLE);
      compositeSubscription.add(RxView.clicks(followStoreButton)
          .subscribe(
              handleSubscriptionLogic(new StoreWrapper(store, isStoreSubscribed), displayable),
              err -> {
                CrashReport.getInstance()
                    .log(err);
              }));

      List<cm.aptoide.pt.dataprovider.model.v7.store.Store.SocialChannel> socialChannels =
          displayable.getSocialLinks();
      setupSocialLinks(displayable.getSocialLinks(), socialChannelsLayout);

      // if there is no channels or description, hide that area
      if ((socialChannels == null || socialChannels.isEmpty()) && TextUtils.isEmpty(
          store.getAppearance()
              .getDescription())) {
        setDescriptionSectionVisibility(false);
      } else {
        backgroundView.setVisibility(View.VISIBLE);
        //show social channels if there are any
        if (socialChannels != null && !socialChannels.isEmpty()) {
          socialChannelsLayout.setVisibility(View.VISIBLE);
        } else {
          socialChannelsLayout.setVisibility(View.GONE);
        }
        //show description if exists
        if (!TextUtils.isEmpty(store.getAppearance()
            .getDescription())) {
          description.setVisibility(View.VISIBLE);
          description.setText(store.getAppearance()
              .getDescription());
        } else {
          description.setVisibility(View.GONE);
        }
      }

      //check if the user is the store's owner
      compositeSubscription.add(accountManager.accountStatus()
          .first()
          .toSingle()
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSuccess(account -> {
            if (!TextUtils.isEmpty(store.getName()) && account.isLoggedIn() && store.getName()
                .equals(account.getStore()
                    .getName())) {
              description.setVisibility(View.VISIBLE);
              backgroundView.setVisibility(View.VISIBLE);
              if (TextUtils.isEmpty(store.getAppearance()
                  .getDescription())) {
                description.setText("Add a description to your store by editing it.");
              }
              editStoreButton.setVisibility(View.VISIBLE);
              compositeSubscription.add(RxView.clicks(editStoreButton)
                  .subscribe(click -> editStore(store.getId(), store.getAppearance()
                      .getTheme(), store.getAppearance()
                      .getDescription(), store.getName(), store.getAvatar())));
            } else {
              editStoreButton.setVisibility(View.GONE);
            }
          })
          .subscribe());

      if (user != null) {
        setSecondaryInfoVisibility(true);
        setupSecondaryInfo(context, user.getName(), user.getAvatar());
      } else {
        setSecondaryInfoVisibility(false);
      }
    } else {
      followStoreButton.setVisibility(View.INVISIBLE);
      setupMainInfo(user.getName(), StoreTheme.get("default"), getContext(), getHomeMeta.getData()
              .getStats()
              .getFollowers(), getHomeMeta.getData()
              .getStats()
              .getFollowing(), user.getAvatar(), R.drawable.user_default,
          R.drawable.user_shape_mini_icon);
      setSecondaryInfoVisibility(false);
      setDescriptionSectionVisibility(false);
    }
  }

  private void setDescriptionSectionVisibility(boolean isVisible) {
    int visibility = isVisible ? View.VISIBLE : View.GONE;
    backgroundView.setVisibility(visibility);
    description.setVisibility(visibility);
    socialChannelsLayout.setVisibility(visibility);
    editStoreButton.setVisibility(visibility);
  }

  private void showMainIcon(Context context, String mainIconUrl, int defaultMainIcon) {
    if (TextUtils.isEmpty(mainIconUrl)) {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(defaultMainIcon, mainIcon);
    } else {
      ImageLoader.with(context)
          .loadWithShadowCircleTransform(mainIconUrl, mainIcon);
    }
  }

  /**
   * @param appsVisibility true if number of apps should be displayed, false otherwise
   */
  private void setupMainInfo(String name, StoreTheme theme, Context context, long appsCount,
      long followersCount, long followingCount, boolean appsVisibility, String mainIconUrl,
      @DrawableRes int defaultMainIcon, @DrawableRes int mainNameDrawable) {

    setupTheme(theme, context);

    mainName.setText(name);
    Drawable drawable;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable = getContext().getDrawable(mainNameDrawable);
    } else {
      drawable = getContext().getResources()
          .getDrawable(mainNameDrawable);
    }
    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    mainName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    if (appsVisibility) {
      appsCountTv.setText(NumberFormat.getNumberInstance(Locale.getDefault())
          .format(appsCount));
      appsCountTv.setVisibility(View.VISIBLE);
    } else {
      appsCountTv.setVisibility(View.INVISIBLE);
    }
    followersCountTv.setText(AptoideUtils.StringU.withSuffix(followersCount));
    followingCountTv.setText(AptoideUtils.StringU.withSuffix(followingCount));

    showMainIcon(getContext(), mainIconUrl, defaultMainIcon);
  }

  private void updateSubscribeButtonText(boolean isStoreSubscribed) {
    followStoreButton.setText(isStoreSubscribed ? itemView.getContext()
        .getString(R.string.followed) : itemView.getContext()
        .getString(R.string.follow));
  }

  private Action1<Void> handleSubscriptionLogic(final StoreWrapper storeWrapper,
      GridStoreMetaDisplayable displayable) {
    return aVoid -> {
      if (storeWrapper.isStoreSubscribed()) {
        storeWrapper.setStoreSubscribed(false);
        if (accountManager.isLoggedIn()) {
          accountManager.unsubscribeStore(displayable.getStoreName(),
              displayable.getStoreUserName(), displayable.getStorePassword());
        }
        StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(
            ((V8Engine) getContext().getApplicationContext()
                .getApplicationContext()).getDatabase(), Store.class);
        storeAccessor.remove(storeWrapper.getStore()
            .getId());
        ShowMessage.asSnack(itemView,
            AptoideUtils.StringU.getFormattedString(R.string.unfollowing_store_message,
                getContext().getResources(), storeWrapper.getStore()
                    .getName()));
      } else {
        storeWrapper.setStoreSubscribed(true);
        storeUtilsProxy.subscribeStore(storeWrapper.getStore()
            .getName(), subscribedStoreMeta -> {
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed,
                  getContext().getResources(), subscribedStoreMeta.getData()
                      .getName()));
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        }, accountManager);
      }
      updateSubscribeButtonText(storeWrapper.isStoreSubscribed());
    };
  }

  private void editStore(long storeId, String storeThemeName, String storeDescription,
      String storeName, String storeImagePath) {
    ManageStoreFragment.ViewModel viewModel =
        new ManageStoreFragment.ViewModel(storeId, StoreTheme.fromName(storeThemeName), storeName,
            storeDescription, storeImagePath);
    getFragmentNavigator().navigateTo(ManageStoreFragment.newInstance(viewModel, false));
  }

  private void setSecondaryInfoVisibility(boolean userVisibility) {
    secondaryIcon.setVisibility(userVisibility ? View.VISIBLE : View.GONE);
    secondaryName.setVisibility(userVisibility ? View.VISIBLE : View.GONE);
  }

  private void setupSecondaryInfo(Context context, String name, String secondaryIconUrl) {
    secondaryName.setText(name);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(secondaryIconUrl, secondaryIcon);
  }

  private void setupMainInfo(String name, StoreTheme theme, Context context, long followersCount,
      long followingCount, String mainIconUrl, @DrawableRes int defaultMainIcon,
      @DrawableRes int mainNameDrawble) {
    setupMainInfo(name, theme, context, 0, followersCount, followingCount, false, mainIconUrl,
        defaultMainIcon, mainNameDrawble);
  }

  private void setupTheme(StoreTheme theme, Context context) {
    @ColorInt int color = getColorOrDefault(theme, context);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      containerLayout.setBackground(d);
    } else {
      Drawable d = context.getResources()
          .getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      containerLayout.setBackgroundDrawable(d);
    }
    followStoreButton.setTextColor(color);
    editStoreButton.setTextColor(color);
  }

  private int getColorOrDefault(StoreTheme theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(theme.getPrimaryColor(), context.getTheme());
    } else {
      return context.getResources()
          .getColor(theme.getPrimaryColor());
    }
  }

  private static class StoreWrapper {
    @Getter private final cm.aptoide.pt.dataprovider.model.v7.store.Store store;
    @Getter @Setter private boolean storeSubscribed;

    StoreWrapper(cm.aptoide.pt.dataprovider.model.v7.store.Store store, boolean isStoreSubscribed) {
      this.store = store;
      this.storeSubscribed = isStoreSubscribed;
    }
  }
}
