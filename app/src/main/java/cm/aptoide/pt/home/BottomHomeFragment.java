package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.app.Application;
import cm.aptoide.pt.view.app.FeatureGraphicApplication;
import cm.aptoide.pt.view.fragment.FragmentView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 05/03/2018.
 */

public class BottomHomeFragment extends FragmentView implements HomeView {

  @Inject Home home;
  @Inject HomePresenter presenter;
  private RecyclerView list;
  private BundlesAdapter adapter;
  private PublishSubject<HomeClick> uiEventsListener;
  private PublishSubject<Application> appClickedEvents;
  private LinearLayoutManager layoutManager;
  private DecimalFormat oneDecimalFormatter;
  private View genericError;
  private ProgressBar progressBar;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    uiEventsListener = PublishSubject.create();
    appClickedEvents = PublishSubject.create();
    oneDecimalFormatter = new DecimalFormat("#.#");
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
    getFragmentComponent(savedInstanceState).inject(this);
    list = (RecyclerView) view.findViewById(R.id.bundles_list);
    genericError = view.findViewById(R.id.generic_error);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

    adapter = new BundlesAdapter(new ArrayList<>(), uiEventsListener, oneDecimalFormatter,
        appClickedEvents);
    layoutManager = new LinearLayoutManager(getContext());
    list.setLayoutManager(layoutManager);
    list.setAdapter(adapter);
    attachPresenter(presenter);
  }

  @Override public void showHomeBundles(List<HomeBundle> bundles) {
    adapter.update(bundles);
  }

  @Override public void showLoading() {
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    list.setVisibility(View.VISIBLE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public void showGenericError() {
    this.genericError.setVisibility(View.VISIBLE);
    this.list.setVisibility(View.GONE);
    this.progressBar.setVisibility(View.GONE);
  }

  @Override public Observable<HomeClick> moreClicked() {
    return uiEventsListener.filter(click -> click.getActionType()
        .equals(HomeClick.Type.MORE));
  }

  @Override public Observable<Application> appClicked() {
    return appClickedEvents;
  }

  public List<AppBundle> getFakeBundles() {
    List<Application> tmp = new ArrayList<>();
    String icon = "https://placeimg.com/640/480/any";
    Application aptoide = new Application("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300);
    tmp.add(aptoide);
    Application facebook =
        new Application("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com", 30);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);
    tmp.add(aptoide);
    tmp.add(facebook);

    List<Application> tmp1 = new ArrayList<>();
    FeatureGraphicApplication aptoideFeatureGraphic =
        new FeatureGraphicApplication("Aptoide", icon, 0, 1000, "cm.aptoide.pt", 300, icon);
    tmp.add(aptoideFeatureGraphic);
    FeatureGraphicApplication facebookFeatureGraphic =
        new FeatureGraphicApplication("Facebook", icon, (float) 4.2, 1000, "katana.facebook.com",
            30, icon);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    tmp1.add(aptoideFeatureGraphic);
    tmp1.add(facebookFeatureGraphic);
    AppBundle appBundle =
        new AppBundle("As escolhas do filipe", tmp1, AppBundle.BundleType.EDITORS, null, "");
    AppBundle appBundle1 =
        new AppBundle("piores apps locais", tmp, AppBundle.BundleType.APPS, null, "");
    AppBundle appBundle2 =
        new AppBundle("um pouco melhor apps", tmp, AppBundle.BundleType.APPS, null, "");

    List<AppBundle> appBundles = new ArrayList<>();
    appBundles.add(appBundle);
    appBundles.add(appBundle1);
    appBundles.add(appBundle2);
    return appBundles;
  }
}
