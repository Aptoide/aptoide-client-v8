package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadNotFoundException;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends Widget<AppUpdateDisplayable> {

	private TextView appName;
	private TextView appVersion;
	private ImageView appIcon;
	private TextView appUpdate;

	private Button updateButton;
	private Download download;
	private Subscription downloadSubscription;
	private TextView errorText;

	public AppUpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appName = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update_name);
		appIcon = (ImageView)itemView.findViewById(R.id.displayable_social_timeline_app_update_icon);
		appVersion = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update_version);
		updateButton = (Button) itemView.findViewById(R.id.displayable_social_timeline_app_update_button);
		errorText = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_error);
		appUpdate = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update);
	}

	@Override
	public void bindView(AppUpdateDisplayable displayable) {

		appName.setText(displayable.getAppTitle(getContext()));
		appUpdate.setText(displayable.getHasUpdateText(getContext()));
		appVersion.setText(displayable.getVersionText(getContext()));

		ImageLoader.load(displayable.getAppIconUrl(), appIcon);

		downloadSubscription = Observable.merge(
				displayable.getDownload()
						.onErrorResumeNext(throwable -> treatDownloadNotFoundError(throwable)),
				RxView.clicks(updateButton)
						.flatMap(click -> displayable.startDownload())
						.retry())
						.distinctUntilChanged()
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(download -> updateDownloadStatus(displayable, download), throwable -> showDownloadError(displayable, throwable));
	}

	@Override
	public void unbindView() {
		if (downloadSubscription != null) {
			downloadSubscription.unsubscribe();
		}
	}

	@NonNull
	private Observable<Download> treatDownloadNotFoundError(Throwable throwable) {
		if (throwable instanceof DownloadNotFoundException) {
			return Observable.just(new Download());
		}
		return Observable.error(throwable);
	}

	private void showDownloadError(AppUpdateDisplayable displayable, Throwable throwable) {
		errorText.setText(displayable.getUpdateErrorText(getContext()));
		errorText.setVisibility(View.VISIBLE);
		updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app, 0, 0, 0);
		updateButton.setText(displayable.getUpdateAppText(getContext()));
	}

	private void updateDownloadStatus(AppUpdateDisplayable displayable, Download download) {
		errorText.setVisibility(View.GONE);
		switch (download.getOverallDownloadStatus()) {
			case Download.PROGRESS:
			case Download.WARN:
			case Download.BLOCK_COMPLETE:
			case Download.CONNECTED:
			case Download.RETRY:
			case Download.STARTED:
			case Download.PENDING:
			case Download.IN_QUEUE:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				updateButton.setText(displayable.getUpdatingText(getContext()));
				break;
			case Download.COMPLETED:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				updateButton.setText(displayable.getCompletedText(getContext()));
				break;
			case Download.ERROR:
				showDownloadError(displayable, null);
				break;
			case Download.FILE_MISSING:
			case Download.INVALID_STATUS:
			case Download.NOT_DOWNLOADED:
			case Download.PAUSED:
			default:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app, 0, 0, 0);
				updateButton.setText(displayable.getUpdateAppText(getContext()));
				break;
		}
	}
}
