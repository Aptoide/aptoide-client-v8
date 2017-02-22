package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.GetStoreWidgetsFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FollowStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 13/12/2016.
 */

public class MyStoresFragment extends GetStoreWidgetsFragment {

  private static final String TAG = MyStoresFragment.class.getSimpleName();
  private Subscription subscription;

  public static MyStoresFragment newInstance(Event event, String title, String storeTheme,
      String tag) {
    // TODO: 28-12-2016 neuro ia saltando um preguito com este null lolz
    Bundle args = buildBundle(event, null, storeTheme, tag, null);
    MyStoresFragment fragment = new MyStoresFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    Observable<List<Displayable>> widgetList =  super.buildDisplayables(refresh, url).map(displayables -> addFollowStoreDisplayable(displayables));
    //criar método para eliminar o 6º diplayable do tipo stores (followed stores) e adicionar o novo displayable (follow store (novo) ) na posição onde inicialmente existe a primeira followed store (usualmente a apps)
    return widgetList;
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    if (subscription == null || subscription.isUnsubscribed()) {
      subscription = storeRepository.getAll()
          .distinct()
          .observeOn(AndroidSchedulers.mainThread())
          .skip(1)
          .compose(bindUntilEvent(LifecycleEvent.DESTROY_VIEW))
          .subscribe(stores -> {
            Logger.d(TAG, "Store database changed, reloading...");
            super.load(false, true, null);
          });
    }
    super.load(create, refresh, savedInstanceState);
  }

  private List<Displayable> addFollowStoreDisplayable(List<Displayable> displayables) {
    int groupPosition = 0;
    int gridStoreDisplayablePosition = 0;
    for(int i = 0; i < displayables.size(); i++) {
      if (displayables.get(i) instanceof DisplayableGroup) {
        groupPosition = i;
        break;
      }
    }
    DisplayableGroup displayableGroup = (DisplayableGroup) displayables.get(groupPosition);
    List<Displayable> displayableList = displayableGroup.getChildren();
    for(int i = 0; i < displayableList.size(); i++) {
      if (displayableList.get(i) instanceof GridStoreDisplayable) {
        gridStoreDisplayablePosition = i;
        break;
      }
    }
    ((DisplayableGroup) displayables.get(groupPosition)).getChildren().add(gridStoreDisplayablePosition, new FollowStoreDisplayable());
    return displayables;
  }
}
