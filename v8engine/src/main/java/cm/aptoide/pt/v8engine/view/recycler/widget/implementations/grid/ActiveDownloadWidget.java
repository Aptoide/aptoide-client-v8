package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by trinkes on 7/18/16.
 */
@Displayables({ActiveDownloadDisplayable.class})
public class ActiveDownloadWidget extends Widget<ActiveDownloadDisplayable> {

	private TextView appName;
	private ProgressBar progressBar;
	private TextView downloadSpeedTv;
	private TextView downloadProgressTv;
	private ImageView pauseCancelButton;
	private ImageView appIcon;
	private CompositeSubscription subscriptions;
	private ActiveDownloadDisplayable displayable;

	public ActiveDownloadWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appName = (TextView) itemView.findViewById(R.id.app_name);
		downloadSpeedTv = (TextView) itemView.findViewById(R.id.speed);
		downloadProgressTv = (TextView) itemView.findViewById(R.id.progress);
		progressBar = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
		pauseCancelButton = (ImageView) itemView.findViewById(R.id.pause_cancel_button);
		appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
	}

	@Override
	public void bindView(ActiveDownloadDisplayable displayable) {
		this.displayable = displayable;
		displayable.setOnPauseAction(() -> onViewDetached());
		displayable.setOnResumeAction(() -> onViewAttached());
	}

	@Override public void onViewAttached() {
		if (subscriptions == null || subscriptions.isUnsubscribed()) {
			subscriptions = new CompositeSubscription();
		}
		subscriptions.add(
				RxView.clicks(pauseCancelButton).subscribe(click -> displayable.pauseInstall()));
		subscriptions.add(displayable.getDownload()
				.observeOn(Schedulers.computation())
				.distinctUntilChanged()
				.map(download -> download)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(this::updateUi));
	}

	@Override
	public void onViewDetached() {
		if (subscriptions != null && !subscriptions.isUnsubscribed()) {
			subscriptions.unsubscribe();
		}
	}

	private Void updateUi(Download download) {
		appName.setText(download.getAppName());
		if (!TextUtils.isEmpty(download.getIcon())) {
			ImageLoader.load(download.getIcon(), appIcon);
		}
		if (download.getOverallDownloadStatus() == Download.IN_QUEUE) {
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setIndeterminate(false);
			progressBar.setProgress(download.getOverallProgress());
		}
		downloadProgressTv.setText(download.getOverallProgress() + "%");
		downloadSpeedTv.setText(String.valueOf(AptoideUtils.StringU.formatBits((long) download.getDownloadSpeed())));
		return null;
	}
}