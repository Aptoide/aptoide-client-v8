/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.Malware;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SearchDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 01-06-2016.
 */
@Displayables({SearchDisplayable.class})
public class SearchWidget extends Widget<SearchDisplayable> {

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	private TextView name;
	private ImageView icon;
	private TextView downloads;
	private RatingBar ratingBar;
	private ImageView overflow;
	private TextView time;
	private TextView store;
	private ImageView icTrusted;
	private View bottomView;

	public SearchWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		downloads = (TextView) itemView.findViewById(R.id.downloads);
		ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
		overflow = (ImageView) itemView.findViewById(R.id.overflow);
		time = (TextView) itemView.findViewById(R.id.search_time);
		store = (TextView) itemView.findViewById(R.id.search_store);
		icTrusted = (ImageView) itemView.findViewById(R.id.ic_trusted_search);
		bottomView = itemView.findViewById(R.id.bottom_view);
	}

	@Override
	public void bindView(SearchDisplayable displayable) {

		ListSearchApps.SearchAppsApp pojo = displayable.getPojo();

		overflow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final PopupMenu popup = new PopupMenu(view.getContext(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.menu_search_item, popup.getMenu());
				MenuItem menuItem = popup.getMenu().findItem(R.id.versions);
				menuItem.setVisible(pojo.isHasVersions());
				menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						// TODO: 07-06-2016 neuro more versions
//						Intent intent = new Intent(itemView.getContext(), MoreVersionsActivity.class);
						//						intent.putExtra(Constants.PACKAGE_NAME_KEY, appItem.packageName);
//						intent.putExtra(Constants.EVENT_LABEL, appItem.name);
//						itemView.getContext().startActivity(intent);
						return true;
					}
				});
				menuItem = popup.getMenu().findItem(R.id.go_to_store);
				menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						FragmentUtils.replaceFragmentV4(getContext(), StoreFragment.newInstance(pojo.getStore()
								.getName(), pojo.getStore().getAppearance().getTheme()));
						return true;
					}
				});

				popup.show();
			}
		});

		name.setText(pojo.getName());
		String downloadNumber = AptoideUtils.StringU.withSuffix(pojo.getStats()
				.getDownloads()) + " " + bottomView.getContext().getString(R.string.downloads);
		downloads.setText(downloadNumber);

		float avg = pojo.getStats().getRating().getAvg();
		if (avg <= 0) {
			ratingBar.setVisibility(View.GONE);
		} else {
			ratingBar.setVisibility(View.VISIBLE);
			ratingBar.setRating(avg);
		}

		Date modified = pojo.getModified();
		if (modified != null) {
			String timeSinceUpdate = AptoideUtils.DateTimeU.getInstance(itemView.getContext())
					.getTimeDiffAll(itemView.getContext(), modified.getTime());
			if (timeSinceUpdate != null && !timeSinceUpdate.equals("")) {
				time.setText(timeSinceUpdate);
			}
		}

		final StoreThemeEnum theme = StoreThemeEnum.get(pojo.getStore().getAppearance().getTheme());

		Drawable background = bottomView.getBackground();
		if (background instanceof ShapeDrawable) {
			((ShapeDrawable) background).getPaint()
					.setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
		} else if (background instanceof GradientDrawable) {
			((GradientDrawable) background).setColor(itemView.getContext()
					.getResources()
					.getColor(theme.getStoreHeader()));
		}

		background = store.getBackground();
		if (background instanceof ShapeDrawable) {
			((ShapeDrawable) background).getPaint()
					.setColor(itemView.getContext().getResources().getColor(theme.getStoreHeader()));
		} else if (background instanceof GradientDrawable) {
			((GradientDrawable) background).setColor(itemView.getContext()
					.getResources()
					.getColor(theme.getStoreHeader()));
		}

		store.setText(pojo.getStore().getName());
		ImageLoader.load(AptoideUtils.IconSizeU.parseIcon(pojo.getIcon()), icon);

		if (Malware.Rank.TRUSTED.equals(pojo.getFile().getMalware().getRank())) {
			icTrusted.setVisibility(View.VISIBLE);
		} else {
			icTrusted.setVisibility(View.GONE);
		}

		itemView.setOnClickListener(v -> FragmentUtils.replaceFragmentV4(getContext(), AppViewFragment.newInstance
				(pojo.getId(), pojo.getStore().getAppearance().getTheme(), pojo.getStore().getName())));
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
