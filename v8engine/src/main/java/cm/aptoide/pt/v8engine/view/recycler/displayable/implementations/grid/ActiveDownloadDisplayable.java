package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import lombok.Setter;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by trinkes on 7/18/16.
 */
public class ActiveDownloadDisplayable extends DisplayablePojo<Download> {

	private DownloadServiceHelper downloadManager;
	@Setter private Action0 onResumeAction;
	@Setter private Action0 onPauseAction;

	public ActiveDownloadDisplayable() {
		super();
	}

	public ActiveDownloadDisplayable(Download pojo, DownloadServiceHelper downloadManager) {
		super(pojo);
		this.downloadManager = downloadManager;
	}

	public ActiveDownloadDisplayable(Download pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.ACTIVE_DOWNLOAD;
	}

	@Override public void onResume() {
		super.onResume();
		if (onResumeAction != null) {
			onResumeAction.call();
		}
	}

	@Override public void onPause() {
		if (onPauseAction != null) {
			onPauseAction.call();
		}
		super.onPause();
	}

	@Override
	public int getViewLayout() {
		return R.layout.active_donwload_row_layout;
	}

	public void pauseInstall() {
		downloadManager.pauseDownload(getPojo().getAppId());
	}

	public Observable<Download> getDownload() {
		return downloadManager.getDownload(getPojo().getAppId());
	}
}
