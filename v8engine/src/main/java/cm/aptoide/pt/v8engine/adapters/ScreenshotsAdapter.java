/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.ScreenshotsViewerFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;

/**
 * Created by gmartinsribeiro on 01/12/15.
 *
 * code migrated from v7
 */
public class ScreenshotsAdapter extends RecyclerView.Adapter<ScreenshotsAdapter.ScreenshotsViewHolder> {

	private final List<GetAppMeta.Media.Video> videos;
	private final List<GetAppMeta.Media.Screenshot> screenshots;
	private final ArrayList<String> imageUris;

	public ScreenshotsAdapter(GetAppMeta.Media media) {
		this.videos = media.getVideos();
		this.screenshots = media.getScreenshots();

		imageUris = new ArrayList<>(screenshots.size());
		for (GetAppMeta.Media.Screenshot screenshot : screenshots) {
			imageUris.add(screenshot.getUrl());
		}
	}

	@Override
	public ScreenshotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View inflate =
				LayoutInflater
						.from(parent.getContext())
						.inflate(R.layout.row_item_screenshots_gallery, parent, false);

		return new ScreenshotsViewHolder(inflate);
	}

	@Override
	public void onBindViewHolder(ScreenshotsViewHolder holder, int position) {

		if (videos != null && videos.size() > position) {
			// its a video. asSnack placeholder for video
			GetAppMeta.Media.Video item = videos.get(position);
			holder.bindViews(item);
		} else if (screenshots != null && screenshots.size() > position) {
			// its a screenshot. asSnack placeholder for screenshot
			GetAppMeta.Media.Screenshot item = screenshots.get(position);
			int videosOffset = videos!=null? videos.size() : 0;
			holder.bindViews(
					item,
					position - videosOffset,
					imageUris

			);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
		return (videos!=null? videos.size() : 0) + (screenshots!=null? screenshots.size() : 0);
	}

	public static class ScreenshotsViewHolder  extends RecyclerView.ViewHolder {

		private ImageView screenshot;
		private ImageView play_button;
		private FrameLayout media_layout;

		public ScreenshotsViewHolder(View itemView) {
			super(itemView);
			assignViews(itemView);
		}

		protected void assignViews(View itemView) {
			screenshot = (ImageView )itemView.findViewById(R.id.screenshot_image_item);
			play_button = (ImageView )itemView.findViewById(R.id.play_button);
			media_layout = (FrameLayout )itemView.findViewById(R.id.media_layout);
		}

		public void bindViews(GetAppMeta.Media.Video item) {

			Context mainContext = V8Engine.getContext();

			ImageLoader.load(item.getThumbnail(), R.drawable.placeholder_300x300, screenshot);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				media_layout.setForeground(
						mainContext.getResources().getDrawable(R.color.overlay_black, mainContext
								.getTheme())
				);
			}else {
				media_layout.setForeground(
						mainContext.getResources().getDrawable(R.color.overlay_black)
				);
			}

			play_button.setVisibility(View.VISIBLE);

			itemView.setOnClickListener(
					v -> {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mainContext.startActivity(intent);
					}
			);
		}

		public void bindViews(
				GetAppMeta.Media.Screenshot item,
				final int position,
				final ArrayList<String> imagesUris
		) {

			String thumbnail = AptoideUtils.ImageSizeU.screenshotToThumb(item.getUrl(), item.getOrientation());

			media_layout.setForeground(null);
			play_button.setVisibility(View.GONE);

			ImageLoader.load(thumbnail, getPlaceholder(item.getOrientation()), screenshot);

			itemView.setOnClickListener(
					v ->{
						// TODO improve this call
						((FragmentShower) v.getContext()).pushFragmentV4(ScreenshotsViewerFragment.newInstance(imagesUris, position));
					}
			);
		}

		private int getPlaceholder(String orient) {
			int id;
			if(orient != null && orient.equals("portrait")){
				id = R.drawable.placeholder_144x240;
			}else{
				id = R.drawable.placeholder_256x160;
			}
			return id;
		}
	}

}
