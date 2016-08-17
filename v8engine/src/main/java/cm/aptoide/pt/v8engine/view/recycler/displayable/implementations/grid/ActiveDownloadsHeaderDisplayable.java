package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by trinkes on 8/17/16.
 */
public class ActiveDownloadsHeaderDisplayable extends Displayable {

	private static final String TAG = ActiveDownloadsHeaderDisplayable.class.getSimpleName();
	@Getter private String label;
	@Getter private DownloadServiceHelper downloadManager;

	public ActiveDownloadsHeaderDisplayable() {
	}

	public ActiveDownloadsHeaderDisplayable(String label, DownloadServiceHelper downloadManager) {
		this.label = label;
		this.downloadManager = downloadManager;
		Logger.d(TAG, "ActiveDownloadsHeaderDisplayable() called with: " + "label = [" + label + "], downloadManager = [" + downloadManager + "]");
	}

	@Override
	public Type getType() {
		return Type.ACTIVE_DOWNLOAD_HEADER;
	}

	@Override
	public int getViewLayout() {
		return R.layout.active_downloads_header_row;
	}
}
