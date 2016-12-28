/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RecommendedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 29-04-2016.
 */
public abstract class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

  protected StoreRepository storeRepository;

  protected Event.Type type;
  protected Event.Name name;
  protected Layout layout;
  protected String action;
  protected String title;
  protected String tag;
  protected String storeTheme;

  public static StoreTabGridRecyclerFragment newInstance(Event event, String title,
      String storeTheme, String tag) {
    Bundle args = buildBundle(event, title, storeTheme, tag);
    StoreTabGridRecyclerFragment fragment = createFragment(event.getName());
    fragment.setArguments(args);
    return fragment;
  }

  public static StoreTabGridRecyclerFragment newInstance(Event event, String storeTheme,
      String tag) {
    return newInstance(event, null, storeTheme, tag);
  }

  @NonNull
  protected static Bundle buildBundle(Event event, String title, String storeTheme, String tag) {
    Bundle args = new Bundle();

    if (event.getType() != null) {
      args.putString(BundleCons.TYPE, event.getType().toString());
    }
    if (event.getName() != null) {
      args.putString(BundleCons.NAME, event.getName().toString());
    }
    if (event.getData() != null && event.getData().getLayout() != null) {
      args.putString(BundleCons.LAYOUT, event.getData().getLayout().toString());
    }
    args.putString(BundleCons.TITLE, title);
    args.putString(BundleCons.ACTION, event.getAction());
    args.putString(BundleCons.STORE_THEME, storeTheme);
    args.putString(BundleCons.TAG, tag);
    return args;
  }

  private static StoreTabGridRecyclerFragment createFragment(Event.Name name) {
    // TODO: 28-12-2016 neuro newInstance needed, reflection even more..
    switch (name) {
      case listApps:
        return new ListAppsFragment();
      case getStore:
        return new GetStoreFragment();
      case getStoresRecommended:
      case getMyStoresSubscribed:
        return new MyStoresSubscribedFragment();
      case myStores:
      case getStoreWidgets:
        return new GetStoreWidgetsFragment();
      case listReviews:
        return new ListReviewsFragment();
      case getAds:
        return new GetAdsFragment();
      case listStores:
        return new ListStoresFragment();
      default:
        throw new RuntimeException("Fragment " + name + " not implemented!");
    }
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case myStores:
        case getMyStoresSubscribed:
        case getStoresRecommended:
        case listApps:
        case getStore:
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    storeRepository = RepositoryFactory.getStoreRepository();

    super.onCreate(savedInstanceState);
  }

  @Override public void loadExtras(Bundle args) {
    if (args.containsKey(BundleCons.TYPE)) {
      type = Event.Type.valueOf(args.getString(BundleCons.TYPE));
    }
    if (args.containsKey(BundleCons.NAME)) {
      name = Event.Name.valueOf(args.getString(BundleCons.NAME));
    }
    if (args.containsKey(BundleCons.LAYOUT)) {
      layout = Layout.valueOf(args.getString(BundleCons.LAYOUT));
    }
    if (args.containsKey(BundleCons.TAG)) {
      tag = args.getString(BundleCons.TAG);
    }
    title = args.getString(Translator.translate(BundleCons.TITLE));
    action = args.getString(BundleCons.ACTION);
    storeTheme = args.getString(BundleCons.STORE_THEME);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create || refresh) {
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      if (!validateAcceptedName(name)) {
        throw new RuntimeException(
            "Invalid name(" + name + ") for event on " + getClass().getSimpleName() + "!");
      }

      // TODO: 28-12-2016 neuro martelo martelo martelo
      switch (name) {
        case getStoresRecommended:
        case getMyStoresSubscribed:
          caseMyStores(url, refresh);
          break;
        case myStores:
        case getStoreWidgets:
          caseGetStoreWidgets(url, StoreUtils.getStoreCredentialsFromUrl(url), refresh);
          break;
        default:
          Observable<List<? extends Displayable>> displayablesObservable =
              buildDisplayables(refresh, url);
          if (displayablesObservable != null) {
            displayablesObservable.compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
                .subscribe(this::setDisplayables);
          }
      }
    }
  }

  /**
   * @deprecated á espera da ajuda do trinkies :/
   */
  @Deprecated private Subscription caseGetStoreWidgets(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentials,
        AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .observeOn(Schedulers.io())
        .subscribe(getStoreWidgets -> {

          // Load sub nodes
          List<GetStoreWidgets.WSWidget> list = getStoreWidgets.getDatalist().getList();
          CountDownLatch countDownLatch = new CountDownLatch(list.size());

          String aptoideClientUuid =
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID();

          Observable.from(list)
              .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
                  wsWidget.getView() != null ? StoreUtils.getStoreCredentialsFromUrl(
                      wsWidget.getView()) : new BaseRequestWithStore.StoreCredentials(),
                  countDownLatch, refresh, throwable -> finishLoading(throwable),
                  AptoideAccountManager.getAccessToken(), aptoideClientUuid,
                  DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(
                      V8Engine.getContext()), DataProvider.getConfiguration().getPartnerId(),
                  AptoideAccountManager.isMatureSwitchOn()));

          try {
            countDownLatch.await(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          List<Displayable> displayables = DisplayablesFactory.parse(getStoreWidgets, storeTheme,
              RepositoryFactory.getStoreRepository());
          setDisplayables(displayables);
        }, throwable -> finishLoading(throwable));
  }

  /**
   * @deprecated á espera da ajuda do trinkies :/
   */
  @Deprecated private void caseMyStores(String url, boolean refresh) {
    StoreRepository storeRepository = RepositoryFactory.getStoreRepository();
    GetMyStoreListRequest request =
        GetMyStoreListRequest.of(url, AptoideAccountManager.getAccessToken(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID(), true);

    Action1<ListStores> listStoresAction =
        listStores -> addDisplayables(getStoresDisplayable(listStores.getDatalist().getList()));

    ErrorRequestListener errorListener = (throwable) -> {
      recyclerView.clearOnScrollListeners();
      LinkedList<String> errorsList = new LinkedList<>();
      errorsList.add(WSWidgetsUtils.USER_NOT_LOGGED_ERROR);
      if (WSWidgetsUtils.shouldAddObjectView(errorsList, throwable)) {
        DisplayablesFactory.loadLocalSubscribedStores(storeRepository)
            .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
            .subscribe(stores -> addDisplayables(getStoresDisplayable(stores)));
      } else {
        finishLoading(throwable);
      }
    };

    recyclerView.clearOnScrollListeners();
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), request, listStoresAction,
            errorListener, refresh);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  @NonNull private ArrayList<Displayable> getStoresDisplayable(List<Store> list) {
    ArrayList<Displayable> storesDisplayables = new ArrayList<>(list.size());
    Collections.sort(list, (store, t1) -> store.getName().compareTo(t1.getName()));
    for (int i = 0; i < list.size(); i++) {
      if (i == 0 || list.get(i - 1).getId() != list.get(i).getId()) {
        if (layout == Layout.LIST) {
          storesDisplayables.add(new RecommendedStoreDisplayable(list.get(i), storeRepository));
        } else {
          storesDisplayables.add(new GridStoreDisplayable(list.get(i)));
        }
      }
    }
    return storesDisplayables;
  }

  @Override public int getContentViewId() {
    // title flag whether toolbar should be shown or not
    if (title != null) {
      return R.layout.recycler_swipe_fragment_with_toolbar;
    } else {
      return super.getContentViewId();
    }
  }

  @Nullable
  protected abstract Observable<List<? extends Displayable>> buildDisplayables(boolean refresh,
      String url);

  private static class BundleCons {

    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String ACTION = "action";
    public static final String STORE_THEME = "storeTheme";
    public static final String LAYOUT = "layout";
    public static final String TAG = "tag";
  }
}
