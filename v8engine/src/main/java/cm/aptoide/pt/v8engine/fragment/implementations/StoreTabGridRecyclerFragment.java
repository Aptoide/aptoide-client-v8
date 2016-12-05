/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.ListFullReviewsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AdultRowDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAdDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.RowReviewDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import java.util.ArrayList;
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
public class StoreTabGridRecyclerFragment extends GridRecyclerSwipeFragment {

  protected Event.Type type;
  protected Event.Name name;
  protected Layout layout;
  protected String action;
  protected String title;
  protected String tag;
  protected String storeTheme;
  private List<Displayable> displayables;
  private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

  public static StoreTabGridRecyclerFragment newInstance(Event event, String title,
      String storeTheme, String tag) {
    Bundle args = buildBundle(event, title, storeTheme, tag);
    StoreTabGridRecyclerFragment fragment = new StoreTabGridRecyclerFragment();
    fragment.setArguments(args);
    return fragment;
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

  @NonNull protected static Bundle buildBundle(Event event, String title) {
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
    return args;
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case listApps:
        case getStore:
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listStoreComments:
        case listReviews:
          return true;
      }
    }

    return false;
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

  private void caseListStores(String url, boolean refresh) {
    ListStoresRequest listStoresRequest =
        ListStoresRequest.ofAction(url, AptoideAccountManager.getAccessToken(),
            AptoideAccountManager.getUserEmail(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID());
    Action1<ListStores> listStoresAction = listStores -> {

      // Load sub nodes
      List<Store> list = listStores.getDatalist().getList();

      displayables = new LinkedList<>();
      for (Store store : list) {
        displayables.add(new GridStoreDisplayable(store));
      }

      addDisplayables(displayables);
    };

    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listStoresRequest, listStoresAction,
            errorRequestListener);
    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  private void caseGetAds(boolean refresh) {
    GetAdsRequest.ofHomepageMore(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID(),
        DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(V8Engine.getContext()),
        DataProvider.getConfiguration().getPartnerId()).execute(getAdsResponse -> {
      List<GetAdsResponse.Ad> list = getAdsResponse.getAds();

      displayables = new LinkedList<>();
      for (GetAdsResponse.Ad ad : list) {
        displayables.add(new GridAdDisplayable(ad, tag));
      }

      addDisplayables(displayables);
    }, e -> finishLoading());

    //Highlighted should have pull to refresh
    //getView().findViewById(R.id.swipe_container).setEnabled(false);
  }

  private void caseListApps(String url, BaseRequestWithStore.StoreCredentials storeCredentials,
      boolean refresh) {
    ListAppsRequest listAppsRequest =
        ListAppsRequest.ofAction(url, storeCredentials, AptoideAccountManager.getAccessToken(),
            AptoideAccountManager.getUserEmail(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID());
    Action1<ListApps> listAppsAction = listApps -> {

      // Load sub nodes
      List<App> list = listApps.getDatalist().getList();

      displayables = new LinkedList<>();
      if (layout != null) {
        switch (layout) {
          case GRAPHIC:
            for (App app : list) {
              app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(new AppBrickListDisplayable(app, tag));
            }
            break;
          default:
            for (App app : list) {
              app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(new GridAppDisplayable(app, tag));
            }
            break;
        }
      } else {
        for (App app : list) {
          app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
          displayables.add(new GridAppDisplayable(app, tag));
        }
      }

      addDisplayables(displayables);
    };

    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listAppsRequest, listAppsAction,
            errorRequestListener);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  private Subscription caseGetStore(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh) {
    return GetStoreRequest.ofAction(url, storeCredentials, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .observeOn(Schedulers.io())
        .subscribe(getStore -> {

          // Load sub nodes
          List<GetStoreWidgets.WSWidget> list =
              getStore.getNodes().getWidgets().getDatalist().getList();
          CountDownLatch countDownLatch = new CountDownLatch(list.size());

          Observable.from(list)
              .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
                  wsWidget.getView() != null ? StoreUtils.getStoreCredentialsFromUrl(
                      wsWidget.getView()) : new BaseRequestWithStore.StoreCredentials(),
                  countDownLatch, refresh, throwable -> countDownLatch.countDown(),
                  AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
                  new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                      DataProvider.getContext()).getAptoideClientUUID(),
                  DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(
                      V8Engine.getContext()), DataProvider.getConfiguration().getPartnerId(),
                  !TextUtils.isEmpty(AptoideAccountManager.getUserData().getUserRepo())));

          try {
            countDownLatch.await(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          displayables = DisplayablesFactory.parse(getStore.getNodes().getWidgets(), storeTheme);

          // We only want Adult Switch in Home Fragment.
          if (getParentFragment() != null && getParentFragment() instanceof HomeFragment) {
            displayables.add(new AdultRowDisplayable());
          }
          setDisplayables(displayables);
        }, throwable -> finishLoading(throwable));
  }

  private Subscription caseGetStoreWidgets(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh) {
    return GetStoreWidgetsRequest.ofAction(url, storeCredentials,
        AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .observeOn(Schedulers.io())
        .subscribe(getStoreWidgets -> {

          // Load sub nodes
          List<GetStoreWidgets.WSWidget> list = getStoreWidgets.getDatalist().getList();
          CountDownLatch countDownLatch = new CountDownLatch(list.size());

          Observable.from(list)
              .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
                  wsWidget.getView() != null ? StoreUtils.getStoreCredentialsFromUrl(
                      wsWidget.getView()) : new BaseRequestWithStore.StoreCredentials(),
                  countDownLatch, refresh, throwable -> finishLoading(throwable),
                  AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
                  new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                      DataProvider.getContext()).getAptoideClientUUID(),
                  DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(
                      V8Engine.getContext()), DataProvider.getConfiguration().getPartnerId(),
                  !TextUtils.isEmpty(AptoideAccountManager.getUserData().getUserRepo())));

          try {
            countDownLatch.await();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          displayables = DisplayablesFactory.parse(getStoreWidgets, storeTheme);
          setDisplayables(displayables);
        }, throwable -> finishLoading(throwable));
  }

  @Override public void setupToolbar() {

  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
    if (create) {
      String url = action != null ? action.replace(V7.BASE_HOST, "") : null;

      if (!validateAcceptedName(name)) {
        throw new RuntimeException(
            "Invalid name(" + name + ") for event on " + getClass().getSimpleName() + "!");
      }

      switch (name) {
        case listApps:
          caseListApps(url, StoreUtils.getStoreCredentialsFromUrl(url), refresh);
          break;
        case getStore:
          caseGetStore(url, StoreUtils.getStoreCredentialsFromUrl(url), refresh);
          break;
        case getStoreWidgets:
          caseGetStoreWidgets(url, StoreUtils.getStoreCredentialsFromUrl(url), refresh);
          break;
        case listStoreComments:
          caseListStoreComments(url, StoreUtils.getStoreCredentialsFromUrl(url), refresh);
          break;
        case listReviews:
          caseListReviews(url, refresh);
          break;
        //	case getApkComments:
        //		break;
        case getAds:
          caseGetAds(refresh);
          break;
        case listStores:
          caseListStores(url, refresh);
          break;
      }
    } else {
      // Not all requests are endless so..
      if (endlessRecyclerOnScrollListener != null) {
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
      }
      //setDisplayables(displayables);
    }
  }

  void caseListStoreComments(String url, BaseRequestWithStore.StoreCredentials storeCredentials,
      boolean refresh) {
    String aptoideClientUuid = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext()).getAptoideClientUUID();

    ListCommentsRequest listCommentsRequest =
        ListCommentsRequest.ofAction(url, refresh, storeCredentials,
            AptoideAccountManager.getAccessToken(), aptoideClientUuid);

    if (storeCredentials.getId() == null) {
      CrashReports.logException(
          new IllegalStateException("Current store credentials does not have a store id"));
    }

    //final long storeId = storeCredentials.getId();
    final long storeId = 0;

    Action1<ListComments> listCommentsAction = (listComments -> {
      if (listComments != null
          && listComments.getDatalist() != null
          && listComments.getDatalist().getList() != null) {
        List<Comment> comments = listComments.getDatalist().getList();
        LinkedList<Displayable> displayables = new LinkedList<>();
        for (int i = 0; i < comments.size(); i++) {
          Comment comment = comments.get(i);
          displayables.add(new CommentDisplayable(
              new StoreComment(comment, showStoreCommentDialogAndSendComment(storeId, comment))));
        }
        this.displayables = new ArrayList<>(comments.size());
        this.displayables.add(new DisplayableGroup(displayables));
        addDisplayables(this.displayables);
      }
    });
    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listCommentsRequest,
            listCommentsAction, errorRequestListener, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  public Observable<Boolean> showStoreCommentDialogAndSendComment(final long storeId,
      @Nullable Comment comment) {
    // optional method
    return Observable.empty();
  }

  // todo adapt instead of mutate
  public static final class StoreComment extends Comment {

    private final Observable<Boolean> onClickReplyAction;

    StoreComment(Comment comment, Observable<Boolean> onClickReplyAction) {
      this.setAdded(comment.getAdded());
      this.setBody(comment.getBody());
      this.setId(comment.getId());
      this.setParentReview(comment.getParentReview());
      this.setUser(comment.getUser());
      this.onClickReplyAction = onClickReplyAction;
    }

    public Observable<Boolean> observeReplySubmission() {
      return onClickReplyAction;
    }
  }

  private void caseListReviews(String url, boolean refresh) {
    ListFullReviewsRequest listFullReviewsRequest =
        ListFullReviewsRequest.ofAction(url, refresh, AptoideAccountManager.getAccessToken(),
            AptoideAccountManager.getUserEmail(),
            new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                DataProvider.getContext()).getAptoideClientUUID());

    Action1<ListFullReviews> listFullReviewsAction = (listFullReviews -> {
      if (listFullReviews != null
          && listFullReviews.getDatalist() != null
          && listFullReviews.getDatalist().getList() != null) {
        List<FullReview> reviews = listFullReviews.getDatalist().getList();
        LinkedList<Displayable> displayables = new LinkedList<>();
        for (int i = 0; i < reviews.size(); i++) {
          FullReview review = reviews.get(i);
          displayables.add(new RowReviewDisplayable(review));
        }
        this.displayables = new ArrayList<>(reviews.size());
        this.displayables.add(new DisplayableGroup(displayables));
        addDisplayables(this.displayables);
      }
    });

    recyclerView.clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), listFullReviewsRequest,
            listFullReviewsAction, errorRequestListener, true);

    recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

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
