/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.util.DataproviderUtils;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.MyStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.WSWidgetsUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.util.StoreUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablesFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoresFragment extends GridRecyclerSwipeFragment {

  private static final String TAG = SubscribedStoresFragment.class.getName();

  //private FloatingActionButton addStoreButton;

  public static SubscribedStoresFragment newInstance() {
    SubscribedStoresFragment fragment = new SubscribedStoresFragment();
    return fragment;
  }

  @Override public void setupViews() {
    super.setupViews();

    //addStoreButton.setOnClickListener(new View.OnClickListener() {
    //
    //  public void onClick(View v) {
    //    new AddStoreDialog().show(((FragmentActivity) getContext()).getSupportFragmentManager(),
    //        "addStoreDialog");
    //  }
    //});
    /*RxView.clicks(addStoreButton).subscribe(view ->{
      new AddStoreDialog().show(((FragmentActivity) getContext())
					.getSupportFragmentManager(), "addStoreDialog");
		});*/

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        /*Log.d("lou",dy+"");
        if (dy > 0 && addStoreButton.getTranslationY() > 0 && addStoreButton.isShown()) {
					addStoreButton.setTranslationY(addStoreButton.getTranslationY()+dy);
				}*/
      }
    });
  }

  @Override public void reload() {
    super.reload();
    loadStores(true);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {

    //Observable<RealmResults<Store>> realmResultsObservable =
    //    DeprecatedDatabase.StoreQ.getAll(realm).asObservable();
    //realmResultsObservable.compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(stores -> {
    //  LinkedList<Displayable> displayables = new LinkedList<>();
    //  for (Store store : stores) {
    //    displayables.add(new SubscribedStoreDisplayable(store));
    //  }
    //  // Add the final row as a button
    //  //displayables.add(new AddMoreStoresDisplayable());
    //  setDisplayables(displayables);
    //});
    if (!refresh) {
      loadStores(false);
    }

    //StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    //Subscription unManagedSubscription = storeAccessor.getAll()
    //    .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
    //    .observeOn(AndroidSchedulers.mainThread())
    //    .subscribe(stores -> {
    //      LinkedList<Displayable> displayables = new LinkedList<>();
    //      for (Store store : stores) {
    //        displayables.add(new SubscribedStoreDisplayable(store));
    //      }
    //      // Add the final row as a button
    //      //displayables.add(new AddMoreStoresDisplayable());
    //      setDisplayables(displayables);
    //    }, err -> {
    //      Logger.e(TAG, err);
    //      CrashReports.logException(err);
    //    });
  }

  private void loadStores(boolean refresh) {
    MyStoreRequest.of().observe(refresh).observeOn(Schedulers.io()).subscribe(myStore -> {

      List displayables = new LinkedList();
      List<GetStoreWidgets.WSWidget> list = myStore.getWidgets().getDatalist().getList();
      // Load sub nodes
      CountDownLatch countDownLatch = new CountDownLatch(list.size());

      Observable.from(list)
          .observeOn(Schedulers.io())
          .forEach(wsWidget -> WSWidgetsUtils.loadInnerNodes(wsWidget,
              wsWidget.getView() != null ? StoreUtils.getStoreCredentialsFromUrl(wsWidget.getView())
                  : new BaseRequestWithStore.StoreCredentials(), countDownLatch, refresh,
              throwable -> countDownLatch.countDown(), AptoideAccountManager.getAccessToken(),
              AptoideAccountManager.getUserEmail(),
              new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
                  DataProvider.getContext()).getAptoideClientUUID(),
              DataproviderUtils.AdNetworksUtils.isGooglePlayServicesAvailable(
                  V8Engine.getContext()), DataProvider.getConfiguration().getPartnerId()));

      try {
        countDownLatch.await(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      displayables = DisplayablesFactory.parse(myStore.getWidgets(),
          V8Engine.getConfiguration().getDefaultTheme());

      setDisplayables(displayables);
    });
  }

  //@Override public int getContentViewId() {
  //  return R.layout.store_recycler_fragment;
  //}

  private Observable<GetStore> caseGetStore(String url,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean refresh, String storeTheme) {

    return GetStoreRequest.ofAction(url, storeCredentials, AptoideAccountManager.getAccessToken(),
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID())
        .observe(refresh)
        .observeOn(Schedulers.io());
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    //addStoreButton = (FloatingActionButton) view.findViewById(R.id.fabAddStore);
  }
}
