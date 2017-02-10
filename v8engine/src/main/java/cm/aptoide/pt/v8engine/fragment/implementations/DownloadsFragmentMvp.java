package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.adapters.DownloadsAdapter;
import cm.aptoide.pt.v8engine.fragment.FragmentView;
import cm.aptoide.pt.v8engine.presenter.DownloadsPresenter;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.DownloadsView;
import java.util.List;

public class DownloadsFragmentMvp extends FragmentView implements DownloadsView {

  private DownloadsAdapter adapter;

  public static DownloadsFragmentMvp newInstance() {
    return new DownloadsFragmentMvp();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.recycler_fragment_downloads, null, false);

    RecyclerView downloadsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    downloadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    adapter = new DownloadsAdapter(getContext());
    downloadsRecyclerView.setAdapter(adapter);

    attachPresenter(new DownloadsPresenter(this, RepositoryFactory.getDownloadRepository()),
        savedInstanceState);

    return view;
  }

  @Override public void showActiveDownloads(List<Download> downloads) {
    adapter.setActiveDownloads(downloads);
  }

  @Override public void showStandByDownloads(List<Download> downloads) {
    adapter.setStandByDownloads(downloads);
  }

  @Override public void showCompletedDownloads(List<Download> downloads) {
    adapter.setCompletedDownloads(downloads);
  }

  @Override public void showEmptyDownloadList() {
    adapter.clearAll();
  }
}
