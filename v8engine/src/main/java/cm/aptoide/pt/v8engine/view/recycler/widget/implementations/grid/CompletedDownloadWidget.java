package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

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

	public CompletedDownloadWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
		appName = (TextView) itemView.findViewById(R.id.app_name);
		status = (TextView) itemView.findViewById(R.id.speed);
		//		downloadProgress = (ProgressBar) itemView.findViewById(R.id.downloading_progress);
		//		progressText = (TextView) itemView.findViewById(R.id.progress);
		//		errorText = (TextView) itemView.findViewById(R.id.app_error);

	}

	@Override
	public void bindView(CompletedDownloadDisplayable displayable) {
		Download download = displayable.getPojo();
		appName.setText(download.getAppName());
		status.setText(download.getStatusName(itemView.getContext()));
	}
}
