package cm.aptoide.pt.home.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsFragment extends NavigationTrackFragment implements AppsFragmentView {

  private RecyclerView recyclerView;

  public static AppsFragment newInstance() {
    return new AppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

    attachPresenter(new AppsPresenter(this, new AppsManager(new UpdatesManager(),
        ((AptoideApplication) getContext().getApplicationContext()).getInstallManager()),
        AndroidSchedulers.mainThread(), Schedulers.computation(), CrashReport.getInstance()));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_apps, container, false);
  }

  @Override public void showUpdatesList(List<UpdateApp> list) {

    // TODO: 3/7/18 send it to the adapter
  }

  @Override public void showInstalledApps(List<InstalledApp> installedApps) {
    // TODO: 3/7/18 send it to the adapter
  }
}
