package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends DisplayablePojo<Download> {

	private DownloadServiceHelper downloadServiceHelper;

	public ActiveDownloadDisplayable() {
		super();
	}

	public ActiveDownloadDisplayable(Download pojo, DownloadServiceHelper downloadServiceHelper) {
		super(pojo);
		this.downloadServiceHelper = downloadServiceHelper;
	}

	public ActiveDownloadDisplayable(Download pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.ACTIVE_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.active_donwload_row_layout;
	}

	public void pauseDownload(Download download) {
		downloadServiceHelper.pauseDownload(download.getAppId());
	}
}
