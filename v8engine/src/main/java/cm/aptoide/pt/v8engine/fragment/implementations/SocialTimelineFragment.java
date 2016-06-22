package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.Feature;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class SocialTimelineFragment extends GridRecyclerSwipeFragment {

	private DateCalculator dateCalculator;

	public static SocialTimelineFragment newInstance() {
		SocialTimelineFragment fragment = new SocialTimelineFragment();
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateCalculator = new DateCalculator();
	}

	@Override
	public void load(boolean refresh) {
		GetUserTimelineRequest.of().observe(refresh)
				.<GetUserTimeline>compose(bindUntilEvent(FragmentEvent.PAUSE))
				.flatMapIterable(getUserTimeline -> getUserTimeline.getList())
				.flatMapIterable(timelineItem -> timelineItem.getItems())
				.filter(item -> (item instanceof Article || item instanceof Feature || item instanceof StoreLatestApps))
				.map(item -> itemToDisplayable(item, dateCalculator))
				.toList()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						displayables -> setDisplayables((List<? extends Displayable>) displayables),
						throwable -> finishLoading((Throwable) throwable)
				);
	}

	@NonNull
	private Displayable itemToDisplayable(Object item, DateCalculator dateCalculator) {

		if (item instanceof Article) {
			return new ArticleDisplayable((Article) item, dateCalculator);
		} else if (item instanceof Feature) {
			return new FeatureDisplayable((Feature) item, dateCalculator);
		} else if (item instanceof StoreLatestApps) {
			return new StoreLatestAppsDisplayable((StoreLatestApps) item, dateCalculator);
		}
		throw new IllegalArgumentException("Only Articles, Features and Store Latest Apps supported.");
	}
}
