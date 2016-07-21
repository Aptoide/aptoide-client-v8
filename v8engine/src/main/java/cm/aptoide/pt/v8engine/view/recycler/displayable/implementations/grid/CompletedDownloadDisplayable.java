package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.widget.ImageView;

import java.io.File;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import rx.Observable;

/**
 * Created by trinkes on 7/15/16.
 */
public class CompletedDownloadDisplayable extends DisplayablePojo<Download> {

	private DownloadServiceHelper downloadServiceHelper;
	private InstallManager installManager;

	public CompletedDownloadDisplayable() {
		super();
	}

	public CompletedDownloadDisplayable(Download pojo, DownloadServiceHelper downloadServiceHelper, InstallManager installManager) {
		super(pojo);
		this.downloadServiceHelper = downloadServiceHelper;
		this.installManager = installManager;
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

	public Observable<Download> resumeDownload(Context context, Download download, ImageView resumeDownloadButton) {
		return downloadServiceHelper.startDownload(download);
	}
}
