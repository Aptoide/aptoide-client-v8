package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerFragmentWithDecorator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ActiveDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CompletedDownloadDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridHeaderDisplayable;
import rx.Subscription;

/**
 * Created by trinkes on 7/15/16.
 */
public class DownloadsFragment extends GridRecyclerFragmentWithDecorator {

	private List<Displayable> activeDisplayablesList = new LinkedList<>();
	private List<Displayable> completedDisplayablesList = new LinkedList<>();
	private Subscription subscription;

	public static DownloadsFragment newInstance() {
		return new DownloadsFragment();
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		super.load(refresh, savedInstanceState);
		DownloadServiceHelper downloadServiceHelper = new DownloadServiceHelper(AptoideDownloadManager.getInstance());
		if (subscription == null || subscription.isUnsubscribed()) {

			subscription = downloadServiceHelper.getAllDownloads().compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW)).doOnNext(downloads -> {
				activeDisplayablesList.clear();
				completedDisplayablesList.clear();
				for (final Download download : downloads) {
					if (download.getOverallDownloadStatus() == Download.PROGRESS || download.getOverallDownloadStatus() == Download.IN_QUEUE) {
						activeDisplayablesList.add(new ActiveDownloadDisplayable(download));
					} else {
						completedDisplayablesList.add(new CompletedDownloadDisplayable(download));
					}
				}
				if (completedDisplayablesList.size() > 0) {
					completedDisplayablesList.add(0, new GridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU.getResString(R
							.string.completed))));
				}
				if (activeDisplayablesList.size() > 0) {
					activeDisplayablesList.add(0, new GridHeaderDisplayable(new GetStoreWidgets.WSWidget().setTitle(AptoideUtils.StringU.getResString(R.string
							.active))));
				}
			}).subscribe(aVoid -> setDisplayables());
		}
	}

	public void setDisplayables() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		displayables.addAll(activeDisplayablesList);
		displayables.addAll(completedDisplayablesList);
		setDisplayables(displayables);
	}
}
