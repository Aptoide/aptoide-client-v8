package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.fragment.FragmentView;
import java.util.ArrayList;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/03/2018.
 */

public class BottomHomeFragment extends FragmentView implements HomeView {

  private RecyclerView list;

  private BundlesAdapter adapter;

  private PublishSubject<AppBundle> uiEventsListener;
  private LinearLayoutManager layoutManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiEventsListener = PublishSubject.create();
  }

  @Override public void onDestroy() {
    list = null;
    adapter = null;
    uiEventsListener = null;
    layoutManager = null;
    super.onDestroy();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    list = (RecyclerView) view.findViewById(R.id.bundles_list);
    adapter = new BundlesAdapter(new ArrayList<>(), uiEventsListener);
    layoutManager = new LinearLayoutManager(getContext());
    list.setLayoutManager(layoutManager);
    list.setAdapter(adapter);
    attachPresenter(new HomePresenter(this, new Home()));
  }

  @Override public void showHomeBundles(List<AppBundle> bundles) {
    adapter.add(getFakeBundles());
  }

  public List<AppBundle> getFakeBundles() {
    List<Application> tmp = new ArrayList<>();
    String icon = "https://placeimg.com/640/480/any";
    tmp.add(new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300));
    tmp.add(new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30));
    tmp.add(new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300));
    tmp.add(new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30));
    tmp.add(new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300));
    tmp.add(new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30));
    tmp.add(new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300));
    tmp.add(new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30));

    AppBundle appBundle = new AppBundle("Test bundle", tmp);
    AppBundle appBundle1 = new AppBundle("Test bundle1", tmp);
    AppBundle appBundle2 = new AppBundle("Test bundle2", tmp);

    List<AppBundle> appBundles = new ArrayList<>();
    appBundles.add(appBundle);
    appBundles.add(appBundle1);
    appBundles.add(appBundle2);
    return appBundles;
  }
}
