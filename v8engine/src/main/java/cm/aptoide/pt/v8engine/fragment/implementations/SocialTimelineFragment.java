package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.util.Date;
import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.listapp.File;
import cm.aptoide.pt.model.v7.timeline.AppUpdateTimelineItem;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.TimelineItem;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class SocialTimelineFragment extends GridRecyclerSwipeFragment {

	public static final int SEARCH_LIMIT = 2;
	private SpannableFactory spannableFactory;
	private DateCalculator dateCalculator;
	private int offset;

	public static SocialTimelineFragment newInstance() {
		SocialTimelineFragment fragment = new SocialTimelineFragment();
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateCalculator = new DateCalculator();
		spannableFactory = new SpannableFactory();
	}

	@Override
	public void load(boolean refresh) {
		GetUserTimelineRequest.of(SEARCH_LIMIT, offset).observe(refresh)
				.<GetUserTimeline>compose(bindUntilEvent(FragmentEvent.PAUSE))
				.filter(item -> item.getDatalist() != null? setOffset(item.getDatalist().getNext()): false)
				.flatMapIterable(getUserTimeline -> getListWithMockedAppUpdate(getUserTimeline))
				.filter(timelineItem -> timelineItem != null)
				.map(timelineItem -> timelineItem.getData())
				.filter(item -> (item instanceof Article || item instanceof Feature || item instanceof
						StoreLatestApps || item instanceof App))
				.map(item -> itemToDisplayable(item, dateCalculator, spannableFactory))
				.toList()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						displayables -> updateTimeline((List<? extends Displayable>) displayables),
						throwable -> finishLoading((Throwable) throwable)
				);
	}

	private void updateTimeline(List<? extends Displayable> displayables) {
		addDisplayables(0, displayables);
		recyclerView.smoothScrollToPosition(0);
	}

	private List<TimelineItem> getListWithMockedAppUpdate(GetUserTimeline getUserTimeline) {
		App app = new App();
		app.setId(19347406);
		app.setName("Clash of Clans");
		File file = new File();
		file.setVername("8.3332.14");
		app.setFile(file);
		app.setUpdated(new Date());
		app.setIcon("http://cdn6.aptoide.com/imgs/a/a/e/aae8e02f62bf4a4008769ddb14b8fd89_icon_96x96.png");
		getUserTimeline.getDatalist().getList().add(new AppUpdateTimelineItem(app));
		return getUserTimeline.getDatalist().getList();
	}

	@NonNull
	private Displayable itemToDisplayable(Object item, DateCalculator dateCalculator, SpannableFactory spannableFactory) {

		if (item instanceof Article) {
			return ArticleDisplayable.from((Article) item, dateCalculator, spannableFactory);
		} else if (item instanceof Feature) {
			return FeatureDisplayable.from((Feature) item, dateCalculator);
		} else if (item instanceof StoreLatestApps) {
			return StoreLatestAppsDisplayable.from((StoreLatestApps) item, dateCalculator);
		} else if (item instanceof App) {
			return AppUpdateDisplayable.fromApp((App) item, spannableFactory);
		}
		throw new IllegalArgumentException("Only articles, features, store latest apps and app updates supported.");
	}

	public boolean setOffset(int offset) {
		this.offset = offset;
		return true;
	}
}
