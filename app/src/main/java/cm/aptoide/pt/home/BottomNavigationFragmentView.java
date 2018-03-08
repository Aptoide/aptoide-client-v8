package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.view.fragment.FragmentView;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 03/03/2018.
 */

public class BottomNavigationFragmentView extends FragmentView
    implements AptoideBottomNavigationView {

  protected BottomNavigationView bottomNavigationView;
  private PublishSubject<Integer> navigationSubject;
  private MyStoresFragment myStoresFragment;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    navigationSubject = PublishSubject.create();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    navigationSubject = null;
    myStoresFragment = null;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });

    attachPresenter(new BottomNavPresenter(this));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_main, container, false);
  }

  @Override public Observable<Integer> navigationEvent() {
    return navigationSubject;
  }

  @Override public void showFragment(Integer menuItemId) {
    Fragment selectedFragment = null;
    switch (menuItemId) {
      case R.id.action_home:
        selectedFragment = new BottomHomeFragment();
        myStoresFragment = null;
        break;
      case R.id.action_search:
        myStoresFragment = null;
        break;
      case R.id.action_stores:
        selectedFragment = getMyStoresFragment();
        break;
      case R.id.action_apps:
        selectedFragment = new BottomHomeFragment();
        myStoresFragment = null;
        break;
    }
    if (selectedFragment != null) {
      FragmentNavigator fragmentChildNavigator =
          getFragmentChildNavigator(R.id.fragment_placeholder);
      fragmentChildNavigator.navigateTo(selectedFragment, true);
    }
  }

  private Event getStoreEvent() {
    Event event = new Event();
    event.setAction("https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=stores");
    event.setData(null);
    event.setName(Event.Name.myStores);
    event.setType(Event.Type.CLIENT);
    return event;
  }

  private MyStoresFragment getMyStoresFragment() {
    boolean createNew = false;
    if (myStoresFragment != null) {
      try {
        myStoresFragment.scrollToTop();
      } catch (Exception exception) {
        createNew = true;
      }
    }
    if (myStoresFragment == null || createNew) {
      myStoresFragment =
          MyStoresFragment.newInstance(getStoreEvent(), null, "stores", StoreContext.home);
      return myStoresFragment;
    }
    return null;
  }
}
