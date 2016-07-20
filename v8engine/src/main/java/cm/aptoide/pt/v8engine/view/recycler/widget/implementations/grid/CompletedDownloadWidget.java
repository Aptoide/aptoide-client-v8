package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({CompletedDownloadDisplayable.class})
public class CompletedDownloadWidget extends Widget<CompletedDownloadDisplayable> {

	private TextView errorText;
	private TextView progressText;
	private ProgressBar downloadProgress;
	private TextView appName;
	private ImageView appIcon;
	private TextView status;
	private ImageView resumeDownloadButton;
	private ImageView cancelDownloadButton;
	private CompositeSubscription subscription;

	public CompletedDownloadWidget(View itemView) {
		super(itemView);
		subscription = new CompositeSubscription();
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
		appName = (TextView) itemView.findViewById(R.id.app_name);
		status = (TextView) itemView.findViewById(R.id.speed);
		//		progressText = (TextView) itemView.findViewById(R.id.progress);
		//		errorText = (TextView) itemView.findViewById(R.id.app_error);
		resumeDownloadButton = (ImageView) itemView.findViewById(R.id.resume_download);
		cancelDownloadButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
	}

	@Override
	public void bindView(CompletedDownloadDisplayable displayable) {
		Download download = displayable.getPojo();
		appName.setText(download.getAppName());
		if (!TextUtils.isEmpty(download.getIcon())) {
			ImageLoader.load(download.getIcon(), appIcon);
		}
		status.setText(download.getStatusName(itemView.getContext()));

		subscription.unsubscribe();
		subscription = new CompositeSubscription();
		subscription.add(RxView.clicks(resumeDownloadButton).flatMap(click -> displayable.resumeDownload(download)).subscribe(download1 -> {
			if (download1.getOverallDownloadStatus() == Download.COMPLETED) {
				ShowMessage.asSnack(resumeDownloadButton, R.string.download_completed, R.string.install, v -> {
					AptoideUtils.SystemU.installApp(download1.getFilesToDownload().get(0).getFilePath());
				});
			}
		}, Throwable::printStackTrace));
		subscription.add(RxView.clicks(cancelDownloadButton).subscribe(click -> displayable.removeDownload(download)));
	}

	@Override
	public void unbindView() {

	}
}
