package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({ActiveDownloadDisplayable.class})
public class ActiveDownloadWidget extends Widget<ActiveDownloadDisplayable> {

	private TextView appName;
	private ProgressBar progressBar;
	private TextView downloadStatusTv;
	private ImageView pauseCancelButton;

	public ActiveDownloadWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appName = (TextView) itemView.findViewById(R.id.app_name);
		downloadStatusTv = (TextView) itemView.findViewById(R.id.speed);
		progressBar = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
		pauseCancelButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
	}

	@Override
	public void bindView(ActiveDownloadDisplayable displayable) {
		Download download = displayable.getPojo();
		appName.setText(download.getAppName());
		if (download.getOverallDownloadStatus() == Download.IN_QUEUE) {
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setIndeterminate(false);
			progressBar.setProgress(download.getOverallProgress());
		}
		downloadStatusTv.setText(download.getStatusName(itemView.getContext()));
		RxView.clicks(pauseCancelButton).subscribe(aVoid -> {
			updateDownloadStatus(displayable, download);
		});
	}

	private void updateDownloadStatus(ActiveDownloadDisplayable displayable, Download download) {
		displayable.cancelDownload(download);
	}
}
