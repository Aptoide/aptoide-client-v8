package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import cm.aptoide.pt.dataprovider.ws.v7.GetUserTimelineRequest;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.v8engine.fragment.GridRecyclerSwipeFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.ArticleDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.listeners.EndlessRecyclerOnScrollListener;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class SocialTimelineFragment extends GridRecyclerSwipeFragment {


	public static SocialTimelineFragment newInstance() {
		SocialTimelineFragment fragment = new SocialTimelineFragment();
		return fragment;
	}

	@Override
	public void load(boolean refresh) {
		GetUserTimelineRequest.of().observe(refresh)
				.<GetUserTimeline>compose(bindUntilEvent(FragmentEvent.PAUSE))
				.flatMapIterable(getUserTimeline -> getUserTimeline.getList())
				.flatMapIterable(timelineItem -> timelineItem.getItems())
				.filter(item -> item instanceof Article)
				.map(item -> itemToDisplayable(item))
				.toList()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						displayables -> setDisplayables((List<? extends Displayable>) displayables),
						throwable -> finishLoading((Throwable) throwable)
				);
	}

	@NonNull
	private Displayable itemToDisplayable(Object item) {
		return new ArticleDisplayable((Article) item);
	}
}
