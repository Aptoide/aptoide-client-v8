package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadsHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by trinkes on 8/17/16.
 */
public class ActiveDownloadsHeaderWidget extends Widget<ActiveDownloadsHeaderDisplayable> {
	
	private Button more;
	private TextView title;

	public ActiveDownloadsHeaderWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView) itemView.findViewById(R.id.title);
		more = (Button) itemView.findViewById(R.id.more);
	}

	@Override
	public void bindView(ActiveDownloadsHeaderDisplayable displayable) {
		title.setText(displayable.getLabel());
		more.setText(R.string.pause_all_downloads);
		more.setVisibility(View.VISIBLE);
		more.setOnClickListener(view -> displayable.getDownloadManager().pauseAllDownloads());
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
