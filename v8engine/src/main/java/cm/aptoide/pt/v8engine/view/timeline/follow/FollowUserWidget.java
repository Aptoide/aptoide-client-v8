package cm.aptoide.pt.v8engine.view.timeline.follow;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.timeline.displayable.FollowUserDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import okhttp3.OkHttpClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by trinkes on 16/12/2016.
 */

public class FollowUserWidget extends Widget<FollowUserDisplayable> {

  private TextView userNameTv;
  private TextView storeNameTv;
  private TextView followingNumber;
  private TextView followersNumber;
  private ImageView mainIcon;
  private ImageView secondaryIcon;
  private TextView followingTv;
  private TextView followedTv;
  private Button follow;
  private LinearLayout followNumbers;
  private LinearLayout followLayout;
  private View separatorView;
  private AptoideAccountManager accountManager;

  public FollowUserWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userNameTv = (TextView) itemView.findViewById(R.id.user_name);
    storeNameTv = (TextView) itemView.findViewById(R.id.store_name);
    followingNumber = (TextView) itemView.findViewById(R.id.following_number);
    followersNumber = (TextView) itemView.findViewById(R.id.followers_number);
    followingTv = (TextView) itemView.findViewById(R.id.following_tv);
    followedTv = (TextView) itemView.findViewById(R.id.followers_tv);
    mainIcon = (ImageView) itemView.findViewById(R.id.main_icon);
    secondaryIcon = (ImageView) itemView.findViewById(R.id.secondary_icon);
    follow = (Button) itemView.findViewById(R.id.follow_btn);
    followNumbers = (LinearLayout) itemView.findViewById(R.id.followers_following_numbers);
    followLayout = (LinearLayout) itemView.findViewById(R.id.follow_store_layout);
    separatorView = itemView.findViewById(R.id.separator_vertical);
  }

  @Override public void bindView(FollowUserDisplayable displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    final BodyInterceptor<BaseBody> bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();

    if (!displayable.isLike()) {
      followLayout.setVisibility(View.GONE);
      followNumbers.setVisibility(View.VISIBLE);
      separatorView.setVisibility(View.VISIBLE);
      followingNumber.setText(displayable.getFollowing());
      followersNumber.setText(displayable.getFollowers());
    } else {
      followNumbers.setVisibility(View.GONE);
      separatorView.setVisibility(View.INVISIBLE);
      if (displayable.hasStore()) {
        followLayout.setVisibility(View.VISIBLE);
        setFollowColor(displayable);
      }

      final String storeName = displayable.getStoreName();
      final String storeTheme = V8Engine.getConfiguration()
          .getDefaultTheme();

      final StoreUtilsProxy storeUtilsProxy =
          new StoreUtilsProxy(accountManager, bodyInterceptor, new StoreCredentialsProviderImpl(),
              AccessorFactory.getAccessorFor(Store.class), httpClient,
              WebService.getDefaultConverter());

      Action1<Void> openStore = __ -> {
        getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
            .newStoreFragment(storeName, storeTheme));
      };

      Action1<Void> subscribeStore = __ -> {
        storeUtilsProxy.subscribeStore(storeName, getStoreMeta -> {
          ShowMessage.asSnack(itemView,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        }, accountManager);
      };

      StoreRepository storeRepository = RepositoryFactory.getStoreRepository();
      compositeSubscription.add(storeRepository.isSubscribed(displayable.getStoreName())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(isSubscribed -> {
            if (isSubscribed) {
              follow.setText(R.string.followed);
              compositeSubscription.add(RxView.clicks(follow)
                  .subscribe(openStore));
            } else {
              follow.setText(R.string.follow);
              compositeSubscription.add(RxView.clicks(follow)
                  .subscribe(subscribeStore));
            }
          }, (throwable) -> {
            throwable.printStackTrace();
          }));
    }

    final FragmentActivity context = getContext();
    if (displayable.hasStoreAndUser()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getStoreAvatar(), mainIcon);
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getUserAvatar(), secondaryIcon);
      mainIcon.setVisibility(View.VISIBLE);
      secondaryIcon.setVisibility(View.VISIBLE);
    } else if (displayable.hasUser()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getUserAvatar(), mainIcon);
      secondaryIcon.setVisibility(View.GONE);
    } else if (displayable.hasStore()) {
      ImageLoader.with(context)
          .loadUsingCircleTransform(displayable.getStoreAvatar(), mainIcon);
      secondaryIcon.setVisibility(View.GONE);
    } else {
      mainIcon.setVisibility(View.GONE);
      secondaryIcon.setVisibility(View.GONE);
    }

    if (displayable.hasUser()) {
      this.userNameTv.setText(displayable.getUserName());
      userNameTv.setVisibility(View.VISIBLE);
    } else {
      userNameTv.setVisibility(View.GONE);
    }

    if (displayable.hasStore()) {
      setupStoreNameTv(displayable.getStoreColor(), displayable.storeName());
    } else {
      storeNameTv.setVisibility(View.GONE);
    }
    followedTv.setTextColor(displayable.getStoreColor());
    followingTv.setTextColor(displayable.getStoreColor());

    compositeSubscription.add(RxView.clicks(itemView)
        .subscribe(click -> displayable.viewClicked(getFragmentNavigator()), err -> {
          CrashReport.getInstance()
              .log(err);
        }));
  }

  private void setFollowColor(FollowUserDisplayable displayable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      follow.setBackground(displayable.getButtonBackgroundStoreThemeColor());
    } else {
      follow.setBackgroundDrawable(displayable.getButtonBackgroundStoreThemeColor());
    }
    follow.setTextColor(displayable.getStoreColor());
  }

  private void setupStoreNameTv(int storeColor, String storeName) {
    storeNameTv.setText(storeName);
    storeNameTv.setTextColor(storeColor);
    storeNameTv.setVisibility(View.VISIBLE);
    Drawable drawable;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      drawable = getContext().getResources()
          .getDrawable(R.drawable.ic_store, null);
    } else {
      drawable = getContext().getResources()
          .getDrawable(R.drawable.ic_store);
    }
    drawable.setBounds(0, 0, 30, 30);
    drawable.mutate();

    drawable.setColorFilter(storeColor, PorterDuff.Mode.SRC_IN);
    storeNameTv.setCompoundDrawablePadding(5);
    storeNameTv.setCompoundDrawables(drawable, null, null, null);
  }
}
