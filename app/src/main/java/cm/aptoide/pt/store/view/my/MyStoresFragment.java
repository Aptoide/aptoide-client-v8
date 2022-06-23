package cm.aptoide.pt.store.view.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.view.GridStoreDisplayable;
import cm.aptoide.pt.store.view.StoreTabWidgetsGridRecyclerFragment;
import cm.aptoide.pt.timeline.view.displayable.FollowStoreDisplayable;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 13/12/2016.
 */

public class MyStoresFragment extends StoreTabWidgetsGridRecyclerFragment implements MyStoresView {

  private static final String TAG = MyStoresFragment.class.getSimpleName();
  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.STORES;
  @Inject MyStoresPresenter myStoresPresenter;
  @Inject RoomStoreRepository storeRepository;
  private ImageView userAvatar;
  private BottomNavigationActivity bottomNavigationActivity;

  public static MyStoresFragment newInstance(Event event, String storeTheme, String tag,
      StoreContext storeContext) {
    // TODO: 28-12-2016 neuro ia saltando um preguito com este null lolz
    Bundle args =
        buildBundle(event, HomeEvent.Type.NO_OP, null, storeTheme, tag, storeContext, false);
    MyStoresFragment fragment = new MyStoresFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public static Fragment newInstance() {
    return new MyStoresFragment();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onDestroyView() {
    userAvatar = null;
    super.onDestroyView();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    registerForViewChanges();
    if (getView() != null) {
      userAvatar = getView().findViewById(R.id.user_actionbar_icon);
    }

    // SMART related changes for displaying custom store
//    attachPresenter(myStoresPresenter);

    getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
            .newStoreFragment(SMARTStore.getStoreName(view.getContext()), SMARTStore.STORE_COLOR), true);
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url,
      boolean bypassServerCache) {
    return requestFactoryCdnPool.newStoreWidgets(url)
        .observe(refresh, bypassServerCache)
        .observeOn(Schedulers.io())
        .flatMap(this::parseDisplayables)
        .map(this::addFollowStoreDisplayable);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.my_stores, container, false);
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return false;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(null);
    toolbar.setLogo(null);
  }

  private List<Displayable> addFollowStoreDisplayable(List<Displayable> displayables) {
    int groupPosition = 0;
    int gridStoreDisplayablePosition = 0;
    for (int i = 0; i < displayables.size(); i++) {
      if (displayables.get(i) instanceof DisplayableGroup) {
        groupPosition = i;
        break;
      }
    }
    DisplayableGroup displayableGroup = (DisplayableGroup) displayables.get(groupPosition);
    List<Displayable> displayableList = displayableGroup.getChildren();
    for (int i = 0; i < displayableList.size(); i++) {
      if (displayableList.get(i) instanceof GridStoreDisplayable) {
        gridStoreDisplayablePosition = i;
        break;
      }
    }
    ((DisplayableGroup) displayables.get(groupPosition)).getChildren()
        .add(gridStoreDisplayablePosition, new FollowStoreDisplayable());
    if (displayableList.size() > 6) {
      ((DisplayableGroup) displayables.get(groupPosition)).getChildren()
          .remove(displayableList.size() - 1);
    }
    return displayables;
  }

  private void registerForViewChanges() {
    AptoideAccountManager accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();

    Observable<Account> loginObservable = accountManager.accountStatus()
        .doOnNext(__ -> reloadData());

    Observable<List<RoomStore>> storesObservable = storeRepository.getAll()
        .skip(1)
        .doOnNext(__ -> {
          Logger.getInstance()
              .d(TAG, "Store database changed, reloading...");
          reloadData();
        });

    //
    // until this fragment is destroyed we listen for DB changes and login state changes
    // to reload the stores that we are showing
    //
    Observable.merge(loginObservable, storesObservable)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  private void reloadData() {
    //
    // this call "magically" gets the store from db (again) and does a WS call to get the
    // most recent subscribed stores, updating the DB (redundancy?) and showing the
    // remote stores
    //
    super.load(false, true, null);
  }

  @Override @UiThread public void scrollToTop() {
    RecyclerView view = getRecyclerView();
    LinearLayoutManager layoutManager = ((LinearLayoutManager) view.getLayoutManager());
    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
    if (lastVisibleItemPosition > 10) {
      view.scrollToPosition(10);
    }
    view.smoothScrollToPosition(0);
  }

  @Override public void setUserImage(String userAvatarUrl) {
    if (userAvatar != null) {
      ImageLoader.with(getContext())
          .loadWithShadowCircleTransformWithPlaceholder(userAvatarUrl, userAvatar,
              R.drawable.ic_account_circle);
    }
  }

  @Override public Observable<Void> imageClick() {
    return RxView.clicks(userAvatar);
  }

  @Override public void showAvatar() {
    if (userAvatar != null) userAvatar.setVisibility(View.VISIBLE);
  }

  @Override public void setDefaultUserImage() {
    ImageLoader.with(getContext())
        .loadUsingCircleTransform(R.drawable.ic_account_circle, userAvatar);
  }
}
