package cm.aptoide.pt.v8engine.view.downloads;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.download.DownloadEventConverter;
import cm.aptoide.pt.v8engine.download.InstallEventConverter;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadWidget;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.active.ActiveDownloadsHeaderWidget;
import cm.aptoide.pt.v8engine.view.downloads.completed.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.downloads.completed.CompletedDownloadWidget;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.store.StoreGridHeaderWidget;
import java.util.ArrayList;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<Widget<? extends Displayable>> {

  private final InstallManager installManager;
  private final Analytics analytics;
  private final InstallEventConverter installConverter;
  private final DownloadEventConverter downloadConverter;
  private final List<Download> activeDownloads;
  private final List<Download> standByDownloads;
  private final List<Download> completedDownloads;

  public DownloadsAdapter(InstallEventConverter installConverter,
      DownloadEventConverter downloadConverter, InstallManager installManager,
      Analytics analytics) {
    this.activeDownloads = new ArrayList<>();
    this.standByDownloads = new ArrayList<>();
    this.completedDownloads = new ArrayList<>();
    this.installManager = installManager;
    this.analytics = analytics;
    this.installConverter = installConverter;
    this.downloadConverter = downloadConverter;
  }

  public void setActiveDownloads(List<Download> downloads) {
    this.activeDownloads.clear();
    this.activeDownloads.addAll(downloads);
    this.notifyDataSetChanged();
  }

  public void setStandByDownloads(List<Download> downloads) {
    this.standByDownloads.clear();
    this.standByDownloads.addAll(downloads);
    this.notifyDataSetChanged();
  }

  public void setCompletedDownloads(List<Download> downloads) {
    this.completedDownloads.clear();
    this.completedDownloads.addAll(downloads);
    this.notifyDataSetChanged();
  }

  @Override public Widget onCreateViewHolder(ViewGroup parent, int viewType) {
    ItemViewType itemViewType = ItemViewType.values()[viewType];

    View view;
    switch (itemViewType) {
      case Header: {
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.displayable_grid_header, parent, false);
        return new StoreGridHeaderWidget(view);
      }
      case ActiveDownloadHeader: {
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.active_downloads_header_row, parent, false);
        return new ActiveDownloadsHeaderWidget(view);
      }
      case ActiveDownload: {
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.active_download_row_layout, parent, false);
        return new ActiveDownloadWidget(view);
      }
      case CompletedDownload:
      case StandByDownload: {
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.completed_donwload_row_layout, parent, false);
        return new CompletedDownloadWidget(view);
      }
    }
    return null;
  }

  @Override public void onBindViewHolder(Widget holder, int position) {
    final ItemViewType itemViewType = ItemViewType.values()[getItemViewType(position)];
    switch (itemViewType) {
      case Header: {
        bindHeader(holder, position);
        break;
      }

      case ActiveDownloadHeader: {
        bindActiveDownloadHeader(holder);
        break;
      }

      case ActiveDownload: {
        // remove active download header
        position -= 1;
        bindActiveDownload(holder, activeDownloads.get(position));
        break;
      }

      case StandByDownload: {
        // remove active downloads (with header)
        position -= !activeDownloads.isEmpty() ? (activeDownloads.size() + 1) : 0;
        // remove stand by header
        position -= 1;
        bindStandByDownload(holder, standByDownloads.get(position));
        break;
      }

      case CompletedDownload: {
        // remove active downloads (with header) and active by header
        // remove standby downloads (with header) and stand by header
        position -= !activeDownloads.isEmpty() ? (activeDownloads.size() + 1) : 0;
        position -= !standByDownloads.isEmpty() ? (standByDownloads.size() + 1) : 0;
        position -= 1; // remove completed downloads header
        bindCompletedDownload(holder, completedDownloads.get(position));
        break;
      }
    }
  }

  @Override public int getItemViewType(int position) {
    // one of four according to position: (see enum ItemViewType)
    // - group header
    // - active download view holder
    // - stand by download view holder
    // - completed download view holder

    // check if it is an active download (or active download header)
    if (!activeDownloads.isEmpty()) {
      if (position == 0) {
        return ItemViewType.ActiveDownloadHeader.ordinal();
      }

      if (position <= activeDownloads.size()) {
        return ItemViewType.ActiveDownload.ordinal();
      }
    }

    // check if it is a stand by download (or stand by download header)
    position -= activeDownloads.isEmpty() ? 0 : 1 + activeDownloads.size();

    if (!standByDownloads.isEmpty()) {
      if (position == 0) {
        return ItemViewType.Header.ordinal();
      }

      if (position <= standByDownloads.size()) {
        return ItemViewType.StandByDownload.ordinal();
      }
    }

    // check if it is a completed download (or completed download header)
    position -= standByDownloads.isEmpty() ? 0 : 1 + standByDownloads.size();

    if (!completedDownloads.isEmpty()) {
      if (position == 0) {
        return ItemViewType.Header.ordinal();
      }
    }

    return ItemViewType.CompletedDownload.ordinal();
  }

  @Override public int getItemCount() {
    int nrActiveDownloads =
        activeDownloads != null && !activeDownloads.isEmpty() ? activeDownloads.size() + 1 : 0;

    int nrStandByDownloads =
        standByDownloads != null && !standByDownloads.isEmpty() ? standByDownloads.size() + 1 : 0;

    int nrCompletedDownloads =
        completedDownloads != null && !completedDownloads.isEmpty() ? completedDownloads.size() + 1
            : 0;

    return nrActiveDownloads + nrStandByDownloads + nrCompletedDownloads;
  }

  @Override public void onViewRecycled(Widget holder) {
    holder.unbindView();
    super.onViewRecycled(holder);
  }

  private void bindHeader(Widget holder, int position) {
    // discover if it's the header for stand by or completed downloads
    position -= (activeDownloads.isEmpty() ? 0 : activeDownloads.size() + 1);

    StoreGridHeaderWidget header = (StoreGridHeaderWidget) holder;
    if (position < standByDownloads.size()) {
      // is the header from stand by downloads
      header.bindView(new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(
          AptoideUtils.StringU.getResString(R.string.stand_by))));
    } else {
      // is the header from completed downloads
      header.bindView(new StoreGridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(
          AptoideUtils.StringU.getResString(R.string.completed))));
    }
  }

  private void bindActiveDownloadHeader(Widget holder) {
    holder.internalBindView(
        new ActiveDownloadsHeaderDisplayable(AptoideUtils.StringU.getResString(R.string.active),
            installManager));
  }

  private void bindActiveDownload(Widget holder, Download download) {
    holder.internalBindView(new ActiveDownloadDisplayable(download, installManager));
  }

  private void bindStandByDownload(Widget holder, Download download) {
    holder.internalBindView(
        new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
            installConverter));
  }

  private void bindCompletedDownload(Widget holder, Download download) {
    holder.internalBindView(
        new CompletedDownloadDisplayable(download, installManager, downloadConverter, analytics,
            installConverter));
  }

  public void clearAll() {
    activeDownloads.clear();
    standByDownloads.clear();
    completedDownloads.clear();
  }

  private enum ItemViewType {
    Header, ActiveDownloadHeader, ActiveDownload, StandByDownload, CompletedDownload
  }
}
