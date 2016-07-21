package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.widget.ImageView;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

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

	public void resumeDownload(Download download, ImageView resumeDownloadButton) {
		downloadServiceHelper.startDownload(download).first().subscribe(download1 -> {
			if (download1.getOverallDownloadStatus() == Download.COMPLETED) {
				ShowMessage.asSnack(resumeDownloadButton, R.string.download_completed, R.string.install, v -> {
					AptoideUtils.SystemU.installApp(download1.getFilesToDownload().get(0).getFilePath());
				});
			}
		}, Throwable::printStackTrace);
	}
}
