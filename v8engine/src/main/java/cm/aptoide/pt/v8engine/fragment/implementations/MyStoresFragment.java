package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.GetStoreWidgetsFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 13/12/2016.
 */

public class MyStoresFragment extends GetStoreWidgetsFragment {

  private static final String TAG = MyStoresFragment.class.getSimpleName();

  public static MyStoresFragment newInstance(Event event, String storeTheme, String tag) {
    // TODO: 28-12-2016 neuro ia saltando um preguito com este null lolz
    Bundle args = buildBundle(event, null, storeTheme, tag, null);
    MyStoresFragment fragment = new MyStoresFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    return super.buildDisplayables(refresh, url).map(list -> addFollowStoreDisplayable(list));
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

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    registerForViewChanges();
  }

  private void registerForViewChanges() {
    AptoideAccountManager accountManager =
        ((V8Engine) getContext().getApplicationContext()).getAccountManager();

    Observable<Account> loginObservable =
        accountManager.accountStatus().doOnNext(__ -> reloadData());

    Observable<List<Store>> storesObservable = storeRepository.getAll().skip(1).doOnNext(__ -> {
      Logger.d(TAG, "Store database changed, reloading...");
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
        }, err -> CrashReport.getInstance().log(err));
  }

  private void reloadData() {
    //
    // this call "magically" gets the store from db (again) and does a WS call to get the
    // most recent subscribed stores, updating the DB (redundancy?) and showing the
    // remote stores
    //
    super.load(false, true, null);
  }
}
