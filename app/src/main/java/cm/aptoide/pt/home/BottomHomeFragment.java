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
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.view.app.Application;
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
  private PublishSubject<GetAdsResponse.Ad> adClickedEvents;
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

    adClickedEvents = PublishSubject.create();
    adapter = new BundlesAdapter(new ArrayList<>(), uiEventsListener, oneDecimalFormatter,
        appClickedEvents, adClickedEvents);
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
    return uiEventsListener;
  }

  @Override public Observable<Application> appClicked() {
    return appClickedEvents;
  }

  @Override public Observable<GetAdsResponse.Ad> adClicked() {
    return adClickedEvents;
  }
}
