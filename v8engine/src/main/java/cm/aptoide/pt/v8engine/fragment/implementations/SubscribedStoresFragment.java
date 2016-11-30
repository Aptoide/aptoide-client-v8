/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.ws.v7.MyStoreRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import com.trello.rxlifecycle.FragmentEvent;
import java.util.LinkedList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by neuro on 11-05-2016.
 */
public class SubscribedStoresFragment extends GridRecyclerFragmentWithDecorator {

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

    MyStoreRequest.of().execute(myStore -> Logger.d(TAG, "this is a test" + myStore));

    StoreAccessor storeAccessor = AccessorFactory.getAccessorFor(Store.class);
    Subscription unManagedSubscription = storeAccessor.getAll()
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(stores -> {
          LinkedList<Displayable> displayables = new LinkedList<>();
          for (Store store : stores) {
            displayables.add(new SubscribedStoreDisplayable(store));
          }
          // Add the final row as a button
          //displayables.add(new AddMoreStoresDisplayable());
          setDisplayables(displayables);
        }, err -> {
          Logger.e(TAG, err);
          CrashReports.logException(err);
        });
  }

  @Override public int getContentViewId() {
    return R.layout.store_recycler_fragment;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    //addStoreButton = (FloatingActionButton) view.findViewById(R.id.fabAddStore);
  }
}
