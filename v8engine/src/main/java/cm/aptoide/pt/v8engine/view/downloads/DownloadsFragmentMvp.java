package cm.aptoide.pt.v8engine.view.downloads;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import cm.aptoide.pt.v8engine.install.InstallerFactory;
import cm.aptoide.pt.v8engine.presenter.DownloadsPresenter;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.presenter.DownloadsView;
import cm.aptoide.pt.v8engine.view.custom.DividerItemDecoration;
import java.util.List;

public class DownloadsFragmentMvp extends FragmentView implements DownloadsView {

  private DownloadsAdapter adapter;
  private View noDownloadsView;

  public static DownloadsFragmentMvp newInstance() {
    return new DownloadsFragmentMvp();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recycler_fragment_downloads, container, false);

    RecyclerView downloadsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    downloadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    final int pixelDimen = AptoideUtils.ScreenU.getPixelsForDip(5);
    final DividerItemDecoration decor =
        new DividerItemDecoration(pixelDimen, DividerItemDecoration.ALL);
    downloadsRecyclerView.addItemDecoration(decor);

    adapter = new DownloadsAdapter(getContext());
    downloadsRecyclerView.setAdapter(adapter);

    noDownloadsView = view.findViewById(R.id.no_apps_downloaded);

    InstallManager installManager = new InstallManager(AptoideDownloadManager.getInstance(),
        new InstallerFactory().create(getContext(), InstallerFactory.ROLLBACK));
    attachPresenter(
        new DownloadsPresenter(this, RepositoryFactory.getDownloadRepository(), installManager),
        savedInstanceState);

    return view;
  }

  @UiThread @Override public void showActiveDownloads(List<Download> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setActiveDownloads(downloads);
  }

  @UiThread @Override public void showStandByDownloads(List<Download> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setStandByDownloads(downloads);
  }

  @UiThread @Override public void showCompletedDownloads(List<Download> downloads) {
    setEmptyDownloadVisible(false);
    adapter.setCompletedDownloads(downloads);
  }

  @UiThread @Override public void showEmptyDownloadList() {
    setEmptyDownloadVisible(true);
    adapter.clearAll();
  }

  @UiThread private void setEmptyDownloadVisible(boolean visible) {
    if (noDownloadsView.getVisibility() == View.GONE && visible) {
      noDownloadsView.setVisibility(View.VISIBLE);
    }

    if (noDownloadsView.getVisibility() == View.VISIBLE && !visible) {
      noDownloadsView.setVisibility(View.GONE);
    }
  }
}
