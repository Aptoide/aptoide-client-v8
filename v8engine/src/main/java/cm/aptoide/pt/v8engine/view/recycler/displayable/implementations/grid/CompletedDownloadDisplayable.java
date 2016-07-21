package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends DisplayablePojo<Download> {

	private DownloadServiceHelper downloadServiceHelper;

	public CompletedDownloadDisplayable() {
		super();
	}

	public CompletedDownloadDisplayable(Download pojo, DownloadServiceHelper downloadServiceHelper) {
		super(pojo);
		this.downloadServiceHelper = downloadServiceHelper;
	}

	public CompletedDownloadDisplayable(Download pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.COMPLETED_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.completed_donwload_row_layout;
	}

	public void removeDownload(Download download) {
		downloadServiceHelper.removeDownload(download.getAppId());
	}

	public Observable<Download> resumeDownload(Download download) {
		return downloadServiceHelper.startDownload(download);
	}
}
