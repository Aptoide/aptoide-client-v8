package cm.aptoide.pt.view.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.BackButtonFragment;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 17/10/2017.
 */

public class ListStoreAppsFragment extends BackButtonFragment implements ListStoreAppsView {

  public static final String STORE_ID = "cm.aptoide.pt.ListStoreAppsFragment.storeId";
  private ListStoreAppsAdapter adapter;
  private long storeId;
  private PublishSubject<Application> appClicks;

  public static Fragment newInstance(long storeId) {
    Bundle args = new Bundle();
    args.putLong(STORE_ID, storeId);
    Fragment fragment = new ListStoreAppsFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    appClicks = PublishSubject.create();
    storeId = getArguments().getLong(STORE_ID);
    AptoideApplication applicationContext =
        (AptoideApplication) getContext().getApplicationContext();
    attachPresenter(new ListStoreAppsPresenter(this, storeId, AndroidSchedulers.mainThread(),
        new AppCenter(new AppService(new StoreCredentialsProviderImpl(
            AccessorFactory.getAccessorFor(
                ((AptoideApplication) getContext().getApplicationContext()
                    .getApplicationContext()).getDatabase(), Store.class)),
            applicationContext.getBodyInterceptorPoolV7(), applicationContext.getDefaultClient(),
            WebService.getDefaultConverter(), applicationContext.getTokenInvalidator(),
            applicationContext.getDefaultSharedPreferences())), CrashReport.getInstance(),
        getFragmentNavigator()), savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    adapter = new ListStoreAppsAdapter(new ArrayList<>(), appClicks);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getSpanSize(3)));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.list_store_apps_fragment_layout, container, false);
  }

  @Override public void setApps(List<Application> appsList) {
    adapter.setApps(appsList);
  }

  @Override public Observable<Application> getAppClick() {
    return appClicks;
  }

  public int getSpanSize(int defaultSpan) {
    return (int) (AptoideUtils.ScreenU.getScreenWidthInDip(
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
        getContext().getResources()) / AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * defaultSpan);
  }
}
