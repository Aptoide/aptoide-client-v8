package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

@Displayables({ AppViewStoreDisplayable.class }) public class AppViewStoreWidget
    extends Widget<AppViewStoreDisplayable> {

  private ImageView storeAvatarView;
  private TextView storeNameView;
  private TextView storeNumberUsersView;
  private Button followButton;
  private View storeLayout;
  private StoreRepository storeRepository;
  private AptoideAccountManager accountManager;

  public AppViewStoreWidget(View itemView) {
    super(itemView);
    storeRepository = RepositoryFactory.getStoreRepository();
  }

  @Override protected void assignViews(View itemView) {
    storeAvatarView = ((ImageView) itemView.findViewById(R.id.store_avatar));
    storeNameView = ((TextView) itemView.findViewById(R.id.store_name));
    storeNumberUsersView = ((TextView) itemView.findViewById(R.id.store_number_users));
    followButton = ((Button) itemView.findViewById(R.id.follow_store_btn));
    storeLayout = itemView.findViewById(R.id.store_layout);
  }

  @Override public void bindView(AppViewStoreDisplayable displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptor();

    GetApp getApp = displayable.getPojo();

    GetAppMeta.App app = getApp.getNodes().getMeta().getData();
    Store store = app.getStore();

    final FragmentActivity context = getContext();
    if (TextUtils.isEmpty(store.getAvatar())) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(R.drawable.ic_avatar_apps, storeAvatarView);
    } else {
      ImageLoader.with(context).loadUsingCircleTransform(store.getAvatar(), storeAvatarView);
    }

    StoreThemeEnum storeThemeEnum = StoreThemeEnum.get(store);

    storeNameView.setText(store.getName());
    storeNameView.setTextColor(storeThemeEnum.getStoreHeaderInt());

    storeNumberUsersView.setText(String.format(Locale.ENGLISH,
        V8Engine.getContext().getString(R.string.appview_followers_count_text),
        AptoideUtils.StringU.withSuffix(store.getStats().getSubscribers())));

    followButton.setBackgroundDrawable(storeThemeEnum.getButtonLayoutDrawable());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      followButton.setElevation(0);
    }

    final String storeName = store.getName();
    final String storeTheme = store.getAppearance().getTheme();

    final StoreUtilsProxy storeUtilsProxy =
        new StoreUtilsProxy(accountManager, baseBodyInterceptor, new StoreCredentialsProviderImpl(),
            AccessorFactory.getAccessorFor(cm.aptoide.pt.database.realm.Store.class));

    Action1<Void> openStore = __ -> {
      getNavigationManager().navigateTo(
          V8Engine.getFragmentProvider().newStoreFragment(storeName, storeTheme));
    };

    Action1<Void> subscribeStore = __ -> {
      storeUtilsProxy.subscribeStore(storeName, getStoreMeta -> {
        ShowMessage.asSnack(itemView,
            AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
      }, err -> {
        CrashReport.getInstance().log(err);
      }, accountManager);
    };

    followButton.setTextColor(storeThemeEnum.getStoreHeaderInt());
    compositeSubscription.add(RxView.clicks(storeLayout).subscribe(openStore));

    compositeSubscription.add(storeRepository.isSubscribed(store.getId())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isSubscribed -> {
          if (isSubscribed) {
            //int checkmarkDrawable = storeThemeEnum.getCheckmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(checkmarkDrawable, 0, 0, 0);
            followButton.setText(R.string.followed);
            compositeSubscription.add(RxView.clicks(followButton).subscribe(openStore));
          } else {
            //int plusMarkDrawable = storeThemeEnum.getPlusmarkDrawable();
            //followButton.setCompoundDrawablesWithIntrinsicBounds(plusMarkDrawable, 0, 0, 0);
            followButton.setText(R.string.follow);
            compositeSubscription.add(RxView.clicks(followButton).subscribe(subscribeStore));
          }
        }, throwable -> CrashReport.getInstance().log(throwable)));
  }
}
